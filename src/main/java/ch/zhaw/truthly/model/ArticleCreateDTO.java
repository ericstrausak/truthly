// ArticleCreateDTO.java (update)
package ch.zhaw.truthly.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ArticleCreateDTO {
    private String title;
    private String content;
    private String authorId;
    private boolean isAnonymous;
    private ArticleType articleType; // New field
}