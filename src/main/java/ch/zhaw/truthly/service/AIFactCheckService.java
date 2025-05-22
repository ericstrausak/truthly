package ch.zhaw.truthly.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ch.zhaw.truthly.model.FactCheckRating;

import java.util.Random;

@Service
public class AIFactCheckService {
    
    private static final Logger logger = LoggerFactory.getLogger(AIFactCheckService.class);
    private final Random random = new Random();
    
    public AIFactCheckResult performFactCheck(String title, String content) {
        try {
            logger.info("Starting MOCK AI fact-check for article: {}", title);
            
            // Simulate processing time
            Thread.sleep(500);
            
            // Mock AI logic based on content analysis
            FactCheckRating rating = generateMockRating(title, content);
            String explanation = generateMockExplanation(rating, title);
            
            logger.info("MOCK AI fact-check completed for article: {} with rating: {}", 
                       title, rating);
            
            return new AIFactCheckResult(rating, explanation);
            
        } catch (Exception e) {
            logger.error("Error during MOCK AI fact-check for article: {}", title, e);
            return new AIFactCheckResult(
                FactCheckRating.UNVERIFIABLE,
                "Mock AI fact-check service encountered an error. Manual review required."
            );
        }
    }
    
    private FactCheckRating generateMockRating(String title, String content) {
        // Simple mock logic - in reality this would be AI
        String lowerTitle = title.toLowerCase();
        String lowerContent = content.toLowerCase();
        
        // Look for suspicious words
        if (lowerTitle.contains("fake") || lowerTitle.contains("hoax") || 
            lowerContent.contains("conspiracy") || lowerContent.contains("unverified")) {
            return FactCheckRating.FALSE;
        }
        
        // Look for uncertainty indicators
        if (lowerContent.contains("maybe") || lowerContent.contains("possibly") || 
            lowerContent.contains("unclear") || lowerContent.contains("rumor")) {
            return FactCheckRating.UNVERIFIABLE;
        }
        
        // Look for partial truth indicators
        if (lowerContent.contains("partly") || lowerContent.contains("some") || 
            lowerContent.contains("partially")) {
            return FactCheckRating.PARTLY_TRUE;
        }
        
        // Default to TRUE for well-structured content
        if (content.length() > 100 && !lowerContent.contains("breaking")) {
            return FactCheckRating.TRUE;
        }
        
        // Random fallback
        FactCheckRating[] ratings = FactCheckRating.values();
        return ratings[random.nextInt(ratings.length)];
    }
    
    private String generateMockExplanation(FactCheckRating rating, String title) {
        switch (rating) {
            case TRUE:
                return "Mock AI Analysis: The article '" + title + "' appears to contain factual information based on language patterns and structure. Content seems well-sourced and objective.";
            case FALSE:
                return "Mock AI Analysis: The article '" + title + "' contains suspicious language patterns that suggest misinformation. Claims appear unsubstantiated.";
            case PARTLY_TRUE:
                return "Mock AI Analysis: The article '" + title + "' contains some accurate information but also includes claims that require further verification.";
            case UNVERIFIABLE:
                return "Mock AI Analysis: The article '" + title + "' contains claims that cannot be verified with current information. Manual fact-checking recommended.";
            default:
                return "Mock AI Analysis: Unable to determine factual accuracy of the article '" + title + "'.";
        }
    }
    
    // Inner class for AI result
    public static class AIFactCheckResult {
        private final FactCheckRating rating;
        private final String explanation;
        
        public AIFactCheckResult(FactCheckRating rating, String explanation) {
            this.rating = rating;
            this.explanation = explanation;
        }
        
        public FactCheckRating getRating() {
            return rating;
        }
        
        public String getExplanation() {
            return explanation;
        }
        
        @Override
        public String toString() {
            return "AIFactCheckResult{" +
                   "rating=" + rating +
                   ", explanation='" + explanation + '\'' +
                   '}';
        }
    }
}