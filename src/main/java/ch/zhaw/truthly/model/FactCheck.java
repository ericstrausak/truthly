package ch.zhaw.truthly.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Date;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Document("factcheck")
public class FactCheck {
    @Id
    private String id;
    
    @NonNull
    private String articleId;
    
    @NonNull
    private String checkerId;
    
    @NonNull
    private FactCheckRating result;
    
    @NonNull
    private String description;
    
    private Date checkDate = new Date();
    
    private String aiVerificationResult;
}