package com.miryang.restaurant.controller;

import com.miryang.restaurant.dto.FaqAnswerRequest;
import com.miryang.restaurant.dto.FaqRequest;
import com.miryang.restaurant.dto.FaqResponse;
import com.miryang.restaurant.service.FaqService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** 한우소달구지 FAQ 게시판. */
@RestController
@RequestMapping("/api/faqs")
public class FaqController {

    private final FaqService service;

    public FaqController(FaqService service) {
        this.service = service;
    }

    @GetMapping
    public List<FaqResponse> list() {
        return service.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FaqResponse create(@Valid @RequestBody FaqRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}/answer")
    public FaqResponse answer(@PathVariable Long id, @Valid @RequestBody FaqAnswerRequest req,
                              @RequestHeader(value = "X-Admin-Password", required = false) String adminPassword) {
        return service.answer(id, req, adminPassword);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @RequestHeader(value = "X-Review-Password", required = false) String password) {
        service.delete(id, password);
    }
}
