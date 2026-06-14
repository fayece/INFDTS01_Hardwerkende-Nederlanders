package nl.hardwerkendenederlanders.hrcms.configuration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.PermissionService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.method.HandlerMethod;

class PermissionInterceptorTest {

    // region Setup
    private final UserSessionService userSessionService = mock(UserSessionService.class);
    private final PermissionService permissionService = mock(PermissionService.class);
    private final PermissionInterceptor interceptor = new PermissionInterceptor(userSessionService, permissionService);

    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final HttpSession session = mock(HttpSession.class);
    private final HandlerMethod handlerMethod = mock(HandlerMethod.class);

    @BeforeEach
    void setUp() {
        when(request.getSession(false)).thenReturn(session);
    }

    private void givenAnnotatedEndpoint(String permission) {
        RequiresPermission annotation = mock(RequiresPermission.class);
        when(annotation.value()).thenReturn(permission);
        when(handlerMethod.getMethodAnnotation(RequiresPermission.class)).thenReturn(annotation);
    }

    private UUID givenLoggedInUser() {
        UUID userId = UUID.randomUUID();
        when(userSessionService.isLoggedIn(session)).thenReturn(true);
        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.of(userId));
        return userId;
    }
    // endregion

    // region nonHandlerMethod tests
    @Test
    void preHandle_nonHandlerMethod_returnsTrue() throws Exception {
        boolean result = interceptor.preHandle(request, response, new Object());
        assertTrue(result);
    }
    // endregion

    // region noAnnotation tests
    @Test
    void preHandle_noAnnotation_returnsTrue() throws Exception {
        givenLoggedInUser();
        when(request.getRequestURI()).thenReturn("/articles");

        boolean result = interceptor.preHandle(request, response, handlerMethod);

        assertTrue(result);
    }

    @Test
    void preHandle_noAnnotation_tracksUrl() throws Exception {
        givenLoggedInUser();
        when(request.getRequestURI()).thenReturn("/articles");

        interceptor.preHandle(request, response, handlerMethod);

        verify(session).setAttribute("lastSuccessfulUrl", "/articles");
    }

    @Test
    void preHandle_noAnnotation_withQueryString_tracksFullUrl() throws Exception {
        givenLoggedInUser();
        when(request.getRequestURI()).thenReturn("/articles");
        when(request.getQueryString()).thenReturn("page=2");

        interceptor.preHandle(request, response, handlerMethod);

        verify(session).setAttribute("lastSuccessfulUrl", "/articles?page=2");
    }

    @Test
    void preHandle_noAnnotation_errorUri_doesNotTrackUrl() throws Exception {
        givenLoggedInUser();
        when(request.getRequestURI()).thenReturn("/error/404");

        interceptor.preHandle(request, response, handlerMethod);

        verify(session, never()).setAttribute(eq("lastSuccessfulUrl"), any());
    }

    @Test
    void preHandle_noAnnotation_noSession_redirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, handlerMethod);

        assertFalse(result);
        verify(response).sendRedirect("/login");
    }

    @Test
    void preHandle_noAnnotation_notLoggedIn_redirectsToLogin() throws Exception {
        when(userSessionService.isLoggedIn(session)).thenReturn(false);

        boolean result = interceptor.preHandle(request, response, handlerMethod);

        assertFalse(result);
        verify(response).sendRedirect("/login");
    }
    // endregion

    // region annotationPresent tests
    @Test
    void preHandle_annotationPresent_noSession_redirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        givenAnnotatedEndpoint("any");

        boolean result = interceptor.preHandle(request, response, handlerMethod);

        assertFalse(result);
        verify(response).sendRedirect("/login");
    }

    @Test
    void preHandle_annotationPresent_notLoggedIn_redirectsToLogin() throws Exception {
        givenAnnotatedEndpoint("any");
        when(userSessionService.isLoggedIn(session)).thenReturn(false);

        boolean result = interceptor.preHandle(request, response, handlerMethod);

        assertFalse(result);
        verify(response).sendRedirect("/login");
    }

    @Test
    void preHandle_annotationPresent_loggedIn_noPermission_returns403() throws Exception {
        givenAnnotatedEndpoint("EDIT_ARTICLE");
        UUID userId = givenLoggedInUser();
        when(permissionService.hasPermission(userId, "EDIT_ARTICLE")).thenReturn(false);

        boolean result = interceptor.preHandle(request, response, handlerMethod);

        assertFalse(result);
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
    }

    @Test
    void preHandle_annotationPresent_loggedIn_hasPermission_returnsTrue() throws Exception {
        when(request.getRequestURI()).thenReturn("/articles/edit");
        givenAnnotatedEndpoint("EDIT_ARTICLE");
        UUID userId = givenLoggedInUser();
        when(permissionService.hasPermission(userId, "EDIT_ARTICLE")).thenReturn(true);

        boolean result = interceptor.preHandle(request, response, handlerMethod);

        assertTrue(result);
    }
    // endregion
}
