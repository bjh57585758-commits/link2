# 밀양 맛집 — 기획/설계

## 기능
| 기능 | API |
|---|---|
| 식당 목록 (+검색) | `GET /api/restaurants?keyword=` |
| 식당 등록 | `POST /api/restaurants` |
| 식당 상세 (리뷰 포함) | `GET /api/restaurants/{id}` |
| 리뷰 작성 | `POST /api/restaurants/{id}/reviews` |
| 리뷰 삭제 | `DELETE /api/reviews/{id}` + 헤더 `X-Review-Password` |

회원가입 없이 쓰는 서비스라, 리뷰 작성 시 입력한 **비밀번호(BCrypt 해시 저장)** 로 본인 확인 후 삭제합니다.

## ERD
```
Restaurant (id, name, category, address, phone, description, created_at)
   1 ──< N
Review (id, restaurant_id, author, password_hash, rating 1~5, content, created_at)
```

## 화면
- `/` 식당 목록·검색 / `/restaurants/new` 등록 / `/restaurants/:id` 상세 + 리뷰
