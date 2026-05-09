package nl.hardwerkendenederlanders.hrcms.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.PermissionService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@AllArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final UserSessionService userSessionService;
    private final PermissionService permissionService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod))
            return true;

        RequiresPermission annotation = handlerMethod.getMethodAnnotation(RequiresPermission.class);

        if (annotation == null)
            return true;

        HttpSession session = request.getSession(false);
        if (session == null || !userSessionService.isLoggedIn(session)) {
            response.sendRedirect("/login");
            return false;
        }

        UUID userId = userSessionService.getLoggedInUser(session)
            .orElseThrow(() -> new RuntimeException("User not logged in"));

        if (!permissionService.hasPermission(userId, annotation.value())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return false;
        }

        return true;
    }
}
