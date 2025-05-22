package ch.zhaw.truthly;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import ch.zhaw.truthly.service.AIFactCheckService;
import ch.zhaw.truthly.model.FactCheckRating;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    @Profile("test")
    public AIFactCheckService mockAIFactCheckService() {
        return new AIFactCheckService() {
            @Override
            public AIFactCheckResult performFactCheck(String title, String content) {
                // Deterministic mock responses for testing
                if (title.toLowerCase().contains("fake") || title.toLowerCase().contains("hoax")) {
                    return new AIFactCheckResult(FactCheckRating.FALSE, "Test AI detected fake news");
                } else if (title.toLowerCase().contains("scientific") || title.toLowerCase().contains("research")) {
                    return new AIFactCheckResult(FactCheckRating.TRUE, "Test AI detected credible content");
                } else {
                    return new AIFactCheckResult(FactCheckRating.UNVERIFIABLE, "Test AI could not determine");
                }
            }
        };
    }
}