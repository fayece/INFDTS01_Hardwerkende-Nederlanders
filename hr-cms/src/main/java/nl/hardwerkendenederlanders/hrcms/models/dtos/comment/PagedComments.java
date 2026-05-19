package nl.hardwerkendenederlanders.hrcms.models.dtos.comment;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PagedComments {
    private List<CommentViewDto> comments;
    private boolean hasMore;
}
