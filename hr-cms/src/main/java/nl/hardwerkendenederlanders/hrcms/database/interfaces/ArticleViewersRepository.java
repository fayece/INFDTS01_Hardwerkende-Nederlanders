package nl.hardwerkendenederlanders.hrcms.database.interfaces;

import nl.hardwerkendenederlanders.hrcms.models.ArticleViewer;

public interface ArticleViewersRepository {
    void ensureInsert(ArticleViewer articleViewer);
}
