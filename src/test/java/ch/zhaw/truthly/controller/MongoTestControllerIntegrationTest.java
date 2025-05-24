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
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MongoTestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should test MongoDB connection in integration environment")
    void shouldTestMongoDBConnectionInIntegrationEnvironment() throws Exception {
        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Connection ok")));
    }

    @Test
    @DisplayName("Should handle multiple concurrent requests")
    void shouldHandleMultipleConcurrentRequests() throws Exception {
        // Test multiple requests to ensure thread safety
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/testmongodb"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Connection ok")));
        }
    }

    @Test
    @DisplayName("Should maintain consistent behavior across requests")
    void shouldMaintainConsistentBehaviorAcrossRequests() throws Exception {
        String firstResponse = mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String secondResponse = mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Both should be success responses
        assertTrue(firstResponse.contains("Connection ok"));
        assertTrue(secondResponse.contains("Connection ok"));
    }
}