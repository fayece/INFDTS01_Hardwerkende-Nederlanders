package nl.hardwerkendenederlanders.hrcms.services;

import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.database.cache.interfaces.ArticleCache;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleViewersRepository;
import nl.hardwerkendenederlanders.hrcms.models.ArticleViewer;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleViewersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ArticleViewersServiceImpl implements ArticleViewersService {
    private final ArticleViewersRepository articleViewersRepository;
    private final ArticleCache articleCache;

    @Transactional
    @Override
    public void AddView(ArticleViewer articleViewer) {
        articleViewersRepository.ensureInsert(articleViewer);
        //articleCache.IncrementViewForArticle(articleViewer.getArticleId());
    }
}
