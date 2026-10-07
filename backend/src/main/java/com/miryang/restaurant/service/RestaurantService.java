package com.miryang.restaurant.service;

import com.miryang.restaurant.domain.Restaurant;
import com.miryang.restaurant.domain.Review;
import com.miryang.restaurant.dto.*;
import com.miryang.restaurant.exception.ForbiddenException;
import com.miryang.restaurant.exception.NotFoundException;
import com.miryang.restaurant.repository.RestaurantRepository;
import com.miryang.restaurant.repository.ReviewRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurants;
    private final ReviewRepository reviews;
    private final PasswordEncoder encoder;

    public RestaurantService(RestaurantRepository restaurants, ReviewRepository reviews, PasswordEncoder encoder) {
        this.restaurants = restaurants;
        this.reviews = reviews;
        this.encoder = encoder;
    }

    public List<RestaurantSummary> list(String keyword) {
        List<Restaurant> found = (keyword == null || keyword.isBlank())
                ? restaurants.findAllByOrderByCreatedAtDesc()
                : restaurants.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrderByCreatedAtDesc(
                        keyword.trim(), keyword.trim());
        return found.stream()
                .map(r -> RestaurantSummary.of(r, reviews.findAverageRating(r.getId()),
                        reviews.countByRestaurantId(r.getId())))
                .toList();
    }

    @Transactional
    public RestaurantDetail create(RestaurantRequest req) {
        Restaurant saved = restaurants.save(new Restaurant(req.name().trim(), req.category().trim(),
                req.address().trim(), req.phone(), req.description()));
        return RestaurantDetail.of(saved, null, List.of());
    }

    public RestaurantDetail get(Long id) {
        Restaurant r = findRestaurant(id);
        List<ReviewResponse> list = reviews.findByRestaurantIdOrderByCreatedAtDesc(id).stream()
                .map(ReviewResponse::from).toList();
        return RestaurantDetail.of(r, reviews.findAverageRating(id), list);
    }

    @Transactional
    public ReviewResponse addReview(Long restaurantId, ReviewRequest req) {
        Restaurant r = findRestaurant(restaurantId);
        Review saved = reviews.save(new Review(r, req.author().trim(), encoder.encode(req.password()),
                req.rating(), req.content().trim()));
        return ReviewResponse.from(saved);
    }

    @Transactional
    public void deleteReview(Long reviewId, String password) {
        Review review = reviews.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("리뷰를 찾을 수 없습니다."));
        if (password == null || !encoder.matches(password, review.getPasswordHash())) {
            throw new ForbiddenException("비밀번호가 일치하지 않습니다.");
        }
        reviews.delete(review);
    }

    private Restaurant findRestaurant(Long id) {
        return restaurants.findById(id).orElseThrow(() -> new NotFoundException("식당을 찾을 수 없습니다."));
    }
}
