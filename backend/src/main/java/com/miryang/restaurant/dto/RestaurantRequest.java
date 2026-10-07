package com.miryang.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestaurantRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 50) String category,
        @NotBlank @Size(max = 200) String address,
        @Size(max = 30) String phone,
        @Size(max = 1000) String description) {
}
