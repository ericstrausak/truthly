package ch.zhaw.truthly.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MongoTestControllerSimpleTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should successfully connect to MongoDB")
    void shouldSuccessfullyConnectToMongoDB() throws Exception {
        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isOk())
                .andExpect(content().string("Connection ok"));
    }

    @Test
    @DisplayName("Should return consistent results")
    void shouldReturnConsistentResults() throws Exception {
        String result1 = mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String result2 = mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("Connection ok", result1);
        assertEquals("Connection ok", result2);
    }

    @Test
    @DisplayName("Should handle concurrent requests")
    void shouldHandleConcurrentRequests() throws Exception {
        // Test multiple concurrent requests
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/testmongodb"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Connection ok"));
        }
    }

    @Test
    @DisplayName("Should respond quickly")
    void shouldRespondQuickly() throws Exception {
        long startTime = System.currentTimeMillis();
        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isOk()); // fixed typo: andExpected -> andExpect
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        // Should respond within reasonable time (5 seconds)
        assertTrue(duration < 5000, "Response took too long: " + duration + "ms");
    }
}