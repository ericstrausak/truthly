package ch.zhaw.truthly.model;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.NonNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Setter
@Document("article")
public class Article {
    @Id
    private String id;

    @NonNull
    private String title;

    @NonNull
    private String content;

    @NonNull
    private String authorId;

    private Date publicationDate = new Date();

    private ArticleStatus status = ArticleStatus.DRAFT;

    private boolean isAnonymous = false;  // Nicht @NonNull für boolean

    private ArticleType articleType = ArticleType.NEWS;  // Default-Wert

    // Konstruktor für Tests (mit allen Parametern)
    public Article(String title, String content, String authorId, boolean isAnonymous, ArticleType articleType) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.isAnonymous = isAnonymous;
        this.articleType = articleType != null ? articleType : ArticleType.NEWS;
        this.publicationDate = new Date();
        this.status = ArticleStatus.DRAFT;
    }

    // Zusätzlicher Konstruktor ohne ArticleType (für Backward Compatibility)
    public Article(String title, String content, String authorId, boolean isAnonymous) {
        this(title, content, authorId, isAnonymous, ArticleType.NEWS);
    }

    public void setStatus(ArticleStatus status) {
        this.status = status;
    }
}