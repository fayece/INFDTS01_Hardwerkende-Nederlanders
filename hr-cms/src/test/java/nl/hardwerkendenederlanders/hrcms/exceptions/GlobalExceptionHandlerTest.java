package nl.hardwerkendenederlanders.hrcms.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.*;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleComponentUnavailable_returnsModelAndView() {
        ComponentUnavailableException ex = mock(ComponentUnavailableException.class);
        when(ex.getMessage()).thenReturn("Database connection timeout");
        when(ex.getComponentName()).thenReturn("User Database");
        when(ex.getViewName()).thenReturn("errors/component-down");

        ModelAndView mav = exceptionHandler.handleComponentUnavailable(ex);

        assertEquals("errors/component-down", mav.getViewName());
        assertEquals("Database connection timeout", mav.getModel().get("warningMessage"));
        assertEquals("User Database", mav.getModel().get("failedComponent"));
    }

    @Test
    void handleComponentAction_addsFlashAttributesAndRedirect() {
        ComponentActionException ex = mock(ComponentActionException.class, RETURNS_DEEP_STUBS);
        when(ex.getAction().name()).thenReturn("UPDATE");
        when(ex.getComponentName()).thenReturn("Prefix");
        when(ex.getRedirectTarget()).thenReturn("/settings/profile");

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String result = exceptionHandler.handleComponentAction(ex, redirectAttributes);

        assertEquals("redirect:/settings/profile", result);
        String expectedMessage = "Could not update your Prefix. Please try again later.";
        verify(redirectAttributes).addFlashAttribute("errorMessage", expectedMessage);
    }

    @Test
    void handleMediaUpload_returnsBadRequestWithErrorMap() {
        String expectedErrorMessage = "unsupported file type: text/plain";

        MediaUploadException exception = new MediaUploadException(expectedErrorMessage, null);

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleMediaUpload(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(exception.getMessage(), response.getBody().get("error"));
    }
}
