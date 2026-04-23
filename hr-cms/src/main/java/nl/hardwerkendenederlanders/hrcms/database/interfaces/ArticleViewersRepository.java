package nl.hardwerkendenederlanders.hrcms.database.interfaces;

import nl.hardwerkendenederlanders.hrcms.models.ArticleViewer;

public interface ArticleViewersRepository {
    /*
     * Ensure the object exists in the database. if It already exists nothing happens.
     * */
    void ensureInsert(ArticleViewer articleViewer);
}
