package com.miryang.restaurant.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** 한우소달구지 FAQ 게시판의 질문. 답변은 관리자가 단다. */
@Entity
public class Faq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String author;

    /** BCrypt 해시. 질문 삭제 시 본인 확인용 비밀번호. */
    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 1000)
    private String question;

    @Column(length = 2000)
    private String answer;

    private LocalDateTime answeredAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Faq() {
    }

    public Faq(String author, String passwordHash, String title, String question) {
        this.author = author;
        this.passwordHash = passwordHash;
        this.title = title;
        this.question = question;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void answer(String answer) {
        this.answer = answer;
        this.answeredAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getAuthor() { return author; }
    public String getPasswordHash() { return passwordHash; }
    public String getTitle() { return title; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public LocalDateTime getAnsweredAt() { return answeredAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
