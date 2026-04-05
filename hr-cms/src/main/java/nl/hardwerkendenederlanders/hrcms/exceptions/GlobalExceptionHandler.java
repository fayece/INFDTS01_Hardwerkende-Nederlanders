package nl.hardwerkendenederlanders.hrcms.exceptions;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
                "Could not %s your %s. Please try again later.",
                ex.getAction().name().toLowerCase(), ex.getComponentName());

        redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        return "redirect:" + ex.getRedirectTarget();
    }

    @ExceptionHandler(MediaUploadException.class)
    public ResponseEntity<Map<String, String>> handleMediaUpload(MediaUploadException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
