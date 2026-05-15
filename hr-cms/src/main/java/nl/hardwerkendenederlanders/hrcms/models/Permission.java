package nl.hardwerkendenederlanders.hrcms.models;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Permission {

    @Builder.Default
    private final UUID id = UUID.randomUUID();

    private final String resource;

    private final String actionName;

    private final String permissionKey;

    public static PermissionBuilder of(String resource, String actionName) {
        return Permission.builder().resource(resource).actionName(actionName);
    }
}
