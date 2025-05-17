package ch.zhaw.truthly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ch.zhaw.truthly.model.Article;
import ch.zhaw.truthly.model.ArticleStateChangeDTO;
import ch.zhaw.truthly.model.FactCheckCompleteDTO;
import ch.zhaw.truthly.service.ArticleService;
import ch.zhaw.truthly.repository.ArticleRepository;
import java.util.Optional;

@RestController
@RequestMapping("/api/service")
public class ArticleServiceController {

    @Autowired
    ArticleService articleService;
    
    @Autowired
    ArticleRepository articleRepository;
    
    @PutMapping("/assignarticle")
    public ResponseEntity<Article> assignArticleForChecking(@RequestBody ArticleStateChangeDTO changeDTO) {
        String checkerId = changeDTO.getCheckerId();
        String articleId = changeDTO.getArticleId();
        
        Optional<Article> article = articleService.assignArticleForChecking(articleId, checkerId);
        
        if (article.isPresent()) {
            return new ResponseEntity<>(article.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/completechecking")
public ResponseEntity<Article> completeFactChecking(@RequestBody FactCheckCompleteDTO completeDTO) {
    String checkerId = completeDTO.getCheckerId();
    String articleId = completeDTO.getArticleId();
    String result = completeDTO.getResult();
    
    Optional<Article> article = articleService.completeFactChecking(articleId, checkerId, result);
    
    if (article.isPresent()) {
        return new ResponseEntity<>(article.get(), HttpStatus.OK);
    } else {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
    
    // We'll add more endpoints in the subsequent issues
}