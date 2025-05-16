package ch.zhaw.truthly.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.truthly.model.Article;
import java.util.List;

public interface ArticleRepository extends MongoRepository<Article, String> {
    List<Article> findByAuthorId(String authorId);
}