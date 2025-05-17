package ch.zhaw.truthly.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ch.zhaw.truthly.model.Article;
import ch.zhaw.truthly.model.ArticleType;

import java.util.List;
import java.util.Date;

public interface ArticleRepository extends MongoRepository<Article, String> {
    List<Article> findByAuthorId(String authorId);

    List<Article> findByArticleType(ArticleType articleType);

    List<Article> findByAuthorIdIn(List<String> authorIds);

    @Query("{ 'title' : { $regex: ?0, $options: 'i' } }")
    List<Article> findByTitleContaining(String title);

    @Query("{ 'content' : { $regex: ?0, $options: 'i' } }")
    List<Article> findByContentContaining(String content);

    @Query("{ 'status' : ?0 }")
    List<Article> findByStatus(String status);

    @Query("{ 'publicationDate' : { $gte: ?0, $lte: ?1 } }")
    List<Article> findByPublicationDateBetween(Date start, Date end);

    @Query("{ $or: [ { 'title' : { $regex: ?0, $options: 'i' } }, { 'content' : { $regex: ?0, $options: 'i' } } ] }")
    List<Article> search(String keyword);
}