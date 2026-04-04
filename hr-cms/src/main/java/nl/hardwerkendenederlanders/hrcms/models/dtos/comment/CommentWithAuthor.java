package nl.hardwerkendenederlanders.hrcms.models.dtos.comment;

import nl.hardwerkendenederlanders.hrcms.models.Comment;

public record CommentWithAuthor(Comment comment, String authorName) {}
