package nl.hardwerkendenederlanders.hrcms.configuration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.RoleRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.DbRoleContext;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.DbSessionRole;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DbRoleFilterTest {

    private final UserSessionService userSessionService = mock(UserSessionService.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final DbRoleFilter filter = new DbRoleFilter(userSessionService, userRepository, roleRepository);

    private final ServletRequest request = mock(HttpServletRequest.class);
    private final ServletResponse response = mock(ServletResponse.class);
    private final FilterChain chain = mock(FilterChain.class);
    private final HttpSession session = mock(HttpSession.class);

    @BeforeEach
    void setUp() {
        DbRoleContext.clear();
    }

    @Test
    void doFilter_noSession_setsUnauthenticatedDuringChain() throws Exception {
        when(((HttpServletRequest) request).getSession(false)).thenReturn(null);

        doAnswer(_ -> {
                    assertEquals(DbSessionRole.UNAUTHENTICATED, DbRoleContext.get());
                    return null;
                })
                .when(chain)
                .doFilter(request, response);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilter_notLoggedIn_setsUnauthenticatedDuringChain() throws Exception {
        when(((HttpServletRequest) request).getSession(false)).thenReturn(session);
        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.empty());

        doAnswer(_ -> {
                    assertEquals(DbSessionRole.UNAUTHENTICATED, DbRoleContext.get());
                    return null;
                })
                .when(chain)
                .doFilter(request, response);

        filter.doFilter(request, response, chain);
    }

    @Test
    void doFilter_loggedInUser_resolvesRoleFromInternalName() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        when(((HttpServletRequest) request).getSession(false)).thenReturn(session);
        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.of(userId));
        when(userRepository.findRoleIdById(userId)).thenReturn(Optional.of(roleId));
        when(roleRepository.findInternalNameById(roleId)).thenReturn(Optional.of("ADMINISTRATOR"));

        doAnswer(_ -> {
                    assertEquals(DbSessionRole.ADMINISTRATOR, DbRoleContext.get());
                    return null;
                })
                .when(chain)
                .doFilter(request, response);

        filter.doFilter(request, response, chain);
    }

    @Test
    void doFilter_loggedInUser_unknownRole_setsUnauthenticatedDuringChain() throws Exception {
        UUID userId = UUID.randomUUID();

        when(((HttpServletRequest) request).getSession(false)).thenReturn(session);
        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.of(userId));
        when(userRepository.findRoleIdById(userId)).thenReturn(Optional.empty());

        doAnswer(_ -> {
                    assertEquals(DbSessionRole.UNAUTHENTICATED, DbRoleContext.get());
                    return null;
                })
                .when(chain)
                .doFilter(request, response);

        filter.doFilter(request, response, chain);
    }

    @Test
    void doFilter_clearsContextAfterChain() throws Exception {
        when(((HttpServletRequest) request).getSession(false)).thenReturn(null);

        filter.doFilter(request, response, chain);

        assertEquals(DbSessionRole.UNAUTHENTICATED, DbRoleContext.get());
    }
}
