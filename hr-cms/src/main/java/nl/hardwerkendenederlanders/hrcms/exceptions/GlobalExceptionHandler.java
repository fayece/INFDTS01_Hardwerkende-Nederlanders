package nl.hardwerkendenederlanders.hrcms.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

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
}
