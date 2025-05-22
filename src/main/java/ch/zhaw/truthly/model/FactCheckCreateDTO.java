package ch.zhaw.truthly.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class FactCheckCreateDTO {
    private String articleId;
    private String checkerId;
    private FactCheckRating result;
    private String description;
}