package nl.hardwerkendenederlanders.hrcms.models;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@Builder
public class Article {
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

    public static Article fillOutNullFields(Article article) {
        return Article.builder()
                .id(article.getId() != null ? article.getId() : UUID.randomUUID())
                .title(article.getTitle())
                .textContent(article.getTextContent())
                .createdAt(article.getCreatedAt() != null ? article.getCreatedAt() : OffsetDateTime.now())
                .updatedAt(article.getUpdatedAt())
                .publicationStatus(
                        article.getPublicationStatus() != null
                                ? article.getPublicationStatus()
                                : PublicationStatus.DRAFT)
                .subjectId(article.getSubjectId())
                .build();
    }
}
