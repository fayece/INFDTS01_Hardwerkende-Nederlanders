package nl.hardwerkendenederlanders.hrcms.models;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class Role {

    @Builder.Default
    private final UUID id = UUID.randomUUID();

    @Setter
    private String roleName;

    private final String internalName;

    public static RoleBuilder of(String roleName) {
        return Role.builder().roleName(roleName);
    }
}
