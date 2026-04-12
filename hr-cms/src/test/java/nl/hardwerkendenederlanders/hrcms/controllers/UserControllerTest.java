package nl.hardwerkendenederlanders.hrcms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

class UserControllerTest {

    private final UserService userService = mock(UserService.class);
    private final UserController userController = new UserController(userService);

    @Test
    void manageUserPage_default() {
        Model model = new ConcurrentModel();
        List<User> users = List.of(mock(User.class), mock(User.class));

        when(userService.findUsersPaginated(0, 13)).thenReturn(users);
        when(userService.findAllUsers()).thenReturn(users);

        String result = userController.manageUserPage(0, model, null, null);

        assertEquals("pages/manage-users", result);
        assertEquals(users, model.getAttribute("users"));
        assertEquals(0, model.getAttribute("currentPage"));
        assertEquals(1, model.getAttribute("finalPage"));

        verify(userService).findUsersPaginated(0, 13);
        verify(userService).findAllUsers();
    }

    @Test
    void manageUserPage_withSearchName() {
        Model model = new ConcurrentModel();
        List<User> users = List.of(mock(User.class));

        when(userService.searchByNamePaginated("Kim", 0, 13)).thenReturn(users);
        when(userService.findAllUsers()).thenReturn(users);

        String result = userController.manageUserPage(0, model, "Kim", null);

        assertEquals("pages/manage-users", result);
        assertEquals(users, model.getAttribute("users"));
        assertEquals("Kim", model.getAttribute("searchName"));

        verify(userService).searchByNamePaginated("Kim", 0, 13);
    }

    @Test
    void manageUserPage_withSortActive() {
        Model model = new ConcurrentModel();
        List<User> users = List.of(mock(User.class));

        when(userService.findUserOnActivityPaginated(true, 0, 13)).thenReturn(users);
        when(userService.findAllUsers()).thenReturn(users);

        String result = userController.manageUserPage(0, model, null, true);

        assertEquals("pages/manage-users", result);
        assertEquals(users, model.getAttribute("users"));

        verify(userService).findUserOnActivityPaginated(true, 0, 13);
    }

    @Test
    void createUserPage_returnsView() {
        String result = userController.createUserPage();

        assertEquals("pages/create-user", result);
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
    }

    @Test
    void createNewUser_success() {
        Model model = new ConcurrentModel();

        String result = userController.createNewUser("Kim", "", "Possible", "kp@example.com", "secret", model);

        assertEquals("pages/create-user", result);
        assertEquals(true, model.getAttribute("inserted"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).insertUser(captor.capture());

        User user = captor.getValue();
        assertEquals("Kim", user.getFirstName());
        assertEquals("", user.getPrefix());
        assertEquals("Possible", user.getLastName());
        assertEquals("kp@example.com", user.getEmail());
        assertTrue(user.isActive());
        assertNotNull(user.getId());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void updateUser_success() {
        Model model = new ConcurrentModel();

        UUID id = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();

        User user = new User(id, "Old", null, "Name", "old@test.com", "pass", null, null, true, OffsetDateTime.now());

        when(userService.findById(id)).thenReturn(user);

        String result = userController.updateUser(id, "de", "New", "Name", "new@test.com", roleId, orgId, model);

        assertEquals("redirect:/manage-users", result);

        verify(userService).updateUser(user);

        assertEquals("de", user.getPrefix());
        assertEquals("New", user.getFirstName());
        assertEquals("Name", user.getLastName());
        assertEquals("new@test.com", user.getEmail());
        assertEquals(roleId, user.getRoleId());
        assertEquals(orgId, user.getOrganizationId());
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
    }

    @Test
    void deleteUser_success() {
        UUID id = UUID.randomUUID();

        String result = userController.deleteUser(id);

        assertEquals("redirect:/manage-users", result);
        verify(userService).deleteById(id);
    }
}
