package ch.zhaw.truthly.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.mongodb.DBObject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MongoTestControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    // Suppress deprecation warning for @MockBean
    @SuppressWarnings("removal")
    @MockBean
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("Should handle MongoDB save exception gracefully")
    void shouldHandleMongoDBSaveExceptionGracefully() throws Exception {
        // Mock MongoTemplate to throw exception on save
        when(mongoTemplate.save(any(DBObject.class), eq("Test")))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Failed:")))
                .andExpect(content().string(containsString("Database connection failed")));
    }

    @Test
    @DisplayName("Should handle MongoDB save returning null")
    void shouldHandleMongoDBSaveReturningNull() throws Exception {
        // Mock save to return null (which would cause NullPointerException)
        when(mongoTemplate.save(any(DBObject.class), eq("Test")))
                .thenReturn(null);

        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Failed:")));
    }

    @Test
    @DisplayName("Should handle MongoDB findById returning null")
    void shouldHandleMongoDBFindByIdReturningNull() throws Exception {
        // Mock successful save but findById returns null
        DBObject mockSaved = mock(DBObject.class);
        when(mockSaved.get("_id")).thenReturn("test-id");
        when(mockSaved.get("time")).thenReturn(System.currentTimeMillis());
        
        when(mongoTemplate.save(any(DBObject.class), eq("Test")))
                .thenReturn(mockSaved);
        when(mongoTemplate.findById("test-id", DBObject.class, "Test"))
                .thenReturn(null);

        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Failed:")))
                .andExpect(content().string(containsString("Retrived document does not match")));
    }

    @Test
    @DisplayName("Should handle time mismatch in retrieved document")
    void shouldHandleTimeMismatchInRetrievedDocument() throws Exception {
        // Mock successful save and retrieve but with different time values
        Long saveTime = System.currentTimeMillis();
        Long retrieveTime = saveTime + 1000; // Different time
        
        DBObject mockSaved = mock(DBObject.class);
        when(mockSaved.get("_id")).thenReturn("test-id");
        when(mockSaved.get("time")).thenReturn(saveTime);
        
        DBObject mockRetrieved = mock(DBObject.class);
        when(mockRetrieved.get("time")).thenReturn(retrieveTime);
        
        when(mongoTemplate.save(any(DBObject.class), eq("Test")))
                .thenReturn(mockSaved);
        when(mongoTemplate.findById("test-id", DBObject.class, "Test"))
                .thenReturn(mockRetrieved);

        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Failed:")))
                .andExpect(content().string(containsString("Retrived document does not match")));
    }

    @Test
    @DisplayName("Should handle MongoDB findById exception")
    void shouldHandleMongoDBFindByIdException() throws Exception {
        // Mock successful save but findById throws exception
        DBObject mockSaved = mock(DBObject.class);
        when(mockSaved.get("_id")).thenReturn("test-id");
        
        when(mongoTemplate.save(any(DBObject.class), eq("Test")))
                .thenReturn(mockSaved);
        when(mongoTemplate.findById("test-id", DBObject.class, "Test"))
                .thenThrow(new RuntimeException("FindById failed"));

        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Failed:")))
                .andExpect(content().string(containsString("FindById failed")));
    }

    @Test
    @DisplayName("Should handle MongoDB save with null ID")
    void shouldHandleMongoDBSaveWithNullId() throws Exception {
        // Mock save returning object with null ID
        DBObject mockSaved = mock(DBObject.class);
        when(mockSaved.get("_id")).thenReturn(null);
        
        when(mongoTemplate.save(any(DBObject.class), eq("Test")))
                .thenReturn(mockSaved);

        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Failed:")));
    }

    @Test
    @DisplayName("Should handle general exception during test")
    void shouldHandleGeneralExceptionDuringTest() throws Exception {
        // Mock any unexpected exception
        when(mongoTemplate.save(any(DBObject.class), eq("Test")))
                .thenThrow(new IllegalArgumentException("Unexpected error"));

        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Failed:")))
                .andExpect(content().string(containsString("Unexpected error")));
    }

    @Test
    @DisplayName("Should handle retrieved document with null time")
    void shouldHandleRetrievedDocumentWithNullTime() throws Exception {
        Long saveTime = System.currentTimeMillis();
        
        DBObject mockSaved = mock(DBObject.class);
        when(mockSaved.get("_id")).thenReturn("test-id");
        when(mockSaved.get("time")).thenReturn(saveTime);
        
        DBObject mockRetrieved = mock(DBObject.class);
        when(mockRetrieved.get("time")).thenReturn(null); // Null time
        
        when(mongoTemplate.save(any(DBObject.class), eq("Test")))
                .thenReturn(mockSaved);
        when(mongoTemplate.findById("test-id", DBObject.class, "Test"))
                .thenReturn(mockRetrieved);

        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Failed:")));
    }

    @Test
    @DisplayName("Should handle successful MongoDB operations with mocked template")
    void shouldHandleSuccessfulMongoDBOperationsWithMockedTemplate() throws Exception {
        // Mock a successful scenario
        Long testTime = System.currentTimeMillis();
        
        DBObject mockSaved = mock(DBObject.class);
        when(mockSaved.get("_id")).thenReturn("test-id");
        when(mockSaved.get("time")).thenReturn(testTime);
        
        DBObject mockRetrieved = mock(DBObject.class);
        when(mockRetrieved.get("time")).thenReturn(testTime); // Same time
        
        when(mongoTemplate.save(any(DBObject.class), eq("Test")))
                .thenReturn(mockSaved);
        when(mongoTemplate.findById("test-id", DBObject.class, "Test"))
                .thenReturn(mockRetrieved);

        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isOk())
                .andExpect(content().string("Connection ok"));
    }

    @Test
    @DisplayName("Should handle string comparison edge case")
    void shouldHandleStringComparisonEdgeCase() throws Exception {
        // Test with different object types that convert to same string
        Long saveTime = 12345L;
        String retrieveTime = "12345"; // String version of same number
        
        DBObject mockSaved = mock(DBObject.class);
        when(mockSaved.get("_id")).thenReturn("test-id");
        when(mockSaved.get("time")).thenReturn(saveTime);
        
        DBObject mockRetrieved = mock(DBObject.class);
        when(mockRetrieved.get("time")).thenReturn(retrieveTime);
        
        when(mongoTemplate.save(any(DBObject.class), eq("Test")))
                .thenReturn(mockSaved);
        when(mongoTemplate.findById("test-id", DBObject.class, "Test"))
                .thenReturn(mockRetrieved);

        // This should succeed because toString() comparison should match
        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isOk())
                .andExpect(content().string("Connection ok"));
    }
}