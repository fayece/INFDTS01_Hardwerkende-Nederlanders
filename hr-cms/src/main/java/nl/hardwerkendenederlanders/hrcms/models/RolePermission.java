package nl.hardwerkendenederlanders.hrcms.models;

import java.util.UUID;
import lombok.Getter;

@Getter
public class RolePermission extends BaseEntity {

    private final UUID roleId;

    private final UUID permissionId;

    public RolePermission(UUID id, UUID roleId, UUID permissionId) {
        super(id);
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public RolePermission(UUID roleId, UUID permissionId) {
        super();
        this.roleId = roleId;
        this.permissionId = permissionId;
    }
}
