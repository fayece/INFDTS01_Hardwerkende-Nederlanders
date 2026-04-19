package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleViewersRepository;
import nl.hardwerkendenederlanders.hrcms.models.ArticleViewer;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ArticleViewersServiceImpl implements ArticleViewersService{
    private final ArticleViewersRepository articleViewersRepository;


    @Override
    public void AddView(ArticleViewer articleViewer) {
        articleViewersRepository.ensureInsert(articleViewer);
    }
}
