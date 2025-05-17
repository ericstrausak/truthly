package ch.zhaw.truthly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import ch.zhaw.truthly.model.Article;
import ch.zhaw.truthly.model.ArticleCreateDTO;
import ch.zhaw.truthly.model.ArticleStatus;
import ch.zhaw.truthly.model.StatusUpdateDTO;
import ch.zhaw.truthly.repository.ArticleRepository;
import ch.zhaw.truthly.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class ArticleController {

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/article")
    public ResponseEntity<Article> createArticle(@RequestBody ArticleCreateDTO articleDTO) {
        try {
            Article article = new Article(
                    articleDTO.getTitle(),
                    articleDTO.getContent(),
                    articleDTO.getAuthorId(),
                    articleDTO.isAnonymous(),
                    articleDTO.getArticleType() // New field
            );
            Article savedArticle = articleRepository.save(article);
            return new ResponseEntity<>(savedArticle, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/article")
    public ResponseEntity<List<Article>> getAllArticles() {
        try {
            List<Article> articles = articleRepository.findAll();
            return new ResponseEntity<>(articles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/article/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable String id) {
        try {
            Optional<Article> articleData = articleRepository.findById(id);

            if (articleData.isPresent()) {
                return new ResponseEntity<>(articleData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Alternative Methode mit @RequestMapping statt @PutMapping
    @RequestMapping(value = "/article/{id}/status", method = RequestMethod.PUT)
    public ResponseEntity<Article> updateArticleStatus(
            @PathVariable String id,
            @RequestBody StatusUpdateDTO statusDTO) {
        try {
            Optional<Article> articleData = articleRepository.findById(id);

            if (articleData.isPresent()) {
                Article article = articleData.get();

                // Get current and new status
                ArticleStatus currentStatus = article.getStatus();
                ArticleStatus newStatus = statusDTO.getStatus();

                // Validate the status transition
                if (!isValidStatusTransition(currentStatus, newStatus)) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                // Update status
                article.setStatus(newStatus);
                Article updatedArticle = articleRepository.save(article);

                return new ResponseEntity<>(updatedArticle, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper method to verify if a status transition is valid
    private boolean isValidStatusTransition(ArticleStatus currentStatus, ArticleStatus newStatus) {
        // Same status is always valid
        if (currentStatus == newStatus) {
            return true;
        }

        // Define valid transitions
        switch (currentStatus) {
            case DRAFT:
                return newStatus == ArticleStatus.PUBLISHED || newStatus == ArticleStatus.REJECTED;
            case PUBLISHED:
                return newStatus == ArticleStatus.VERIFIED || newStatus == ArticleStatus.REJECTED;
            case VERIFIED:
                return newStatus == ArticleStatus.REJECTED;
            case REJECTED:
                return newStatus == ArticleStatus.DRAFT;
            default:
                return false;
        }
    }

    @GetMapping("/article/author/{authorId}")
    public ResponseEntity<List<Article>> getArticlesByAuthorId(@PathVariable String authorId) {
        try {
            // Validate that the author exists (optional)
            if (!userRepository.existsById(authorId)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            List<Article> articles = articleRepository.findByAuthorId(authorId);
            return new ResponseEntity<>(articles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/article/search")
    public ResponseEntity<List<Article>> searchArticles(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        try {
            List<Article> articles;

            // If keyword is provided, use the general search
            if (keyword != null && !keyword.isEmpty()) {
                articles = articleRepository.search(keyword);
            }
            // If title is provided, search by title
            else if (title != null && !title.isEmpty()) {
                articles = articleRepository.findByTitleContaining(title);
            }
            // If content is provided, search by content
            else if (content != null && !content.isEmpty()) {
                articles = articleRepository.findByContentContaining(content);
            }
            // If status is provided, search by status
            else if (status != null && !status.isEmpty()) {
                // Validate status (optional)
                List<String> validStatuses = Arrays.asList("DRAFT", "PUBLISHED", "VERIFIED", "REJECTED");
                if (!validStatuses.contains(status)) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                articles = articleRepository.findByStatus(status);
            }
            // If date range is provided, search by date range
            else if (startDate != null && endDate != null) {
                articles = articleRepository.findByPublicationDateBetween(startDate, endDate);
            }
            // Otherwise, get all articles
            else {
                articles = articleRepository.findAll();
            }

            return new ResponseEntity<>(articles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Hilfsmethode zur Überprüfung, ob ein Statusübergang gültig ist
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // Gültige Statuswerte
        List<String> validStatuses = Arrays.asList("DRAFT", "PUBLISHED", "VERIFIED", "REJECTED");

        // Prüfen, ob der neue Status gültig ist
        if (!validStatuses.contains(newStatus)) {
            return false;
        }

        // Spezifische Regeln für Statusübergänge
        // z.B. kann ein abgelehnter Artikel nicht veröffentlicht werden
        if (currentStatus.equals("REJECTED") && newStatus.equals("PUBLISHED")) {
            return false;
        }

        return true;
    }
}