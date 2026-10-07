package com.miryang.restaurant.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
        @NotBlank @Size(max = 50) String author,
        @NotBlank @Size(min = 4, max = 50) String password,
        @Min(1) @Max(5) int rating,
        @NotBlank @Size(max = 1000) String content) {
}
