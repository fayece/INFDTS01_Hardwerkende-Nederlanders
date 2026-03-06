package nl.hardwerkendenederlanders.hrcms.models;

import java.util.UUID;
import lombok.Getter;

@Getter
public class ArticleAuthor extends BaseEntity {

    private final UUID articleId;

    private final UUID authorId;

    public ArticleAuthor(UUID id, UUID articleId, UUID authorId) {
        super(id);
        this.articleId = articleId;
        this.authorId = authorId;
    }

    public ArticleAuthor(UUID articleId, UUID authorId) {
        super();
        this.articleId = articleId;
        this.authorId = authorId;
    }
}
