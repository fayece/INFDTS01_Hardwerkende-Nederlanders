package nl.hardwerkendenederlanders.hrcms.models;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public abstract class BaseTimedEntity extends BaseEntity {

    @Builder.Default
    private final OffsetDateTime createdAt = OffsetDateTime.now();

    protected BaseTimedEntity() {
        super();
        this.createdAt = OffsetDateTime.now();
    }

    protected BaseTimedEntity(UUID id, OffsetDateTime createdAt) {
        super(id);
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
    }
}
