# 과제 제출 정리 — 밀양 맛집

> 괄호 `( )` 로 표시한 부분은 제출 전에 직접 채워 주세요.

## 1. 프로젝트 개요
- **주제**: 밀양의 오프라인 식당 정보를 공유하고 리뷰를 남기는 웹 애플리케이션
- **개발 방식**: AI 도구(Claude Code)와 대화하며 기획 → 설계 → 구현 → 테스트 → 배포까지 진행
- **제출자 / 날짜**: ( 이름 ) / ( 날짜 )

## 2. 배포 주소
| 구분 | 주소 |
|---|---|
| 서비스(프론트엔드) | https://link2-8pkn.vercel.app |
| API(백엔드) | https://link2-qy56.onrender.com (`/api/restaurants`, `/actuator/health`) |
| 소스코드 | https://github.com/bjh57585758-commits/link2 |

> 백엔드가 무료 플랜이라 15분간 요청이 없으면 잠들고, 첫 접속에 30초~1분 걸립니다.

## 3. 기술 스택
| 영역 | 기술 | 배포 |
|---|---|---|
| Frontend | React 19, Vite, React Router | Vercel |
| Backend | Spring Boot 3.3 (Java 17), Spring Data JPA, Bean Validation | Render (Docker) |
| Database | MySQL | Aiven (무료 플랜) |
| 버전 관리 | Git, GitHub | — |

## 4. 구현 기능 (요구사항 대응)
| 요구사항 | 구현 |
|---|---|
| 식당 목록 보기 | `GET /api/restaurants` (이름/종류 검색, 평균 별점·리뷰 수 포함) |
| 식당 등록 | `POST /api/restaurants` (입력값 검증) |
| 식당 상세정보 보기 | `GET /api/restaurants/{id}` (식당 정보 + 리뷰 목록 + 평균 별점) |
| 리뷰 작성 | `POST /api/restaurants/{id}/reviews` (별점 1~5, 내용) |
| 리뷰 삭제 | `DELETE /api/reviews/{id}` — 작성 시 입력한 비밀번호 확인 후 삭제 |

**설계 포인트**
- 회원가입이 없는 서비스라, 리뷰 작성 시 입력한 비밀번호를 **BCrypt 해시**로 저장하고 삭제할 때 일치 여부를 확인합니다.
- DB 접속 정보와 CORS 허용 주소는 코드에 넣지 않고 **환경변수**로 주입합니다.
- 통합 테스트(등록 → 리뷰 작성 → 틀린 비밀번호 거부 → 삭제)를 H2로 작성해 MySQL 없이도 실행됩니다.
- 상세 설계(ERD, API)는 [DESIGN.md](DESIGN.md), 배포 절차는 [DEPLOY.md](DEPLOY.md) 참고.

## 5. 개발 진행 과정
1. **기획**: 주제와 기능 5개 확정, 엔티티(식당·리뷰)와 REST API 목록 정의
2. **백엔드 구현**: Controller → Service → Repository 구조, 예외 처리, 테스트 작성
3. **프론트엔드 구현**: 목록·등록·상세 3개 화면, API 연동, 로딩/오류/빈 목록 처리
4. **배포 준비**: Dockerfile, 환경변수 분리, 배포 문서 작성
5. **배포**: DB(Aiven) → 백엔드(Render) → 프론트엔드(Vercel) → CORS 설정 순서
6. **개선**: 화면 디자인 다듬기(검색 영역, 카드 UI, 모바일 대응)

## 6. AI 도구 활용 내용
- **Claude Code**로 기획 정리, 코드 작성, 테스트 작성, 배포 설정, 문서 작성을 진행했습니다.
- 단계를 나눠 요청했습니다: 기획 → 백엔드 → 프론트엔드 → 배포 → 문제 해결.
- AI가 제안한 내용 중 **직접 확인·판단한 부분**: ( 예: 사용할 DB 서비스 선택, 화면 동작 점검 결과 )
- 주요 프롬프트 예시: ( 사용한 프롬프트를 붙여 넣기 )

## 7. 문제 해결 기록 (실제로 겪은 것)
| 문제 | 원인 | 해결 |
|---|---|---|
| Render에서 MySQL을 쓸 수 없음 | Render 관리형 DB는 PostgreSQL만 제공 | MySQL 필수 조건이라 외부 무료 MySQL(Aiven) 사용 |
| TiDB Cloud 가입 실패 (`registration request too often`) | 가입 요청 속도 제한으로 추정 | 다른 서비스(Aiven)로 변경 |
| 백엔드 배포 후 `Connection refused` | Render 환경변수(`DB_URL` 등)가 입력되지 않아 기본값 `localhost`로 접속 시도 | 환경변수 4개를 정확한 Key 이름으로 추가 후 재배포 |
| Vercel 404 NOT_FOUND | 저장소 `main`에 코드가 없었고, Root Directory가 비어 있어 `frontend`를 빌드하지 못함 | 코드를 `main`에 병합, Root Directory를 `frontend`로 설정, Framework Preset 확인 후 재배포 |
| 화면에서 API 호출 차단 가능성 | CORS 허용 주소 불일치 | `CORS_ORIGINS`를 Vercel 주소로 설정 |

## 8. 화면 캡처
( 목록 / 등록 / 상세·리뷰 화면 캡처를 여기에 추가 )

## 9. 한계와 개선 아이디어
- 무료 플랜 특성상 첫 접속이 느리고, Aiven 무료 DB는 장기간 미사용 시 꺼질 수 있습니다.
- 식당 수정·삭제, 이미지 업로드, 지도 연동, 페이지네이션은 구현하지 않았습니다.
- 리뷰 비밀번호 방식은 간단한 본인 확인용이며, 정식 서비스라면 회원 인증이 필요합니다.
