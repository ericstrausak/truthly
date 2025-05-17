package ch.zhaw.truthly.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FactCheckCompleteDTO {
    private String articleId;
    private String checkerId;
    private String result; // VERIFIED or REJECTED
}