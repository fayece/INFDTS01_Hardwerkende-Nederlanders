package nl.hardwerkendenederlanders.hrcms.models;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ArticleViewer {
    private final UUID articleId;
    private final UUID viewerId;
}
