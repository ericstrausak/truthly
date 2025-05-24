package ch.zhaw.truthly.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ch.zhaw.truthly.model.FactCheckRating;
import ch.zhaw.truthly.service.AIFactCheckService.AIFactCheckResult;
import static org.junit.jupiter.api.Assertions.*;

class AIFactCheckServiceTest {

    private final AIFactCheckService aiService = new AIFactCheckService();

    @Test
    @DisplayName("Should return result for normal content")
    void shouldReturnResultForNormalContent() {
        AIFactCheckResult result = aiService.performFactCheck("Normal Article", "This is normal content");
        assertNotNull(result);
        assertNotNull(result.getRating());
        assertNotNull(result.getExplanation());
    }

    @Test
    @DisplayName("Should handle suspicious content")
    void shouldHandleSuspiciousContent() {
        AIFactCheckResult result = aiService.performFactCheck("Fake News", "This is fake information");
        assertNotNull(result);
        assertEquals(FactCheckRating.FALSE, result.getRating());
    }

    @Test
    @DisplayName("Should handle uncertain content")
    void shouldHandleUncertainContent() {
        AIFactCheckResult result = aiService.performFactCheck("Maybe True", "This maybe happened");
        assertNotNull(result);
        assertEquals(FactCheckRating.UNVERIFIABLE, result.getRating());
    }

    @Test
    @DisplayName("Should handle null inputs")
    void shouldHandleNullInputs() {
        AIFactCheckResult result1 = aiService.performFactCheck(null, "content");
        AIFactCheckResult result2 = aiService.performFactCheck("title", null);
        assertEquals(FactCheckRating.UNVERIFIABLE, result1.getRating());
        assertEquals(FactCheckRating.UNVERIFIABLE, result2.getRating());
    }

    @Test
    @DisplayName("Should handle empty inputs")
    void shouldHandleEmptyInputs() {
        AIFactCheckResult result = aiService.performFactCheck("", "");
        assertNotNull(result);
        assertNotNull(result.getRating());
    }

    @Test
    @DisplayName("Should handle long content")
    void shouldHandleLongContent() {
        String longContent = "This is a very detailed article. ".repeat(50);
        AIFactCheckResult result = aiService.performFactCheck("Long Article", longContent);
        assertNotNull(result);
        assertEquals(FactCheckRating.TRUE, result.getRating());
    }

    @Test
    @DisplayName("Should handle partial truth content")
    void shouldHandlePartialTruthContent() {
        AIFactCheckResult result = aiService.performFactCheck("Partly True", "Some information is partially accurate");
        assertNotNull(result);
        assertEquals(FactCheckRating.PARTLY_TRUE, result.getRating());
    }

    @Test
    @DisplayName("Should generate explanations")
    void shouldGenerateExplanations() {
        AIFactCheckResult result = aiService.performFactCheck("Test", "Test content");
        assertNotNull(result.getExplanation());
        assertTrue(result.getExplanation().length() > 10);
        assertTrue(result.getExplanation().contains("Test"));
    }
}