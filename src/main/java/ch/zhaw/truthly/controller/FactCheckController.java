package ch.zhaw.truthly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ch.zhaw.truthly.model.FactCheck;
import ch.zhaw.truthly.model.FactCheckCreateDTO;
import ch.zhaw.truthly.model.Article;
import ch.zhaw.truthly.model.ArticleStatus;
import ch.zhaw.truthly.repository.FactCheckRepository;
import ch.zhaw.truthly.repository.ArticleRepository;
import ch.zhaw.truthly.repository.UserRepository;
import java.util.List;
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

    @PostMapping("/factcheck")
    public ResponseEntity<FactCheck> createFactCheck(@RequestBody FactCheckCreateDTO factCheckDTO) {
        try {
            // Check if the article exists
            Optional<Article> articleOpt = articleRepository.findById(factCheckDTO.getArticleId());
            if (!articleOpt.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Create FactCheck
            FactCheck factCheck = new FactCheck(
                    factCheckDTO.getArticleId(),
                    factCheckDTO.getCheckerId(),
                    factCheckDTO.getResult(),
                    factCheckDTO.getDescription());

            FactCheck savedFactCheck = factCheckRepository.save(factCheck);

            // Update the article status
            Article article = articleOpt.get();
            if (factCheckDTO.getResult().equals("TRUE")) {
                article.setStatus(ArticleStatus.VERIFIED);
            } else if (factCheckDTO.getResult().equals("FALSE")) {
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
}