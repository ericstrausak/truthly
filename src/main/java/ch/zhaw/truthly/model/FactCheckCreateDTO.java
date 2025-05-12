package ch.zhaw.truthly.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FactCheckCreateDTO {
    private String articleId;
    private String checkerId;
    private String result; // z.B. "TRUE", "FALSE", "PARTLY_TRUE"
    private String description;
}