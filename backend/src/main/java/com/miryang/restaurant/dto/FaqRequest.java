package com.miryang.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FaqRequest(
        @NotBlank @Size(max = 50) String author,
        @NotBlank @Size(min = 4, max = 50) String password,
        @NotBlank @Size(max = 100) String title,
        @NotBlank @Size(max = 1000) String question) {
}
