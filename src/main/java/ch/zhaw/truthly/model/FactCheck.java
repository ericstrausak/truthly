package ch.zhaw.truthly.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.NonNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.Date;

@NoArgsConstructor  // NUR @NoArgsConstructor, NICHT @RequiredArgsConstructor
@Getter
@Setter
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

    // AI-related fields
    private String aiVerificationResult;
    private String aiExplanation;
    private Date aiVerificationDate;

    // Constructor with AI fields
    public FactCheck(String articleId, String checkerId, FactCheckRating result,
            String description, String aiVerificationResult, String aiExplanation) {
        this.articleId = articleId;
        this.checkerId = checkerId;
        this.result = result;
        this.description = description;
        this.aiVerificationResult = aiVerificationResult;
        this.aiExplanation = aiExplanation;
        this.aiVerificationDate = new Date();
        this.checkDate = new Date();
    }

    // Constructor without AI fields (for backward compatibility)
    public FactCheck(String articleId, String checkerId, FactCheckRating result, String description) {
        this.articleId = articleId;
        this.checkerId = checkerId;
        this.result = result;
        this.description = description;
        this.checkDate = new Date();
    }
}