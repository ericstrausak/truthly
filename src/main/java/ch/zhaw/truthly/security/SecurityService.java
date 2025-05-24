package ch.zhaw.truthly.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ch.zhaw.truthly.repository.ArticleRepository;
import ch.zhaw.truthly.repository.UserRepository;

@Service("securityService")
public class SecurityService {
    
    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public boolean isArticleAuthor(String articleId, String userEmail) {
        return articleRepository.findById(articleId)
            .map(article -> {
                // Hier müsstest du die E-Mail zu User ID mapping implementieren
                // Für jetzt: vereinfachte Logik
                return checkIfUserIsAuthor(article.getAuthorId(), userEmail);
            })
            .orElse(false);
    }
    
    private boolean checkIfUserIsAuthor(String authorId, String userEmail) {
        // TODO: Implementiere E-Mail zu User ID Mapping
        // Dies hängt davon ab, wie du User-Informationen aus dem JWT extrahierst
        return true; // Placeholder - implementiere echte Logik
    }
    
    public boolean hasPermissionForArticle(String articleId, String userEmail, String requiredRole) {
        // Weitere custom Authorization-Logik
        return true; // Placeholder
    }
}