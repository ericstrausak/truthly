package ch.zhaw.truthly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ch.zhaw.truthly.model.Article;
import ch.zhaw.truthly.model.ArticleCreateDTO;
import ch.zhaw.truthly.model.StatusUpdateDTO;
import ch.zhaw.truthly.repository.ArticleRepository;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

@RestController
@RequestMapping("/api")
public class ArticleController {

    @Autowired
    ArticleRepository articleRepository;
    
    @PostMapping("/article")
    public ResponseEntity<Article> createArticle(@RequestBody ArticleCreateDTO articleDTO) {
        try {
            Article article = new Article(
                articleDTO.getTitle(),
                articleDTO.getContent(),
                articleDTO.getAuthorId(),
                articleDTO.isAnonymous()
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
        @RequestBody StatusUpdateDTO statusDTO
    ) {
        try {
            Optional<Article> articleData = articleRepository.findById(id);
            
            if (articleData.isPresent()) {
                Article article = articleData.get();
                
                // Validieren des Status
                String newStatus = statusDTO.getStatus();
                if (!isValidStatusTransition(article.getStatus(), newStatus)) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                
                // Status aktualisieren
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