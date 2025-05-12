package ch.zhaw.truthly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import ch.zhaw.truthly.model.FactCheck;
import ch.zhaw.truthly.model.FactCheckCreateDTO;
import ch.zhaw.truthly.model.Article;
import ch.zhaw.truthly.repository.FactCheckRepository;
import ch.zhaw.truthly.repository.ArticleRepository;
import ch.zhaw.truthly.repository.UserRepository;
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
            // Prüfen, ob der Artikel existiert
            Optional<Article> articleOpt = articleRepository.findById(factCheckDTO.getArticleId());
            if (!articleOpt.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            // Prüfen, ob der Prüfer (Checker) existiert
            // Dieser Teil kann optional sein, je nachdem, ob Sie die User-Endpoints bereits implementiert haben
            /*
            Optional<User> checkerOpt = userRepository.findById(factCheckDTO.getCheckerId());
            if (!checkerOpt.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            */
            
            // FactCheck erstellen
            FactCheck factCheck = new FactCheck(
                factCheckDTO.getArticleId(),
                factCheckDTO.getCheckerId(),
                factCheckDTO.getResult(),
                factCheckDTO.getDescription()
            );
            
            // Hier könnten Sie einen KI-Service aufrufen, um eine zusätzliche Verifizierung zu erhalten
            // factCheck.setAiVerificationResult(aiService.verify(articleOpt.get().getContent()));
            
            FactCheck savedFactCheck = factCheckRepository.save(factCheck);
            
            // Optional: Den Status des Artikels aktualisieren
            Article article = articleOpt.get();
            if (factCheckDTO.getResult().equals("TRUE")) {
                article.setStatus("VERIFIED");
            } else if (factCheckDTO.getResult().equals("FALSE")) {
                article.setStatus("REJECTED");
            }
            articleRepository.save(article);
            
            return new ResponseEntity<>(savedFactCheck, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}