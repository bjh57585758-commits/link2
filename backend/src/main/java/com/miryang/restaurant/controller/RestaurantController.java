package com.miryang.restaurant.controller;

import com.miryang.restaurant.dto.*;
import com.miryang.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RestaurantController {

    private final RestaurantService service;

    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    @GetMapping("/restaurants")
    public List<RestaurantSummary> list(@RequestParam(required = false) String keyword) {
        return service.list(keyword);
    }

    @PostMapping("/restaurants")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantDetail create(@Valid @RequestBody RestaurantRequest req) {
        return service.create(req);
    }

    @GetMapping("/restaurants/{id}")
    public RestaurantDetail get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping("/restaurants/{id}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse addReview(@PathVariable Long id, @Valid @RequestBody ReviewRequest req) {
        return service.addReview(id, req);
    }

    @DeleteMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable Long reviewId,
                             @RequestHeader(value = "X-Review-Password", required = false) String password) {
        service.deleteReview(reviewId, password);
    }
}
