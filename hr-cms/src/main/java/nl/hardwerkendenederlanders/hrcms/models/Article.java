package nl.hardwerkendenederlanders.hrcms.models;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@SuperBuilder
public class Article{
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private String title;
    private String textContent;
    @Nullable
    private OffsetDateTime updatedAt;
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
    @Nullable
    private UUID subjectId;

    @Builder.Default
    private PublicationStatus publicationStatus = PublicationStatus.DRAFT;


    public Article(
            UUID id,
            String title,
            String textContent,
            OffsetDateTime createdAt,
            @Nullable OffsetDateTime updatedAt,
            PublicationStatus publicationStatus,
            @Nullable UUID subjectId) {
        this.id = id;
        this.title = title;
        this.textContent = textContent;
        this.updatedAt = updatedAt;
        this.publicationStatus = publicationStatus;
        this.createdAt = OffsetDateTime.now();
        this.subjectId = subjectId;
    }

    public Article(String title, String textContent, @Nullable PublicationStatus publicationStatus) {
        super();
        this.title = title;
        this.textContent = textContent;
        this.publicationStatus = publicationStatus != null ? publicationStatus : PublicationStatus.DRAFT;
    }

    public  Article() {
        super();
        this.id = UUID.randomUUID();
    }

    public static Article FillOutNullFields(Article article){
        return new Article(
                article.getId() != null ? article.getId() : UUID.randomUUID(),
                article.getTitle(),
                article.getTextContent(),
                article.getCreatedAt() != null ? article.getCreatedAt() : OffsetDateTime.now(),
                article.getUpdatedAt() != null ? article.getUpdatedAt() : OffsetDateTime.now(),
                article.getPublicationStatus() != null ? article.getPublicationStatus() : PublicationStatus.DRAFT,
                article.getSubjectId()
        );
    }
}
