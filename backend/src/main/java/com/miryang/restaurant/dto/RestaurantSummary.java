package com.miryang.restaurant.dto;

import com.miryang.restaurant.domain.Restaurant;

public record RestaurantSummary(Long id, String name, String category, String address,
                                Double averageRating, long reviewCount) {
    public static RestaurantSummary of(Restaurant r, Double avg, long count) {
        return new RestaurantSummary(r.getId(), r.getName(), r.getCategory(), r.getAddress(), avg, count);
    }
}
