package com.calc;

import com.calc.service.ForecastService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ForecastController.class)
public class ForecastControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ForecastService forecastService;

    @Test
    void shouldReturn400_WhenJsonInvalid() throws Exception {
        String fakeJson = "{ \"id\": \"1\", \"x\": [1, 4, 5], \"y\": [100, 160, 400, 500] }";

        mockMvc.perform(post("/forecast")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(fakeJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200_WhenDataIsValid() throws Exception {
        String validJson = "{ \"id\": \"1\", \"x\": [1, 2, 3, 4], \"y\": [100, 120, 160, 400] }";

        byte[] fakeImage = new byte[]{1, 2, 3};

        Mockito.when(forecastService.getForecast(any(), any())).thenReturn(fakeImage);

        mockMvc.perform(post("/forecast")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(fakeImage));
    }

    @Test
    void shouldReturn400_WhenJsonIsMalformed() throws Exception {
        String brokenJson = "{ \"id\": \"1\", \"x\": [1, 2, 3, 4], \"y\": [100, 120, 160, 400] ";  // Скобки на закрыты

        mockMvc.perform(post("/forecast")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(brokenJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_WhenIdIsEmpty() throws Exception {
        String jsonWithEmptyId = "{ \"id\": \"\", \"x\": [1, 2, 3, 4], \"y\": [100, 120, 160, 400] }"; // ID пустой

        mockMvc.perform(post("/forecast")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonWithEmptyId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
