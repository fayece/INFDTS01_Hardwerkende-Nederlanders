package nl.hardwerkendenederlanders.hrcms.models;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Role extends BaseEntity {

    @Setter
    private String roleName;

    private final String internalName;

    public Role(UUID id, String roleName, String internalName) {
        super(id);
        this.roleName = roleName;
        this.internalName = internalName;
    }

    public Role(String roleName) {
        super();
        this.roleName = roleName;
        this.internalName = generateInternalName(roleName);
    }

    private String generateInternalName(String roleName) {
        return roleName.toUpperCase().replaceAll("\\s+", "_");
    }
}
