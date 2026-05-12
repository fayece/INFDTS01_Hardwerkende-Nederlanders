package nl.hardwerkendenederlanders.hrcms.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.configuration.PermissionInterceptor;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.PermissionService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class ReaderHomeControllerTest {

    private final ArticleService articleService = mock(ArticleService.class);
    private final UserSessionService userSessionService = mock(UserSessionService.class);
    private final PermissionService permissionService = mock(PermissionService.class);

    private final ReaderHomeController controller = new ReaderHomeController(articleService);
    private final PermissionInterceptor interceptor = new PermissionInterceptor(userSessionService, permissionService);

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .addInterceptors(interceptor)
                .build();
    }

    @Test
    void getHomePage_authenticatedUser_returnsOk() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userSessionService.isLoggedIn(any())).thenReturn(true);
        when(userSessionService.getLoggedInUser(any())).thenReturn(Optional.of(userId));
        when(permissionService.hasPermission(any(), any())).thenReturn(true);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);

        mockMvc.perform(get("/").session(session)).andExpect(status().isOk()).andExpect(view().name("pages/index"));
    }

    @Test
    void getHomePage_unauthenticatedUser_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().is3xxRedirection());
    }
}
