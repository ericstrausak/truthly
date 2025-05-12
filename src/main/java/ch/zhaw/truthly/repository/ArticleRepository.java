package ch.zhaw.truthly.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.truthly.model.Article;

public interface ArticleRepository extends MongoRepository<Article, String> {
}