package nl.hardwerkendenederlanders.hrcms.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.PermissionService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final UserSessionService userSessionService;
    private final PermissionService permissionService;

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) return true;

        HttpSession session = request.getSession(false);

        RequiresPermission annotation = handlerMethod.getMethodAnnotation(RequiresPermission.class);

        if (annotation == null) {
            trackSuccessfulUrl(session, request);
            return true;
        }

        if (session == null || !userSessionService.isLoggedIn(session)) {
            response.sendRedirect("/login");
            return false;
        }

        UUID userId = userSessionService
                .getLoggedInUser(session)
                .orElseThrow(() -> new RuntimeException("User not logged in"));

        if (!permissionService.hasPermission(userId, annotation.value())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return false;
        }

        trackSuccessfulUrl(session, request);
        return true;
    }

    private void trackSuccessfulUrl(HttpSession session, HttpServletRequest request) {
        if (session == null) return;
        String uri = request.getRequestURI();
        if (uri.startsWith("/error")) return;
        String query = request.getQueryString();
        session.setAttribute("lastSuccessfulUrl", query != null ? uri + "?" + query : uri);
    }
}
