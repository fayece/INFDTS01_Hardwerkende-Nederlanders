package nl.hardwerkendenederlanders.hrcms.exceptions;

import lombok.Getter;

@Getter
public class ComponentActionException extends RuntimeException {

    public enum Action {
        CREATE,
        UPDATE,
        DELETE
    }

    private final String componentName;
    private final Action action;
    private final String redirectTarget;

    public ComponentActionException(String componentName, Action action, String redirectTarget, Throwable cause) {
        super("Failed to " + action.name().toLowerCase() + " component: " + componentName, cause);
        this.componentName = componentName;
        this.action = action;
        this.redirectTarget = redirectTarget;
    }
}
