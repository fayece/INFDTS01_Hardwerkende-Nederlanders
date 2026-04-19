package nl.hardwerkendenederlanders.hrcms.models.dtos.article;

import nl.hardwerkendenederlanders.hrcms.models.Article;

public record ArticleWithSubjectAndViewsDto(
    Article article,
    String subjectName,
    int viewCount
) {}