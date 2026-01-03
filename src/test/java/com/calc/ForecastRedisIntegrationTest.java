package com.calc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ForecastRedisIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RedisTemplate<String, byte[]> redisTemplate;

    @Test
    void shouldSaveBytesToRedis_WhenRequestIsValid() throws Exception {
        String rawId = "test-id-1";

        String validJson = "{ \"id\": \"" + rawId + "\", \"x\": [1, 2, 3, 4], \"y\": [100, 120, 160, 400] }";

        mockMvc.perform(post("/forecast")
                    .param("type", "linear")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validJson))
                .andExpect(status().isOk());

        String redisKey = "salary" + rawId + ":linear";

        byte[] savedImage = redisTemplate.opsForValue().get(redisKey);

        assertThat(savedImage)
                .isNotNull()
                .isNotEmpty();

        redisTemplate.delete(redisKey);
    }

    @Test
    void shouldReturnCacheData_WhenDataExistsInRedis() throws Exception {
        String rawId = "test-id-2";

        String redisKey = "salary" + rawId + ":linear";

        byte[] cachedFakeImage = "from_cache".getBytes();

        redisTemplate.opsForValue().set(redisKey, cachedFakeImage);

        String requestJson = "{ \"id\": \"" + rawId + "\", \"x\": [1, 2, 3, 4], \"y\": [100, 120, 160, 400] }";

        try {
            mockMvc.perform(post("/forecast")
                        .param("type", "linear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))

                    .andExpect(status().isOk())
                    .andExpect(content().bytes(cachedFakeImage));

        } finally {
            redisTemplate.delete(redisKey);
        }
    }
}
