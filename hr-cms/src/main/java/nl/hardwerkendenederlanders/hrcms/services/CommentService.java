package nl.hardwerkendenederlanders.hrcms.services;

import java.util.List;
import java.util.UUID;

import nl.hardwerkendenederlanders.hrcms.database.CommentRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ComponentUnavailableException;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getTopLevelComments(UUID articleId, int page, int limit) {
        try {
            return commentRepository.findTopLevelCommentsByArticleIdPaged(articleId, page, limit);
        } catch (Exception e) {
            throw new ComponentUnavailableException("comments", e);
        }
    }

    public List<Comment> getReplies(UUID parentId) {
        try {
            return commentRepository.findCommentsByParentId(parentId);
        } catch (Exception e) {
            throw new ComponentUnavailableException("comments", e);
        }
    }
}
