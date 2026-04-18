package nl.hardwerkendenederlanders.hrcms.exceptions;

import static java.util.Objects.requireNonNullElse;

import nl.hardwerkendenederlanders.hrcms.models.dtos.exceptions.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ComponentUnavailableException.class)
    public ModelAndView handleComponentUnavailable(ComponentUnavailableException ex) {
        ModelAndView mav = new ModelAndView();

        mav.addObject("warningMessage", ex.getMessage());
        mav.addObject("failedComponent", ex.getComponentName());

        mav.setViewName(ex.getViewName());
        return mav;
    }

    @ExceptionHandler(ComponentActionException.class)
    public String handleComponentAction(ComponentActionException ex, RedirectAttributes redirectAttributes) {
        String errorMessage = String.format(
                "Could not %s your %s. %s.",
                ex.getAction().name().toLowerCase(), ex.getComponentName(), ex.getReason());

        redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        return "redirect:" + ex.getRedirectTarget();
    }

    @ExceptionHandler(MediaUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMediaUpload(MediaUploadException ex) {
        String errorMessage = requireNonNullElse(ex.getMessage(), "An unknown error occurred");
        return new ErrorResponse(errorMessage);
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException e, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/manage-users";
    }

}
