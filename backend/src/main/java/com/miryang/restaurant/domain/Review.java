package com.miryang.restaurant.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(nullable = false, length = 50)
    private String author;

    /** BCrypt 해시. 삭제 시 본인 확인용 비밀번호. */
    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Review() {
    }

    public Review(Restaurant restaurant, String author, String passwordHash, int rating, String content) {
        this.restaurant = restaurant;
        this.author = author;
        this.passwordHash = passwordHash;
        this.rating = rating;
        this.content = content;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Restaurant getRestaurant() { return restaurant; }
    public String getAuthor() { return author; }
    public String getPasswordHash() { return passwordHash; }
    public int getRating() { return rating; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
