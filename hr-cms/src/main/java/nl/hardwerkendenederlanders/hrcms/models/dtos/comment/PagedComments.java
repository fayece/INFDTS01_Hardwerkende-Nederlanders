package nl.hardwerkendenederlanders.hrcms.models.dtos.comment;

import java.util.List;

public record PagedComments(List<CommentViewDto> comments, boolean hasMore) {}
