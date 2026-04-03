interface Comment {
    id: string;
    commentBody: string;
    creatorId: string;
    articleId: string;
    mediaId?: string | null;
    parentCommentId?: string | null;
    createdAt: string;
    deletedAt?: string | null;
}
