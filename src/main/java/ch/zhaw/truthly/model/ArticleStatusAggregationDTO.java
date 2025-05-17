package ch.zhaw.truthly.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ArticleStatusAggregationDTO {
    private String id;
    private List<String> articleIds;
    private String count;
}