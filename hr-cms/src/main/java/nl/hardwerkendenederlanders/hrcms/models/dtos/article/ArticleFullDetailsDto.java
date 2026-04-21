package nl.hardwerkendenederlanders.hrcms.models.dtos.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.hardwerkendenederlanders.hrcms.models.PublicationStatus;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ArticleFullDetailsDto {


    private UUID id;

    private String title;
    private String textContent;
    @Nullable
    private OffsetDateTime updatedAt;
    private OffsetDateTime createdAt;
    private PublicationStatus publicationStatus;
    private String subjectName;

    private int viewCount;
    private int commentCount;

    private AuthorDto firstAuthor; // the first author is the author that was the first to write something in an article.
}