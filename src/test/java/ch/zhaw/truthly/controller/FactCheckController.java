package ch.zhaw.truthly.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import ch.zhaw.truthly.model.Article;
import ch.zhaw.truthly.model.ArticleStatus;
import ch.zhaw.truthly.model.ArticleType;
import ch.zhaw.truthly.model.FactCheck;
import ch.zhaw.truthly.model.FactCheckCreateDTO;
import ch.zhaw.truthly.model.FactCheckRating;
import ch.zhaw.truthly.model.User;
import ch.zhaw.truthly.repository.ArticleRepository;
import ch.zhaw.truthly.repository.FactCheckRepository;
import ch.zhaw.truthly.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FactCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FactCheckRepository factCheckRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    private User testAuthor;
    private User testChecker;
    private Article testArticle;
    private String testArticleId;
    private String testCheckerId;

    @BeforeEach
    void setUp() {
        // Clean database
        factCheckRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        testAuthor = new User("testauthor", "author@test.com", "password", "AUTHOR");
        testAuthor = userRepository.save(testAuthor);

        testChecker = new User("testchecker", "checker@test.com", "password", "FACT_CHECKER");
        testChecker = userRepository.save(testChecker);
        testCheckerId = testChecker.getId();

        // Create test article
        testArticle = new Article(
                "Test Article for Fact Checking",
                "This article contains claims that need to be verified.",
                testAuthor.getId(),
                false,
                ArticleType.NEWS);
        testArticle = articleRepository.save(testArticle);
        testArticleId = testArticle.getId();
    }

    @Test
    @DisplayName("Should create fact check with AI integration")
    void shouldCreateFactCheckWithAI() throws Exception {
        // Given
        FactCheckCreateDTO factCheckDto = new FactCheckCreateDTO();
        factCheckDto.setArticleId(testArticleId);
        factCheckDto.setCheckerId(testCheckerId);
        factCheckDto.setResult(FactCheckRating.TRUE);
        factCheckDto.setDescription("Article content has been verified and is accurate.");

        // When & Then
        mockMvc.perform(post("/api/factcheck")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(factCheckDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.articleId").value(testArticleId))
                .andExpect(jsonPath("$.checkerId").value(testCheckerId))
                .andExpect(jsonPath("$.result").value("TRUE"))
                .andExpect(jsonPath("$.description").value("Article content has been verified and is accurate."))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.checkDate").exists())
                .andExpect(jsonPath("$.aiVerificationResult").exists()) // AI result should be present
                .andExpect(jsonPath("$.aiExplanation").exists()); // AI explanation should be present

        // Verify fact check was saved to database
        assertEquals(1, factCheckRepository.count());

        // Verify article status was updated
        Article updatedArticle = articleRepository.findById(testArticleId).orElse(null);
        assertNotNull(updatedArticle);
        assertEquals(ArticleStatus.VERIFIED, updatedArticle.getStatus());
    }

    @ParameterizedTest
    @EnumSource(FactCheckRating.class)
    @DisplayName("Should accept all valid fact check ratings")
    void shouldAcceptValidFactCheckRatings(FactCheckRating rating) throws Exception {
        // Given
        FactCheckCreateDTO factCheckDto = new FactCheckCreateDTO();
        factCheckDto.setArticleId(testArticleId);
        factCheckDto.setCheckerId(testCheckerId);
        factCheckDto.setResult(rating);
        factCheckDto.setDescription("Test fact check with rating: " + rating);

        // When & Then
        mockMvc.perform(post("/api/factcheck")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(factCheckDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result").value(rating.toString()));
    }

    @Test
    @DisplayName("Should update article status to REJECTED for FALSE rating")
    void shouldUpdateArticleStatusToRejectedForFalseRating() throws Exception {
        // Given
        FactCheckCreateDTO factCheckDto = new FactCheckCreateDTO();
        factCheckDto.setArticleId(testArticleId);
        factCheckDto.setCheckerId(testCheckerId);
        factCheckDto.setResult(FactCheckRating.FALSE);
        factCheckDto.setDescription("Article contains false information.");

        // When
        mockMvc.perform(post("/api/factcheck")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(factCheckDto)))
                .andExpect(status().isCreated());

        // Then - Verify article status was updated
        Article updatedArticle = articleRepository.findById(testArticleId).orElse(null);
        assertNotNull(updatedArticle);
        assertEquals(ArticleStatus.REJECTED, updatedArticle.getStatus());
    }

    @Test
    @DisplayName("Should return all fact checks")
    void shouldReturnAllFactChecks() throws Exception {
        // Given - Create test fact checks
        FactCheck factCheck1 = new FactCheck(testArticleId, testCheckerId, FactCheckRating.TRUE, "Verified as true");
        FactCheck factCheck2 = new FactCheck(testArticleId, testCheckerId, FactCheckRating.FALSE, "Found to be false");
        factCheckRepository.save(factCheck1);
        factCheckRepository.save(factCheck2);

        // When & Then
        mockMvc.perform(get("/api/factcheck"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].result").value("TRUE"))
                .andExpect(jsonPath("$[1].result").value("FALSE"));
    }

    @Test
    @DisplayName("Should return specific fact check by ID")
    void shouldReturnFactCheckById() throws Exception {
        // Given
        FactCheck factCheck = new FactCheck(testArticleId, testCheckerId, FactCheckRating.PARTLY_TRUE,
                "Partially accurate");
        FactCheck savedFactCheck = factCheckRepository.save(factCheck);

        // When & Then
        mockMvc.perform(get("/api/factcheck/" + savedFactCheck.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("PARTLY_TRUE"))
                .andExpect(jsonPath("$.description").value("Partially accurate"))
                .andExpect(jsonPath("$.id").value(savedFactCheck.getId()));
    }

    @Test
    @DisplayName("Should return 404 for non-existent fact check")
    void shouldReturn404ForNonExistentFactCheck() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/factcheck/nonexistent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should perform AI-only verification")
    void shouldPerformAIOnlyVerification() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/factcheck/ai-verify/" + testArticleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(testArticleId))
                .andExpect(jsonPath("$.articleTitle").value("Test Article for Fact Checking"))
                .andExpect(jsonPath("$.aiRating").exists())
                .andExpect(jsonPath("$.aiExplanation").exists())
                .andExpect(jsonPath("$.verificationDate").exists());
    }

    @Test
    @DisplayName("Should return 404 for AI verification of non-existent article")
    void shouldReturn404ForAIVerificationOfNonExistentArticle() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/factcheck/ai-verify/nonexistent-id"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Article not found"));
    }

    @Test
    @DisplayName("Should handle fact check creation with non-existent article")
    void shouldHandleFactCheckCreationWithNonExistentArticle() throws Exception {
        // Given
        FactCheckCreateDTO factCheckDto = new FactCheckCreateDTO();
        factCheckDto.setArticleId("nonexistent-article-id");
        factCheckDto.setCheckerId(testCheckerId);
        factCheckDto.setResult(FactCheckRating.TRUE);
        factCheckDto.setDescription("Test description");

        // When & Then
        mockMvc.perform(post("/api/factcheck")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(factCheckDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle invalid JSON")
    void shouldHandleInvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/factcheck")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return empty list when no fact checks exist")
    void shouldReturnEmptyListWhenNoFactChecks() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/factcheck"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should include AI results in fact check response")
    void shouldIncludeAIResultsInFactCheckResponse() throws Exception {
        // Given
        FactCheckCreateDTO factCheckDto = new FactCheckCreateDTO();
        factCheckDto.setArticleId(testArticleId);
        factCheckDto.setCheckerId(testCheckerId);
        factCheckDto.setResult(FactCheckRating.UNVERIFIABLE);
        factCheckDto.setDescription("Unable to verify claims in this article.");

        // When & Then
        mockMvc.perform(post("/api/factcheck")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(factCheckDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.aiVerificationResult").isNotEmpty())
                .andExpect(jsonPath("$.aiExplanation").isNotEmpty())
                .andExpect(jsonPath("$.aiVerificationDate").exists());

        // Verify the AI result was stored
        FactCheck savedFactCheck = factCheckRepository.findAll().get(0);
        assertNotNull(savedFactCheck.getAiVerificationResult());
        assertNotNull(savedFactCheck.getAiExplanation());
        assertNotNull(savedFactCheck.getAiVerificationDate());
    }

    @Test
    @DisplayName("Should handle AI verification with suspicious content")
    void shouldHandleAIVerificationWithSuspiciousContent() throws Exception {
        // Given - Create article with suspicious content
        Article suspiciousArticle = new Article(
                "Breaking: Fake Moon Landing Conspiracy",
                "This article contains conspiracy theories and hoax claims that are unverified.",
                testAuthor.getId(),
                false,
                ArticleType.NEWS);
        suspiciousArticle = articleRepository.save(suspiciousArticle);

        // When & Then
        mockMvc.perform(post("/api/factcheck/ai-verify/" + suspiciousArticle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aiRating").value("FALSE"))
                .andExpect(jsonPath("$.aiExplanation").value(containsString("suspicious")));
    }
}