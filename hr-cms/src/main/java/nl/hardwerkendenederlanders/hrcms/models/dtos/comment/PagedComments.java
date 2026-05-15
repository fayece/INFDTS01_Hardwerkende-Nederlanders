package nl.hardwerkendenederlanders.hrcms.models.dtos.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PagedComments {
    private List<CommentViewDto> comments;
    private boolean hasMore;
}
