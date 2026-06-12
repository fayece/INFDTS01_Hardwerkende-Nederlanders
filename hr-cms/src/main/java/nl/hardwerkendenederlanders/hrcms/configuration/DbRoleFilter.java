package nl.hardwerkendenederlanders.hrcms.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.RoleRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.DbRoleContext;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.DbSessionRole;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.springframework.web.filter.GenericFilterBean;

@AllArgsConstructor
public class DbRoleFilter extends GenericFilterBean {

    private final UserSessionService userSessionService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) request).getSession(false);

        DbRoleContext.set(resolveRole(session));
        try {
            chain.doFilter(request, response);
        } finally {
            DbRoleContext.clear();
        }
    }

    private DbSessionRole resolveRole(HttpSession session) {
        if (session == null) return DbSessionRole.UNAUTHENTICATED;

        return userSessionService
                .getLoggedInUser(session)
                .flatMap(userRepository::findRoleIdById)
                .flatMap(roleRepository::findInternalNameById)
                .map(DbSessionRole::fromInternalName)
                .orElse(DbSessionRole.UNAUTHENTICATED);
    }
}