package nl.hardwerkendenederlanders.hrcms.models;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.Nullable;

@Getter
@SuperBuilder
public class Article extends BaseTimedEntity {

    @Setter
    private String title;

    @Setter
    private String textContent;

    @Setter
    @Nullable
    private OffsetDateTime updatedAt;

    @Setter
    @Builder.Default
    private PublicationStatus publicationStatus = PublicationStatus.DRAFT;

    public Article(
            UUID id,
            String title,
            String textContent,
            OffsetDateTime createdAt,
            @Nullable OffsetDateTime updatedAt,
            PublicationStatus publicationStatus) {
        super(id, createdAt);
        this.title = title;
        this.textContent = textContent;
        this.updatedAt = updatedAt;
        this.publicationStatus = publicationStatus;
    }

    public Article(String title, String textContent, @Nullable PublicationStatus publicationStatus) {
        super();
        this.title = title;
        this.textContent = textContent;
        this.publicationStatus = publicationStatus != null ? publicationStatus : PublicationStatus.DRAFT;
    }

    public  Article() {
        super();
    }


}
