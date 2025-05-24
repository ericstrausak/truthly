package ch.zhaw.truthly.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

class ModelTest {

    @Test
    @DisplayName("Should create User with all fields")
    void shouldCreateUserWithAllFields() {
        User user = new User("testuser", "test@example.com", "password", "USER");
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals("USER", user.getRole());
        assertNotNull(user.getRegistrationDate());
    }

    @Test
    @DisplayName("Should create Article with default status")
    void shouldCreateArticleWithDefaultStatus() {
        Article article = new Article("Title", "Content", "authorId", false, ArticleType.NEWS);
        assertEquals("Title", article.getTitle());
        assertEquals("Content", article.getContent());
        assertEquals("authorId", article.getAuthorId());
        assertFalse(article.isAnonymous());
        assertEquals(ArticleType.NEWS, article.getArticleType());
        assertEquals(ArticleStatus.DRAFT, article.getStatus());
        assertNotNull(article.getPublicationDate());
    }

    @Test
    @DisplayName("Should create FactCheck with AI fields")
    void shouldCreateFactCheckWithAIFields() {
        FactCheck factCheck = new FactCheck("articleId", "checkerId", 
            FactCheckRating.TRUE, "description", "AI_TRUE", "AI explanation");
        assertEquals("articleId", factCheck.getArticleId());
        assertEquals("checkerId", factCheck.getCheckerId());
        assertEquals(FactCheckRating.TRUE, factCheck.getResult());
        assertEquals("description", factCheck.getDescription());
        assertEquals("AI_TRUE", factCheck.getAiVerificationResult());
        assertEquals("AI explanation", factCheck.getAiExplanation());
        assertNotNull(factCheck.getCheckDate());
        assertNotNull(factCheck.getAiVerificationDate());
    }

    @Test
    @DisplayName("Should create FactCheck without AI fields")
    void shouldCreateFactCheckWithoutAIFields() {
        FactCheck factCheck = new FactCheck("articleId", "checkerId", FactCheckRating.FALSE, "description");
        assertEquals("articleId", factCheck.getArticleId());
        assertEquals("checkerId", factCheck.getCheckerId());
        assertEquals(FactCheckRating.FALSE, factCheck.getResult());
        assertEquals("description", factCheck.getDescription());
        assertNotNull(factCheck.getCheckDate());
        assertNull(factCheck.getAiVerificationResult());
        assertNull(factCheck.getAiExplanation());
        assertNull(factCheck.getAiVerificationDate());
    }

    @Test
    @DisplayName("Should test ArticleStatus enum")
    void shouldTestArticleStatusEnum() {
        ArticleStatus[] statuses = ArticleStatus.values();
        assertEquals(5, statuses.length);
        assertTrue(Arrays.asList(statuses).contains(ArticleStatus.DRAFT));
        assertTrue(Arrays.asList(statuses).contains(ArticleStatus.PUBLISHED));
        assertTrue(Arrays.asList(statuses).contains(ArticleStatus.CHECKING));
        assertTrue(Arrays.asList(statuses).contains(ArticleStatus.VERIFIED));
        assertTrue(Arrays.asList(statuses).contains(ArticleStatus.REJECTED));
    }

    @Test
    @DisplayName("Should test ArticleType enum")
    void shouldTestArticleTypeEnum() {
        ArticleType[] types = ArticleType.values();
        assertEquals(4, types.length);
        assertTrue(Arrays.asList(types).contains(ArticleType.NEWS));
        assertTrue(Arrays.asList(types).contains(ArticleType.OPINION));
        assertTrue(Arrays.asList(types).contains(ArticleType.INVESTIGATION));
        assertTrue(Arrays.asList(types).contains(ArticleType.FACTUAL));
    }

    @Test
    @DisplayName("Should test FactCheckRating enum")
    void shouldTestFactCheckRatingEnum() {
        FactCheckRating[] ratings = FactCheckRating.values();
        assertEquals(4, ratings.length);
        assertTrue(Arrays.asList(ratings).contains(FactCheckRating.TRUE));
        assertTrue(Arrays.asList(ratings).contains(FactCheckRating.FALSE));
        assertTrue(Arrays.asList(ratings).contains(FactCheckRating.PARTLY_TRUE));
        assertTrue(Arrays.asList(ratings).contains(FactCheckRating.UNVERIFIABLE));
    }

    @Test
    @DisplayName("Should test DTO creation")
    void shouldTestDTOCreation() {
        UserCreateDTO userDto = new UserCreateDTO();
        userDto.setUsername("test");
        userDto.setEmail("test@test.com");
        userDto.setPassword("pass");
        userDto.setRole("USER");
        
        assertEquals("test", userDto.getUsername());
        assertEquals("test@test.com", userDto.getEmail());
        assertEquals("pass", userDto.getPassword());
        assertEquals("USER", userDto.getRole());

        ArticleCreateDTO articleDto = new ArticleCreateDTO();
        articleDto.setTitle("Test");
        articleDto.setContent("Content");
        articleDto.setAuthorId("author");
        articleDto.setAnonymous(true);
        articleDto.setArticleType(ArticleType.OPINION);
        
        assertEquals("Test", articleDto.getTitle());
        assertEquals("Content", articleDto.getContent());
        assertEquals("author", articleDto.getAuthorId());
        assertTrue(articleDto.isAnonymous());
        assertEquals(ArticleType.OPINION, articleDto.getArticleType());
    }

    @Test
    @DisplayName("Should test User no-args constructor")
    void shouldTestUserNoArgsConstructor() {
        User user = new User();
        assertNotNull(user);
        assertNotNull(user.getRegistrationDate());
    }

    @Test
    @DisplayName("Should test Article setters")
    void shouldTestArticleSetters() {
        Article article = new Article();
        article.setTitle("New Title");
        article.setContent("New Content");
        article.setStatus(ArticleStatus.PUBLISHED);
        article.setArticleType(ArticleType.INVESTIGATION);
        
        assertEquals("New Title", article.getTitle());
        assertEquals("New Content", article.getContent());
        assertEquals(ArticleStatus.PUBLISHED, article.getStatus());
        assertEquals(ArticleType.INVESTIGATION, article.getArticleType());
    }

    @Test
    @DisplayName("Should test FactCheck setters")
    void shouldTestFactCheckSetters() {
        FactCheck factCheck = new FactCheck();
        factCheck.setArticleId("newArticleId");
        factCheck.setCheckerId("newCheckerId");
        factCheck.setResult(FactCheckRating.PARTLY_TRUE);
        factCheck.setDescription("New description");
        
        assertEquals("newArticleId", factCheck.getArticleId());
        assertEquals("newCheckerId", factCheck.getCheckerId());
        assertEquals(FactCheckRating.PARTLY_TRUE, factCheck.getResult());
        assertEquals("New description", factCheck.getDescription());
    }
}