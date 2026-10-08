package com.miryang.restaurant.service;

import com.miryang.restaurant.domain.Faq;
import com.miryang.restaurant.dto.FaqAnswerRequest;
import com.miryang.restaurant.dto.FaqRequest;
import com.miryang.restaurant.dto.FaqResponse;
import com.miryang.restaurant.exception.ForbiddenException;
import com.miryang.restaurant.exception.NotFoundException;
import com.miryang.restaurant.repository.FaqRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FaqService {

    private final FaqRepository faqs;
    private final PasswordEncoder encoder;
    private final String adminPassword;

    public FaqService(FaqRepository faqs, PasswordEncoder encoder,
                      @Value("${app.admin.password:}") String adminPassword) {
        this.faqs = faqs;
        this.encoder = encoder;
        this.adminPassword = adminPassword;
    }

    public List<FaqResponse> list() {
        return faqs.findAllByOrderByCreatedAtDesc().stream().map(FaqResponse::from).toList();
    }

    @Transactional
    public FaqResponse create(FaqRequest req) {
        Faq saved = faqs.save(new Faq(req.author().trim(), encoder.encode(req.password()),
                req.title().trim(), req.question().trim()));
        return FaqResponse.from(saved);
    }

    @Transactional
    public FaqResponse answer(Long id, FaqAnswerRequest req, String adminPassword) {
        requireAdmin(adminPassword);
        Faq faq = find(id);
        faq.answer(req.answer().trim());
        return FaqResponse.from(faq);
    }

    /** 질문 작성자 비밀번호 또는 관리자 비밀번호로 삭제한다. */
    @Transactional
    public void delete(Long id, String password) {
        Faq faq = find(id);
        if (!isAdmin(password) && (password == null || !encoder.matches(password, faq.getPasswordHash()))) {
            throw new ForbiddenException("비밀번호가 일치하지 않습니다.");
        }
        faqs.delete(faq);
    }

    private void requireAdmin(String password) {
        if (!isAdmin(password)) {
            throw new ForbiddenException("관리자 비밀번호가 일치하지 않습니다.");
        }
    }

    /** ADMIN_PASSWORD 가 설정되지 않으면 관리자 기능은 비활성화된다. */
    private boolean isAdmin(String password) {
        return !adminPassword.isBlank() && adminPassword.equals(password);
    }

    private Faq find(Long id) {
        return faqs.findById(id).orElseThrow(() -> new NotFoundException("질문을 찾을 수 없습니다."));
    }
}
