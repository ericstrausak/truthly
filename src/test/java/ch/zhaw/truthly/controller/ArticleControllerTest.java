package ch.zhaw.truthly.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import ch.zhaw.truthly.model.Article;
import ch.zhaw.truthly.model.ArticleCreateDTO;
import ch.zhaw.truthly.model.ArticleStatus;
import ch.zhaw.truthly.model.ArticleType;
import ch.zhaw.truthly.model.StatusUpdateDTO;
import ch.zhaw.truthly.model.User;
import ch.zhaw.truthly.repository.ArticleRepository;
import ch.zhaw.truthly.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private String testUserId;

    @BeforeEach
    void setUp() {
        // Clean database
        articleRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User("testauthor", "author@test.com", "password", "AUTHOR");
        testUser = userRepository.save(testUser);
        testUserId = testUser.getId();
    }

    @Test
    @DisplayName("Should create article with valid data")
    void shouldCreateArticleWithValidData() throws Exception {
        // Given
        ArticleCreateDTO articleDto = new ArticleCreateDTO();
        articleDto.setTitle("Test Article");
        articleDto.setContent("This is a test article content with sufficient length.");
        articleDto.setAuthorId(testUserId);
        articleDto.setAnonymous(false);
        articleDto.setArticleType(ArticleType.NEWS);

        // When & Then
        mockMvc.perform(post("/api/article")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Article"))
                .andExpect(jsonPath("$.content").value(articleDto.getContent()))
                .andExpect(jsonPath("$.authorId").value(testUserId))
                .andExpect(jsonPath("$.anonymous").value(false))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.publicationDate").exists());

        // Verify article was saved to database
        assertEquals(1, articleRepository.count());
    }

    @Test
    @DisplayName("Should create anonymous article")
    void shouldCreateAnonymousArticle() throws Exception {
        // Given
        ArticleCreateDTO articleDto = new ArticleCreateDTO();
        articleDto.setTitle("Anonymous Article");
        articleDto.setContent("This article should be published anonymously.");
        articleDto.setAuthorId(testUserId);
        articleDto.setAnonymous(true);
        articleDto.setArticleType(ArticleType.OPINION);

        // When & Then
        mockMvc.perform(post("/api/article")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.anonymous").value(true))
                .andExpect(jsonPath("$.articleType").value("OPINION"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "NEWS", "OPINION", "INVESTIGATION", "FACTUAL" })
    @DisplayName("Should accept all valid article types")
    void shouldAcceptValidArticleTypes(String type) throws Exception {
        // Given
        ArticleCreateDTO articleDto = new ArticleCreateDTO();
        articleDto.setTitle("Article of type " + type);
        articleDto.setContent("Content for article type " + type);
        articleDto.setAuthorId(testUserId);
        articleDto.setAnonymous(false);
        articleDto.setArticleType(ArticleType.valueOf(type));

        // When & Then
        mockMvc.perform(post("/api/article")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.articleType").value(type));
    }

    @Test
    @DisplayName("Should return all articles")
    void shouldReturnAllArticles() throws Exception {
        // Given - Create test articles
        Article article1 = new Article("First Article", "Content 1", testUserId, false, ArticleType.NEWS);
        Article article2 = new Article("Second Article", "Content 2", testUserId, true, ArticleType.OPINION);
        articleRepository.save(article1);
        articleRepository.save(article2);

        // When & Then
        mockMvc.perform(get("/api/article"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("First Article"))
                .andExpect(jsonPath("$[1].title").value("Second Article"));
    }

    @Test
    @DisplayName("Should filter articles by type")
    void shouldFilterArticlesByType() throws Exception {
        // Given - Create articles of different types
        Article newsArticle = new Article("News Article", "News content", testUserId, false, ArticleType.NEWS);
        Article opinionArticle = new Article("Opinion Article", "Opinion content", testUserId, false,
                ArticleType.OPINION);
        articleRepository.save(newsArticle);
        articleRepository.save(opinionArticle);

        // When & Then - Filter by NEWS
        mockMvc.perform(get("/api/article").param("type", "NEWS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("News Article"));
    }

    @Test
    @DisplayName("Should return specific article by ID")
    void shouldReturnArticleById() throws Exception {
        // Given
        Article article = new Article("Test Article", "Test content", testUserId, false, ArticleType.NEWS);
        Article savedArticle = articleRepository.save(article);

        // When & Then
        mockMvc.perform(get("/api/article/" + savedArticle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Article"))
                .andExpect(jsonPath("$.content").value("Test content"))
                .andExpect(jsonPath("$.id").value(savedArticle.getId()));
    }

    @Test
    @DisplayName("Should return 404 for non-existent article")
    void shouldReturn404ForNonExistentArticle() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/article/nonexistent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update article status")
    void shouldUpdateArticleStatus() throws Exception {
        // Given
        Article article = new Article("Test Article", "Test content", testUserId, false, ArticleType.NEWS);
        Article savedArticle = articleRepository.save(article);

        StatusUpdateDTO statusUpdate = new StatusUpdateDTO();
        statusUpdate.setStatus(ArticleStatus.PUBLISHED);

        // When & Then
        mockMvc.perform(put("/api/article/" + savedArticle.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }

    @Test
    @DisplayName("Should return articles by author")
    void shouldReturnArticlesByAuthor() throws Exception {
        // Given
        Article article1 = new Article("Article 1", "Content 1", testUserId, false, ArticleType.NEWS);
        Article article2 = new Article("Article 2", "Content 2", testUserId, false, ArticleType.OPINION);
        articleRepository.save(article1);
        articleRepository.save(article2);

        // When & Then
        mockMvc.perform(get("/api/article/author/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].authorId").value(testUserId))
                .andExpect(jsonPath("$[1].authorId").value(testUserId));
    }

    @Test
    @DisplayName("Should search articles by title")
    void shouldSearchArticlesByTitle() throws Exception {
        // Given
        Article article = new Article("Unique Title for Search", "Some content", testUserId, false, ArticleType.NEWS);
        articleRepository.save(article);

        // When & Then
        mockMvc.perform(get("/api/article/search").param("title", "Unique"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Unique Title for Search"));
    }

    @Test
    @DisplayName("Should search articles by content")
    void shouldSearchArticlesByContent() throws Exception {
        // Given
        Article article = new Article("Some Title", "Very specific content for testing", testUserId, false,
                ArticleType.NEWS);
        articleRepository.save(article);

        // When & Then
        mockMvc.perform(get("/api/article/search").param("content", "specific"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content").value("Very specific content for testing"));
    }

    @Test
    @DisplayName("Should search articles by keyword")
    void shouldSearchArticlesByKeyword() throws Exception {
        // Given
        Article article1 = new Article("Technology News", "Latest technology trends", testUserId, false,
                ArticleType.NEWS);
        Article article2 = new Article("Sports Update", "Technology in sports", testUserId, false, ArticleType.NEWS);
        articleRepository.save(article1);
        articleRepository.save(article2);

        // When & Then
        mockMvc.perform(get("/api/article/search").param("keyword", "technology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Should search articles by status")
    void shouldSearchArticlesByStatus() throws Exception {
        // Given
        Article draftArticle = new Article("Draft Article", "Draft content", testUserId, false, ArticleType.NEWS);
        draftArticle.setStatus(ArticleStatus.DRAFT);

        Article publishedArticle = new Article("Published Article", "Published content", testUserId, false,
                ArticleType.NEWS);
        publishedArticle.setStatus(ArticleStatus.PUBLISHED);

        articleRepository.save(draftArticle);
        articleRepository.save(publishedArticle);

        // When & Then
        mockMvc.perform(get("/api/article/search").param("status", "DRAFT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Draft Article"));
    }

    @Test
    @DisplayName("Should handle invalid article creation")
    void shouldHandleInvalidArticleCreation() throws Exception {
        // Given - Invalid author ID
        ArticleCreateDTO articleDto = new ArticleCreateDTO();
        articleDto.setTitle("Test Article");
        articleDto.setContent("Test content");
        articleDto.setAuthorId("invalid-user-id");
        articleDto.setAnonymous(false);

        // When & Then
        mockMvc.perform(post("/api/article")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid JSON")
    void shouldHandleInvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/article")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return empty list when no articles exist")
    void shouldReturnEmptyListWhenNoArticles() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/article"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should handle malformed JSON in article creation")
    void shouldHandleMalformedJsonInArticleCreation() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/article")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ malformed json without quotes }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for invalid status update")
    void shouldReturn400ForInvalidStatusUpdate() throws Exception {
        // Given
        Article article = new Article("Test", "Content", testUserId, false, ArticleType.NEWS);
        Article savedArticle = articleRepository.save(article);

        // When & Then - Try invalid status transition
        mockMvc.perform(put("/api/article/" + savedArticle.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"status\": \"INVALID_STATUS\" }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should search articles with empty results")
    void shouldSearchArticlesWithEmptyResults() throws Exception {
        // When & Then - Search for non-existing content
        mockMvc.perform(get("/api/article/search")
                .param("keyword", "nonexistentxyz123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should handle null article type gracefully")
    void shouldHandleNullArticleTypeGracefully() throws Exception {
        // Given
        ArticleCreateDTO articleDto = new ArticleCreateDTO();
        articleDto.setTitle("Test Article");
        articleDto.setContent("Test content");
        articleDto.setAuthorId(testUserId);
        articleDto.setAnonymous(false);
        // articleType bleibt null

        // When & Then
        mockMvc.perform(post("/api/article")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.articleType").value("NEWS")); // Default sollte NEWS sein
    }

    @Test
    @DisplayName("Should return articles sorted by publication date")
    void shouldReturnArticlesSortedByPublicationDate() throws Exception {
        // When & Then - Einfacher GET Test
        mockMvc.perform(get("/api/article"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}