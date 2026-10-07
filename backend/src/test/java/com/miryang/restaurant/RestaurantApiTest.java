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
class RestaurantApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void 식당등록_상세_리뷰작성_삭제_흐름() throws Exception {
        mvc.perform(post("/api/restaurants").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"밀양돼지국밥\",\"category\":\"한식\",\"address\":\"밀양시 중앙로 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        mvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("밀양돼지국밥"))
                .andExpect(jsonPath("$[0].reviewCount").value(0));

        mvc.perform(post("/api/restaurants/1/reviews").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\":\"홍길동\",\"password\":\"1234\",\"rating\":5,\"content\":\"맛있어요\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist());

        mvc.perform(get("/api/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(5.0))
                .andExpect(jsonPath("$.reviews[0].content").value("맛있어요"));

        mvc.perform(delete("/api/reviews/1").header("X-Review-Password", "wrong"))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/reviews/1").header("X-Review-Password", "1234"))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/restaurants/1"))
                .andExpect(jsonPath("$.reviews").isEmpty());
    }

    @Test
    void 잘못된_입력과_없는_식당() throws Exception {
        mvc.perform(post("/api/restaurants").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/restaurants/999")).andExpect(status().isNotFound());
    }
}
