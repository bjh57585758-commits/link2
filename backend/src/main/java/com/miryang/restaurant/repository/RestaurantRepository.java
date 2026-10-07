package com.miryang.restaurant.repository;

import com.miryang.restaurant.domain.Restaurant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findAllByOrderByCreatedAtDesc();

    List<Restaurant> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrderByCreatedAtDesc(
            String name, String category);
}
