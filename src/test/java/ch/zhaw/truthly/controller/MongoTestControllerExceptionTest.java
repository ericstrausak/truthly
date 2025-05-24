package ch.zhaw.truthly.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

@WebMvcTest(MongoTestController.class)
@ActiveProfiles("test")
class MongoTestControllerExceptionTest {

        @Autowired
        private MockMvc mockMvc;

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
                                .andExpect(content().string(containsString("Failed:")));
        }

        @Test
        @DisplayName("Should handle MongoDB save returning null")
        void shouldHandleMongoDBSaveReturningNull() throws Exception {
                // Mock save to return null
                when(mongoTemplate.save(any(DBObject.class), eq("Test")))
                                .thenReturn(null);

                mockMvc.perform(get("/testmongodb"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().string(containsString("Failed:")));
        }

        @Test
        @DisplayName("Should handle successful MongoDB operations")
        void shouldHandleSuccessfulMongoDBOperations() throws Exception {
                // Mock successful operations
                Long testTime = System.currentTimeMillis();
                DBObject mockSaved = mock(DBObject.class);
                when(mockSaved.get("_id")).thenReturn("test-id");
                when(mockSaved.get("time")).thenReturn(testTime);
                DBObject mockRetrieved = mock(DBObject.class);
                when(mockRetrieved.get("time")).thenReturn(testTime);
                when(mongoTemplate.save(any(DBObject.class), eq("Test")))
                                .thenReturn(mockSaved);
                when(mongoTemplate.findById("test-id", DBObject.class, "Test"))
                                .thenReturn(mockRetrieved);

                mockMvc.perform(get("/testmongodb"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Connection ok"));
        }
}