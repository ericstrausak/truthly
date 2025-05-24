package ch.zhaw.truthly.controller;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import ch.zhaw.truthly.model.User;
import ch.zhaw.truthly.model.Article;
import ch.zhaw.truthly.model.ArticleType;
import ch.zhaw.truthly.model.ArticleStatus;
import ch.zhaw.truthly.repository.ArticleRepository;
import ch.zhaw.truthly.repository.UserRepository;
import ch.zhaw.truthly.model.ArticleStateChangeDTO;
import ch.zhaw.truthly.model.FactCheckCompleteDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ArticleServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    private User testAuthor;
    private User testChecker;
    private Article testArticle;

    @BeforeEach
    void setUp() {
        articleRepository.deleteAll();
        userRepository.deleteAll();

        testAuthor = new User("author", "author@test.com", "pass", "AUTHOR");
        testAuthor = userRepository.save(testAuthor);

        testChecker = new User("checker", "checker@test.com", "pass", "FACT_CHECKER");
        testChecker = userRepository.save(testChecker);

        testArticle = new Article("Test Article", "Content", testAuthor.getId(), false, ArticleType.NEWS);
        testArticle.setStatus(ArticleStatus.PUBLISHED);
        testArticle = articleRepository.save(testArticle);
    }

    @Test
    @DisplayName("Should assign article for checking")
    void shouldAssignArticleForChecking() throws Exception {
        ArticleStateChangeDTO dto = new ArticleStateChangeDTO();
        dto.setArticleId(testArticle.getId());
        dto.setCheckerId(testChecker.getId());

        mockMvc.perform(put("/api/service/assignarticle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CHECKING"));
    }

    @Test
    @DisplayName("Should complete fact checking")
    void shouldCompleteFactChecking() throws Exception {
        // First assign for checking
        testArticle.setStatus(ArticleStatus.CHECKING);
        articleRepository.save(testArticle);

        FactCheckCompleteDTO dto = new FactCheckCompleteDTO();
        dto.setArticleId(testArticle.getId());
        dto.setCheckerId(testChecker.getId());
        dto.setResult("VERIFIED");

        mockMvc.perform(put("/api/service/completechecking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VERIFIED"));
    }

    @Test
    @DisplayName("Should get article status aggregation")
    void shouldGetArticleStatusAggregation() throws Exception {
        mockMvc.perform(get("/api/service/articledashboard")
                .param("author", testAuthor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should handle invalid author for aggregation")
    void shouldHandleInvalidAuthorForAggregation() throws Exception {
        mockMvc.perform(get("/api/service/articledashboard")
                .param("author", "invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle empty author ID in aggregation request")
    void shouldHandleEmptyAuthorIdInAggregation() throws Exception {
        mockMvc.perform(get("/api/service/articledashboard")
                .param("author", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle null values in assign article request")
    void shouldHandleNullValuesInAssignArticleRequest() throws Exception {
        ArticleStateChangeDTO dto = new ArticleStateChangeDTO();
        // Leave articleId and checkerId as null

        mockMvc.perform(put("/api/service/assignarticle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle null values in complete checking request")
    void shouldHandleNullValuesInCompleteCheckingRequest() throws Exception {
        FactCheckCompleteDTO dto = new FactCheckCompleteDTO();
        // Leave all fields as null

        mockMvc.perform(put("/api/service/completechecking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle empty result string in complete checking")
    void shouldHandleEmptyResultStringInCompleteChecking() throws Exception {
        testArticle.setStatus(ArticleStatus.CHECKING);
        articleRepository.save(testArticle);

        FactCheckCompleteDTO dto = new FactCheckCompleteDTO();
        dto.setArticleId(testArticle.getId());
        dto.setCheckerId(testChecker.getId());
        dto.setResult(""); // Empty string

        mockMvc.perform(put("/api/service/completechecking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle whitespace-only result string")
    void shouldHandleWhitespaceOnlyResultString() throws Exception {
        testArticle.setStatus(ArticleStatus.CHECKING);
        articleRepository.save(testArticle);

        FactCheckCompleteDTO dto = new FactCheckCompleteDTO();
        dto.setArticleId(testArticle.getId());
        dto.setCheckerId(testChecker.getId());
        dto.setResult("   "); // Whitespace only

        mockMvc.perform(put("/api/service/completechecking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

}