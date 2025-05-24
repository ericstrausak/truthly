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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MongoTestControllerCoverageTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should test MongoDB connection endpoint")
    void shouldTestMongoDBConnectionEndpoint() throws Exception {
        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Connection ok")));
    }

    @Test
    @DisplayName("Should handle multiple requests consistently")
    void shouldHandleMultipleRequestsConsistently() throws Exception {
        // Test multiple times to increase coverage
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/testmongodb"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Connection ok")));
        }
    }

    @Test
    @DisplayName("Should return text response")
    void shouldReturnTextResponse() throws Exception {
        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/plain"));
    }

    @Test
    @DisplayName("Should reject POST method")
    void shouldRejectPostMethod() throws Exception {
        // Test that only GET is supported
        mockMvc.perform(post("/testmongodb"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("Should reject PUT method")
    void shouldRejectPutMethod() throws Exception {
        mockMvc.perform(put("/testmongodb"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("Should reject DELETE method")
    void shouldRejectDeleteMethod() throws Exception {
        mockMvc.perform(delete("/testmongodb"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("Should handle request with query parameters")
    void shouldHandleRequestWithQueryParameters() throws Exception {
        // MongoDB test should ignore query parameters
        mockMvc.perform(get("/testmongodb").param("test", "value"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Connection ok")));
    }

    @Test
    @DisplayName("Should handle request with headers")
    void shouldHandleRequestWithHeaders() throws Exception {
        mockMvc.perform(get("/testmongodb")
                .header("X-Test-Header", "test-value"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Connection ok")));
    }
}