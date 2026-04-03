package nl.hardwerkendenederlanders.hrcms.models.dtos.userDtos;

import lombok.Builder;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UserViewDto {

    @Setter
    private UUID id;

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
    private UUID roleId;

    @Setter
    @Nullable
    private UUID organizationId;

    @Setter
    private boolean active;

    @Setter
    private OffsetDateTime createdAt;
}
