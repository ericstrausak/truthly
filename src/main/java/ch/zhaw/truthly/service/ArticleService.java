package ch.zhaw.truthly.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ch.zhaw.truthly.model.Article;
import ch.zhaw.truthly.model.ArticleStatus; // Import the enum
import ch.zhaw.truthly.repository.ArticleRepository;
import ch.zhaw.truthly.repository.UserRepository;

@Service
public class ArticleService {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    UserRepository userRepository;

    public Optional<Article> assignArticleForChecking(String articleId, String checkerId) {
        // Check if the article exists
        Optional<Article> articleOpt = articleRepository.findById(articleId);
        if (!articleOpt.isPresent()) {
            return Optional.empty();
        }

        // Check if the checker exists
        if (!userRepository.existsById(checkerId)) {
            return Optional.empty();
        }

        // Get the article and check if it's in PUBLISHED state
        Article article = articleOpt.get();
        if (article.getStatus() != ArticleStatus.PUBLISHED) {
            return Optional.empty();
        }

        // Update article status
        article.setStatus(ArticleStatus.CHECKING);

        // Save the updated article
        Article updatedArticle = articleRepository.save(article);
        return Optional.of(updatedArticle);
    }

    public Optional<Article> completeFactChecking(String articleId, String checkerId, String resultStr) {
        // Check if the article exists
        Optional<Article> articleOpt = articleRepository.findById(articleId);
        if (!articleOpt.isPresent()) {
            return Optional.empty();
        }

        // Check if the checker exists
        if (!userRepository.existsById(checkerId)) {
            return Optional.empty();
        }

        // Get the article and check if it's in CHECKING state
        Article article = articleOpt.get();
        if (article.getStatus() != ArticleStatus.CHECKING) {
            return Optional.empty();
        }

        // Convert string result to enum
        ArticleStatus result;
        try {
            result = ArticleStatus.valueOf(resultStr);
        } catch (IllegalArgumentException e) {
            return Optional.empty(); // Invalid status
        }

        // Make sure result is either VERIFIED or REJECTED
        if (result != ArticleStatus.VERIFIED && result != ArticleStatus.REJECTED) {
            return Optional.empty();
        }

        // Update article status based on the result
        article.setStatus(result);

        // Save the updated article
        Article updatedArticle = articleRepository.save(article);
        return Optional.of(updatedArticle);
    }
}