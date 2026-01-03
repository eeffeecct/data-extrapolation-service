package com.calc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ForecastIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnRealImage_WhenDataIsValid() throws Exception {
        String validJson = "{ \"id\": \"1\", \"x\": [1, 2, 3, 4], \"y\": [100, 120, 160, 400] }";

        mockMvc.perform(post("/forecast")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().string(org.hamcrest.Matchers.not("")));
    }
}
