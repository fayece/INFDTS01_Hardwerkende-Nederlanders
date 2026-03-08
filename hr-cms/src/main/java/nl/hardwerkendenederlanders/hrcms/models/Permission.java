package nl.hardwerkendenederlanders.hrcms.models;

import java.util.UUID;
import lombok.Getter;

@Getter
public class Permission extends BaseEntity {

    private final String resource;

    private final String actionName;

    private final String permissionKey;

    private final String internalName;

    private static String generateKey(String resource, String actionName) {
        return resource.toUpperCase() + ":" + actionName.toUpperCase();
    }

    public Permission(UUID id, String resource, String actionName, String permissionKey, String internalName) {
        super(id);
        this.resource = resource;
        this.actionName = actionName;
        this.permissionKey = permissionKey;
        this.internalName = internalName;
    }

    public Permission(String resource, String actionName, String internalName) {
        super();
        this.resource = resource;
        this.actionName = actionName;
        this.permissionKey = generateKey(resource, actionName);
        this.internalName = internalName;
    }
}
