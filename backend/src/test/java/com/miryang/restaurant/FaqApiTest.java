package com.miryang.restaurant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class FaqApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void 질문등록_답변_삭제_흐름() throws Exception {
        mvc.perform(post("/api/faqs").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\":\"홍길동\",\"password\":\"1234\",\"title\":\"주차 가능한가요?\",\"question\":\"주차장이 있나요?\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.answer").doesNotExist());

        mvc.perform(put("/api/faqs/1/answer").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answer\":\"네, 있습니다.\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(put("/api/faqs/1/answer").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Admin-Password", "admin-pw")
                        .content("{\"answer\":\"네, 있습니다.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("네, 있습니다."));

        mvc.perform(get("/api/faqs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("주차 가능한가요?"))
                .andExpect(jsonPath("$[0].answeredAt").exists());

        mvc.perform(delete("/api/faqs/1").header("X-Review-Password", "wrong"))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/faqs/1").header("X-Review-Password", "1234"))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/faqs")).andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void 빈_입력과_없는_질문() throws Exception {
        mvc.perform(post("/api/faqs").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(delete("/api/faqs/999").header("X-Review-Password", "1234"))
                .andExpect(status().isNotFound());
    }
}
