package com.calc;

import com.calc.enums.ForecastType;
import com.calc.model.SalaryRequest;
import com.calc.service.ForecastService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastService forecastService;

    @PostMapping(value = "/forecast", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> forecast(
            @Valid @RequestBody SalaryRequest request,
            @RequestParam(name = "forecast_type", defaultValue = "linear") ForecastType forecastType
    ) {
        try {
            log.info("✅ [Validaion] was successful ID: {}, type forecast: {}", request.getId(), forecastType);
            byte[] imageData = forecastService.getForecast(request, forecastType);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageData);

        } catch (Exception e) {
            log.error("❌ Error while creating forecast for ID: {}", request.getId(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
