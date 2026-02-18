package nl.hardwerkendenederlanders.hrcms.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class notFoundPageController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request){
        return switch (request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)) {
            case HttpStatus.NOT_FOUND -> "errorPages/notFoundError";
            case HttpStatus.INTERNAL_SERVER_ERROR -> "errorPages/internalServerError";
            default -> "errorPages/genericError";
        };
    }
}
