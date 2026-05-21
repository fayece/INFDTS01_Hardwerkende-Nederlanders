package nl.hardwerkendenederlanders.hrcms.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.data.annotation.Id;

import java.time.OffsetDateTime;
import java.util.UUID;

@SuperBuilder
@Getter
public class LoggingEntity {
    private UUID id;
    private String message;
    private String severity;
    private OffsetDateTime timestamp;
}
