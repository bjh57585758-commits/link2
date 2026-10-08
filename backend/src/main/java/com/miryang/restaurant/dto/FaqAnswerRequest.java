package com.miryang.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FaqAnswerRequest(@NotBlank @Size(max = 2000) String answer) {
}
