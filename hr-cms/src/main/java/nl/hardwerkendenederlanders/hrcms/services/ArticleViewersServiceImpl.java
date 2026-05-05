package nl.hardwerkendenederlanders.hrcms.services;

import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleViewersRepository;
import nl.hardwerkendenederlanders.hrcms.models.ArticleViewer;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleViewersService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ArticleViewersServiceImpl implements ArticleViewersService {
    private final ArticleViewersRepository articleViewersRepository;

    @Override
    public void addView(ArticleViewer articleViewer) {
        articleViewersRepository.ensureInsert(articleViewer);
    }
}
