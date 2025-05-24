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
class MongoTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should test MongoDB connection successfully with real MongoDB")
    void shouldTestMongoDBConnectionSuccessfully() throws Exception {
        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Connection ok")));
    }

    @Test
    @DisplayName("Should handle MongoDB connection test multiple times")
    void shouldHandleMongoDBConnectionTestMultipleTimes() throws Exception {
        // Test multiple calls to ensure consistent behavior
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/testmongodb"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Connection ok")));
        }
    }
}