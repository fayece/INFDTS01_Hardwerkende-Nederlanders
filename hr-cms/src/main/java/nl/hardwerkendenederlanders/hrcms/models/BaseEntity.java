package nl.hardwerkendenederlanders.hrcms.models;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public abstract class BaseEntity {

    @Builder.Default
    protected final UUID id = UUID.randomUUID();

    protected BaseEntity() {
        this.id = UUID.randomUUID();
    }

    protected BaseEntity(UUID id) {
        this.id = id != null ? id : UUID.randomUUID();
    }
}
