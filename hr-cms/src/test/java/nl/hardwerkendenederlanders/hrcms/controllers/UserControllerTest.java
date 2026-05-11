package nl.hardwerkendenederlanders.hrcms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Role;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.RoleService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

class UserControllerTest {

    private final UserService userService = mock(UserService.class);
    private final UserSessionService userSessionService = mock(UserSessionService.class);
    private final RoleService roleService = mock(RoleService.class);
    private final UserController userController = new UserController(userService, userSessionService, roleService);

    @Test
    void manageUserPage_default() {
        Model model = new ConcurrentModel();
        HttpSession session = mock(HttpSession.class);
        List<User> users = List.of(mock(User.class), mock(User.class));

        when(userService.getUsers(0, null, null)).thenReturn(users);
        when(userService.getMaxPages(null, null)).thenReturn(1);
        when(session.getAttribute("recentSearches")).thenReturn(null);

        String result = userController.manageUserPage(0, null, null, model, session);

        assertEquals("pages/manage-users", result);
        assertEquals(users, model.getAttribute("users"));
        assertEquals(0, model.getAttribute("currentPage"));
        assertEquals(1, model.getAttribute("finalPage"));

        verify(userService).getUsers(0, null, null);
        verify(userService).getMaxPages(null, null);
        verify(session).getAttribute("recentSearches");
        verify(session, never()).setAttribute(eq("recentSearches"), any());
    }

    @Test
    void manageUserPage_withSearchName() {
        Model model = new ConcurrentModel();
        HttpSession session = mock(HttpSession.class);
        List<User> users = List.of(mock(User.class));

        when(userService.getUsers(0, "Kim", null)).thenReturn(users);
        when(userService.getMaxPages("Kim", null)).thenReturn(1);
        when(session.getAttribute("recentSearches")).thenReturn(null);

        String result = userController.manageUserPage(0, "Kim", null, model, session);

        assertEquals("pages/manage-users", result);
        assertEquals(users, model.getAttribute("users"));
        assertEquals("Kim", model.getAttribute("searchName"));
        assertEquals(0, model.getAttribute("currentPage"));
        assertEquals(1, model.getAttribute("finalPage"));

        verify(userService).getUsers(0, "Kim", null);
        verify(userService).getMaxPages("Kim", null);
        verify(session).getAttribute("recentSearches");
        verify(session).setAttribute(eq("recentSearches"), any(ArrayDeque.class));
    }

    @Test
    void manageUserPage_withSortActive() {
        Model model = new ConcurrentModel();
        HttpSession session = mock(HttpSession.class);
        List<User> users = List.of(mock(User.class));

        when(userService.getUsers(0, null, true)).thenReturn(users);
        when(userService.getMaxPages(null, true)).thenReturn(1);
        when(session.getAttribute("recentSearches")).thenReturn(null);

        String result = userController.manageUserPage(0, null, true, model, session);

        assertEquals("pages/manage-users", result);
        assertEquals(users, model.getAttribute("users"));
        assertEquals(0, model.getAttribute("currentPage"));
        assertEquals(1, model.getAttribute("finalPage"));

        verify(userService).getUsers(0, null, true);
        verify(userService).getMaxPages(null, true);
        verify(session).getAttribute("recentSearches");
        verify(session, never()).setAttribute(eq("recentSearches"), any());
    }

    @Test
    void createUserPage_returnsView() {
        Model model = new ConcurrentModel();
        List<Role> roles = List.of(mock(Role.class));
        when(roleService.findAll()).thenReturn(roles);

        String result = userController.createUserPage(model);

        assertEquals("pages/create-user", result);
        assertEquals(roles, model.getAttribute("roles"));
        verify(roleService).findAll();
    }

    @Test
    void editUser_success() {
        Model model = new ConcurrentModel();
        UUID id = UUID.randomUUID();
        User user = mock(User.class);

        when(userService.findById(id)).thenReturn(user);

        String result = userController.editUser(id, model);

        assertEquals("pages/edit-user", result);
        assertEquals(user, model.getAttribute("currentUser"));
        assertEquals(id, model.getAttribute("currentUserId"));

        verify(userService).findById(id);
    }

    @Test
    void editUser_notFound_redirects() {
        Model model = new ConcurrentModel();
        UUID id = UUID.randomUUID();

        when(userService.findById(id)).thenReturn(null);

        String result = userController.editUser(id, model);

        assertEquals("redirect:/manage-users", result);
        verify(userService).findById(id);
    }

    @Test
    void createNewUser_success() {
        Model model = new ConcurrentModel();
        UUID roleId = UUID.randomUUID();
        List<Role> roles = List.of(mock(Role.class));
        when(roleService.findAll()).thenReturn(roles);

        String result = userController.createNewUser("Kim", "", "Possible", "kp@example.com", "secret", roleId, model);

        assertEquals("pages/create-user", result);
        assertEquals(true, model.getAttribute("inserted"));
        assertEquals(roles, model.getAttribute("roles"));

        verify(userService).insertUser("Kim", "", "Possible", "kp@example.com", "secret", roleId);
        verify(roleService).findAll();
    }

    @Test
    void updateUser_success() {
        Model model = new ConcurrentModel();

        UUID id = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();

        String result = userController.updateUser(id, "New", "de", "Name", "new@test.com", roleId, orgId, model);

        assertEquals("redirect:/manage-users", result);

        verify(userService).updateUser(id, "New", "de", "Name", "new@test.com", roleId, orgId);
    }

    @Test
    void changeActiveStatus_success() {
        Model model = new ConcurrentModel();
        UUID id = UUID.randomUUID();

        String result = userController.changeActiveStatus(id, true, model);

        assertEquals("redirect:/manage-users", result);
        verify(userService).updateActivityById(id, true);
    }

    @Test
    void confirmDeleteUser_success() {
        Model model = new ConcurrentModel();
        UUID id = UUID.randomUUID();

        User user = mock(User.class);
        when(user.getFirstName()).thenReturn("Kim");
        when(user.getLastName()).thenReturn("Possible");
        when(user.getEmail()).thenReturn("kp@example.com");

        when(userService.findById(id)).thenReturn(user);

        String result = userController.confirmDeleteUser(id, model);

        assertEquals("pages/confirm-delete-user", result);
        assertEquals("Kim", model.getAttribute("userFirstName"));
        assertEquals("Possible", model.getAttribute("userLastName"));
        assertEquals("kp@example.com", model.getAttribute("userEmail"));
        assertEquals(id, model.getAttribute("userToDeleteId"));

        verify(userService).findById(id);
    }

    @Test
    void deleteUser_success() {
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        HttpSession session = mock(HttpSession.class);

        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.of(currentUserId));

        String result = userController.deleteUser(userId, session);

        assertEquals("redirect:/manage-users", result);
        verify(userSessionService).getLoggedInUser(session);
        verify(userService).deleteById(userId, currentUserId);
    }

    @Test
    void deleteUser_fail() {
        UUID userId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        HttpSession session = mock(HttpSession.class);

        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.of(currentUserId));
        doThrow(new IllegalArgumentException("cannot delete current user"))
                .when(userService)
                .deleteById(userId, currentUserId);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> userController.deleteUser(userId, session));

        assertEquals("cannot delete current user", exception.getMessage());
        verify(userSessionService).getLoggedInUser(session);
        verify(userService).deleteById(userId, currentUserId);
    }
}
