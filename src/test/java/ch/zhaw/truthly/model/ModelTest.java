
package ch.zhaw.truthly.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    @DisplayName("Should create Article with all parameters")
    void shouldCreateArticleWithAllParameters() {
        // Given & When
        Article article = new Article("Title", "Content", "author123", false, ArticleType.NEWS);

        // Then
        assertEquals("Title", article.getTitle());
        assertEquals("Content", article.getContent());
        assertEquals("author123", article.getAuthorId());
        assertFalse(article.isAnonymous());
        assertEquals(ArticleType.NEWS, article.getArticleType());
        assertEquals(ArticleStatus.DRAFT, article.getStatus());
        assertNotNull(article.getPublicationDate());
    }

    @Test
    @DisplayName("Should test Article setters")
    void shouldTestArticleSetters() {
        // Given
        Article article = new Article();

        // When
        article.setTitle("New Title");
        article.setContent("New Content");
        article.setAuthorId("newAuthor");
        article.setAnonymous(true);
        article.setArticleType(ArticleType.OPINION);
        article.setStatus(ArticleStatus.PUBLISHED);

        // Then
        assertEquals("New Title", article.getTitle());
        assertEquals("New Content", article.getContent());
        assertEquals("newAuthor", article.getAuthorId());
        assertTrue(article.isAnonymous());
        assertEquals(ArticleType.OPINION, article.getArticleType());
        assertEquals(ArticleStatus.PUBLISHED, article.getStatus());
    }

    @Test
    @DisplayName("Should create User with all parameters")
    void shouldCreateUserWithAllParameters() {
        // Given & When
        User user = new User("testuser", "test@email.com", "password", "USER");

        // Then
        assertEquals("testuser", user.getUsername());
        assertEquals("test@email.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals("USER", user.getRole());
        assertNotNull(user.getRegistrationDate());
    }

    @Test
    @DisplayName("Should create FactCheck with AI fields")
    void shouldCreateFactCheckWithAIFields() {
        // Given & When
        FactCheck factCheck = new FactCheck("article123", "checker123", FactCheckRating.TRUE,
                "Verified", "AI_TRUE", "AI says it's true");

        // Then
        assertEquals("article123", factCheck.getArticleId());
        assertEquals("checker123", factCheck.getCheckerId());
        assertEquals(FactCheckRating.TRUE, factCheck.getResult());
        assertEquals("Verified", factCheck.getDescription());
        assertEquals("AI_TRUE", factCheck.getAiVerificationResult());
        assertEquals("AI says it's true", factCheck.getAiExplanation());
        assertNotNull(factCheck.getCheckDate());
        assertNotNull(factCheck.getAiVerificationDate());
    }

    @Test
    @DisplayName("Should test FactCheck setters")
    void shouldTestFactCheckSetters() {
        // Given
        FactCheck factCheck = new FactCheck();

        // When
        factCheck.setAiVerificationResult("AI_FALSE");
        factCheck.setAiExplanation("AI explanation");

        // Then
        assertEquals("AI_FALSE", factCheck.getAiVerificationResult());
        assertEquals("AI explanation", factCheck.getAiExplanation());
    }

    @Test
    @DisplayName("Should test DTOs")
    void shouldTestDTOs() {
        // UserCreateDTO
        UserCreateDTO userDto = new UserCreateDTO();
        userDto.setUsername("test");
        userDto.setEmail("test@test.com");
        userDto.setPassword("pass");
        userDto.setRole("USER");

        assertEquals("test", userDto.getUsername());
        assertEquals("test@test.com", userDto.getEmail());
        assertEquals("pass", userDto.getPassword());
        assertEquals("USER", userDto.getRole());

        // ArticleCreateDTO
        ArticleCreateDTO articleDto = new ArticleCreateDTO();
        articleDto.setTitle("Test");
        articleDto.setContent("Content");
        articleDto.setAuthorId("author");
        articleDto.setAnonymous(true);
        articleDto.setArticleType(ArticleType.NEWS);

        assertEquals("Test", articleDto.getTitle());
        assertEquals("Content", articleDto.getContent());
        assertEquals("author", articleDto.getAuthorId());
        assertTrue(articleDto.isAnonymous());
        assertEquals(ArticleType.NEWS, articleDto.getArticleType());
    }
}