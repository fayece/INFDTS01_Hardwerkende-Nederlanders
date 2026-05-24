package nl.hardwerkendenederlanders.hrcms.models;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class LoggingEntity {
    private UUID id;
    private String message;
    private String severity;
    private OffsetDateTime timestamp;
    private UUID userId;
    private String profileId;
}
