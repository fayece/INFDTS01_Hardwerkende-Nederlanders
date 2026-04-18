package nl.hardwerkendenederlanders.hrcms.exceptions;

import lombok.Getter;

@Getter
public class ComponentUnavailableException extends RuntimeException {

    private final String componentName;
    private final String viewName;

    public ComponentUnavailableException(String componentName, String viewName, Throwable cause) {
        super(String.format("The %s component is currently unavailable.", componentName), cause);
        this.componentName = componentName;
        this.viewName = viewName;
    }
}
