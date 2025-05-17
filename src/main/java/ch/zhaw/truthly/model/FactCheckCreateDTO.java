package ch.zhaw.truthly.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FactCheckCreateDTO {
    private String articleId;
    private String checkerId;
    private FactCheckRating result;
    private String description;
}