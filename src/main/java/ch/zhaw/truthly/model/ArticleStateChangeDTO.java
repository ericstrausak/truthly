package ch.zhaw.truthly.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ArticleStateChangeDTO {
    private String articleId;
    private String checkerId;
}