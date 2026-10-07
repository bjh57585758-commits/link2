package com.miryang.restaurant.repository;

import com.miryang.restaurant.domain.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);

    @Query("select avg(r.rating) from Review r where r.restaurant.id = :restaurantId")
    Double findAverageRating(Long restaurantId);

    long countByRestaurantId(Long restaurantId);
}
