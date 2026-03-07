package nl.hardwerkendenederlanders.hrcms.models;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.Nullable;

@SuperBuilder
@Getter
public class User extends BaseTimedEntity {

    @Setter
    private String firstName;

    @Setter
    @Nullable
    private String prefix;

    @Setter
    private String lastName;

    @Setter
    private String email;

    @Setter
    private String passwordHash;

    @Setter
    private UUID roleId;

    @Setter
    @Nullable
    private UUID organizationId;

    @Setter
    @Builder.Default
    private boolean active = true;

    public User(
            UUID id,
            String firstName,
            @Nullable String prefix,
            String lastName,
            String email,
            String passwordHash,
            UUID roleId,
            @Nullable UUID organizationId,
            boolean active,
            OffsetDateTime createdAt) {
        super(id, createdAt);
        this.firstName = firstName;
        this.prefix = prefix;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roleId = roleId;
        this.organizationId = organizationId;
        this.active = active;
    }

    public User(
            String firstName,
            @Nullable String prefix,
            String lastname,
            String email,
            String passwordHash,
            UUID roleId,
            @Nullable UUID organizationId) {
        super();
        this.firstName = firstName;
        this.prefix = prefix;
        this.lastName = lastname;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roleId = roleId;
        this.organizationId = organizationId;
        this.active = true;
    }
}
