package ch.zhaw.truthly.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ArticleStateChangeDTO {
    private String articleId;
    private String checkerId;
}