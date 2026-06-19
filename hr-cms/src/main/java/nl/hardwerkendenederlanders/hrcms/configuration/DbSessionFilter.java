package nl.hardwerkendenederlanders.hrcms.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.RoleRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.DbSessionContext;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.DbSessionRole;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.springframework.web.filter.GenericFilterBean;

@AllArgsConstructor
public class DbSessionFilter extends GenericFilterBean {

    private final UserSessionService userSessionService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) request).getSession(false);
        UUID sessionUserId = resolveUserId(session);
        DbSessionRole role = resolveRole(sessionUserId);
        UUID userId = role == DbSessionRole.UNAUTHENTICATED ? null : sessionUserId;

        DbSessionContext.set(role, userId);
        try {
            chain.doFilter(request, response);
        } finally {
            DbSessionContext.clear();
        }
    }

    private UUID resolveUserId(HttpSession session) {
        if (session == null) return null;
        return userSessionService.getLoggedInUser(session).orElse(null);
    }

    private DbSessionRole resolveRole(UUID userId) {
        if (userId == null) return DbSessionRole.UNAUTHENTICATED;

        return userRepository
                .findRoleIdById(userId)
                .flatMap(roleRepository::findInternalNameById)
                .map(DbSessionRole::fromInternalName)
                .orElse(DbSessionRole.UNAUTHENTICATED);
    }
}
