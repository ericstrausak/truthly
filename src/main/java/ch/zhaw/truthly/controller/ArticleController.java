package ch.zhaw.truthly.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import ch.zhaw.truthly.model.Article;
import ch.zhaw.truthly.model.ArticleCreateDTO;
import ch.zhaw.truthly.repository.ArticleRepository;

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
                    articleDTO.isAnonymous());
            Article savedArticle = articleRepository.save(article);
            return new ResponseEntity<>(savedArticle, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/article")
    public ResponseEntity<List<Article>> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    @GetMapping("/article/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable String id) {
        Optional<Article> articleData = articleRepository.findById(id);
        if (articleData.isPresent()) {
            return new ResponseEntity<>(articleData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}