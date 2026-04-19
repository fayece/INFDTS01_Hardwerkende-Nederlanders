package nl.hardwerkendenederlanders.hrcms.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ArticleViewer {
    private final UUID articleId;
    private final UUID viewerId;
}
