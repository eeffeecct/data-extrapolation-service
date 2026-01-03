package com.calc;

import com.calc.enums.ForecastType;
import com.calc.service.ForecastService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ForecastMathTest {

    private ForecastService forecastService;

    @BeforeEach
    void setUp() {
        RedisTemplate redisMock = Mockito.mock(RedisTemplate.class);
        forecastService = new ForecastService(redisMock);
    }

    @Test
    void testLinearRegressionCalculation() {
        double[] x = {1.0, 2.0, 3.0};
        double[] y = {10.0, 20.0, 30.0};

        List<Double> result = ReflectionTestUtils.invokeMethod(
                forecastService,
                "calculatePoints",
                x, y, ForecastType.linear
        );

        Assertions.assertNotNull(result);
        assertEquals(10, result.size(), "Должно быть 10 точек прогноза");
        assertEquals(40.0, result.get(0), 0.001);
        assertEquals(80.0, result.get(4), 0.001);
        assertEquals(130.0, result.get(9), 0.001);
    }

    @Test
    void testPolynomialRegressionCalculation() {
        double[] x = {1.0, 2.0, 3.0};
        double[] y = {1.0, 4.0, 9.0};

        List<Double> result = ReflectionTestUtils.invokeMethod(
                forecastService,
                "calculatePoints",
                x, y, ForecastType.poli
        );

        Assertions.assertNotNull(result);
        assertEquals(10, result.size());
        assertEquals(16.0, result.get(0), 0.1);
        assertEquals(25.0, result.get(1), 0.1);
    }
}
