package com.calc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class SalaryRequest {
    @NotEmpty(message = "ID cant be empty")
    private String id;
    @NotNull(message = "X cant be empty")
    private double[] x; // month
    @NotNull(message = "Y cant be empty")
    @Size(min = 2, message = "Min 2 points required")
    private double[] y; // salary

    @JsonIgnore
    @AssertTrue(message = "The number of points must match")
    public boolean isArraysLengthMatch() {
        log.info("[Validation] Check the length of arrays for ID: {}", id);

        if (x == null || y == null) return true;
        boolean isValid = x.length == y.length;

        if (!isValid) {
            log.warn("❌ Length error: X={}, Y={}", x.length, y.length);
        } else {
            log.info("✅ Length matches");
        }

        return isValid;
    }

    @JsonIgnore
    @AssertTrue(message = "Salary (Y) cant be negative")
    public boolean isSalaryPositive() {
        if (y == null) return true;
        boolean hasNegative = Arrays.stream(y).anyMatch(n -> n < 0);

        if (hasNegative) {
            log.warn("❌ Negative salary was found");
            return false;
        }

        return true;
    }

    @JsonIgnore
    @AssertTrue(message = "Time points (X) must be strictly increasing")
    public boolean isXSorted() {
        log.info("[Validation] Month sort check");
        if (x == null || x.length < 2) return true;

        for (int i = 0; i < x.length - 1; i++) {
            if (x[i] >= x[i+1]) {
                log.warn("❌ Sorting error: element [{}]={} >= element [{}]={}", i, x[i], i+1, x[i+1]);
                return false;
            }
        }
        log.info("✅ Time correct");
        return true;
    }
}
