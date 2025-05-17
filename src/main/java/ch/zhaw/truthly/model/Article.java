package ch.zhaw.truthly.model;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
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

    private ArticleStatus status = ArticleStatus.DRAFT; // Now using enum

    @NonNull
    private boolean isAnonymous;

    @NonNull
    private ArticleType articleType;

    public void setStatus(ArticleStatus status) {
        this.status = status;
    }
}