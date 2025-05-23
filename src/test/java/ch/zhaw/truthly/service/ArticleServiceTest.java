package ch.zhaw.truthly.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.zhaw.truthly.model.Article;
import ch.zhaw.truthly.model.ArticleStatus;
import ch.zhaw.truthly.repository.ArticleRepository;
import ch.zhaw.truthly.repository.UserRepository;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ArticleService articleService;

    private String testArticleId = "article123";
    private String testCheckerId = "checker123";
    private Article testArticle;

    @BeforeEach
    void setUp() {
        testArticle = new Article();
        testArticle.setId(testArticleId);
        testArticle.setStatus(ArticleStatus.PUBLISHED);
    }

    @Test
    @DisplayName("Should assign article for checking successfully")
    void shouldAssignArticleForCheckingSuccessfully() {
        // Given
        when(articleRepository.findById(testArticleId)).thenReturn(Optional.of(testArticle));
        when(userRepository.existsById(testCheckerId)).thenReturn(true);
        when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

        // When
        Optional<Article> result = articleService.assignArticleForChecking(testArticleId, testCheckerId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(ArticleStatus.CHECKING, result.get().getStatus());
        verify(articleRepository).save(testArticle);
    }

    @Test
    @DisplayName("Should fail to assign non-existent article")
    void shouldFailToAssignNonExistentArticle() {
        // Given
        when(articleRepository.findById(testArticleId)).thenReturn(Optional.empty());

        // When
        Optional<Article> result = articleService.assignArticleForChecking(testArticleId, testCheckerId);

        // Then
        assertFalse(result.isPresent());
        verify(articleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should complete fact checking successfully")
    void shouldCompleteFactCheckingSuccessfully() {
        // Given
        testArticle.setStatus(ArticleStatus.CHECKING);
        when(articleRepository.findById(testArticleId)).thenReturn(Optional.of(testArticle));
        when(userRepository.existsById(testCheckerId)).thenReturn(true);
        when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

        // When
        Optional<Article> result = articleService.completeFactChecking(testArticleId, testCheckerId, "VERIFIED");

        // Then
        assertTrue(result.isPresent());
        assertEquals(ArticleStatus.VERIFIED, result.get().getStatus());
    }

    @Test
    @DisplayName("Should check if user exists")
    void shouldCheckIfUserExists() {
        // Given
        when(userRepository.existsById("user123")).thenReturn(true);

        // When
        boolean exists = articleService.userExists("user123");

        // Then
        assertTrue(exists);
        verify(userRepository).existsById("user123");
    }
}