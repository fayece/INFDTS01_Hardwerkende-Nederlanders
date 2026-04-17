package nl.hardwerkendenederlanders.hrcms.models.dtos.article;

import nl.hardwerkendenederlanders.hrcms.models.Article;

public record ArticleWithSubject (
    Article article,
    String subjectName
) {}