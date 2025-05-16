// Article.java (update)
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

    private String status = "DRAFT"; // DRAFT, PUBLISHED, VERIFIED, REJECTED

    @NonNull
    private boolean isAnonymous;
    
    @NonNull
    private ArticleType articleType; // New field

    public void setStatus(String status) {
        this.status = status;
    }
}