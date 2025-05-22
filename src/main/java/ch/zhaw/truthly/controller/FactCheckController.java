package ch.zhaw.truthly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ch.zhaw.truthly.model.FactCheck;
import ch.zhaw.truthly.model.FactCheckCreateDTO;
import ch.zhaw.truthly.model.FactCheckRating;
import ch.zhaw.truthly.model.Article;
import ch.zhaw.truthly.model.ArticleStatus;
import ch.zhaw.truthly.repository.FactCheckRepository;
import ch.zhaw.truthly.repository.ArticleRepository;
import ch.zhaw.truthly.repository.UserRepository;
import ch.zhaw.truthly.service.AIFactCheckService;
import ch.zhaw.truthly.service.AIFactCheckService.AIFactCheckResult;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class FactCheckController {

    @Autowired
    FactCheckRepository factCheckRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    AIFactCheckService aiFactCheckService;

    @PostMapping("/factcheck")
    public ResponseEntity<FactCheck> createFactCheck(@RequestBody FactCheckCreateDTO factCheckDTO) {
        try {
            // Check if the article exists
            Optional<Article> articleOpt = articleRepository.findById(factCheckDTO.getArticleId());
            if (!articleOpt.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            Article article = articleOpt.get();
            
            // Perform AI fact-check first
            AIFactCheckResult aiResult = aiFactCheckService.performFactCheck(
                article.getTitle(), 
                article.getContent()
            );
            
            // Create FactCheck with AI result
            FactCheck factCheck = new FactCheck(
                factCheckDTO.getArticleId(),
                factCheckDTO.getCheckerId(),
                factCheckDTO.getResult(),
                factCheckDTO.getDescription(),
                aiResult.getRating().toString(),
                aiResult.getExplanation()
            );
            
            FactCheck savedFactCheck = factCheckRepository.save(factCheck);
            
            // Update article status based on human fact-checker decision
            if (factCheckDTO.getResult() == FactCheckRating.TRUE) {
                article.setStatus(ArticleStatus.VERIFIED);
            } else if (factCheckDTO.getResult() == FactCheckRating.FALSE) {
                article.setStatus(ArticleStatus.REJECTED);
            }
            articleRepository.save(article);
            
            return new ResponseEntity<>(savedFactCheck, HttpStatus.CREATED);
            
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/factcheck")
    public ResponseEntity<List<FactCheck>> getAllFactChecks() {
        try {
            List<FactCheck> factChecks = factCheckRepository.findAll();
            return new ResponseEntity<>(factChecks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/factcheck/{id}")
    public ResponseEntity<FactCheck> getFactCheckById(@PathVariable String id) {
        try {
            Optional<FactCheck> factCheckData = factCheckRepository.findById(id);

            if (factCheckData.isPresent()) {
                return new ResponseEntity<>(factCheckData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/factcheck/ai-verify/{articleId}")
    public ResponseEntity<?> performAIFactCheck(@PathVariable String articleId) {
        try {
            // Get the article
            Optional<Article> articleOpt = articleRepository.findById(articleId);
            if (!articleOpt.isPresent()) {
                return new ResponseEntity<>("Article not found", HttpStatus.NOT_FOUND);
            }

            Article article = articleOpt.get();

            // Perform AI fact-check
            AIFactCheckResult aiResult = aiFactCheckService.performFactCheck(
                    article.getTitle(),
                    article.getContent()
            );

            // Create response object
            Map<String, Object> response = new HashMap<>();
            response.put("articleId", articleId);
            response.put("articleTitle", article.getTitle());
            response.put("aiRating", aiResult.getRating().toString());
            response.put("aiExplanation", aiResult.getExplanation());
            response.put("verificationDate", new Date());

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("AI verification failed: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}