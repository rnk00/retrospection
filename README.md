# DOT — KPT 회고 캘린더

## 기술 스택
- **백엔드**: Spring Boot 3 / Java 21 / Gradle / MySQL
- **프론트엔드**: Vue 3 / Vite / Pinia / Vue Router
- **인증**: OAuth2 (카카오, GitHub) + JWT
- **AI**: OpenAI GPT-4o-mini

## 프로젝트 구조
```
DOT/
├── backend/       # Spring Boot
└── frontend/      # Vue 3
```

## 실행 방법

### 1. DB 생성
```sql
CREATE DATABASE dot_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 환경 변수 설정
`.env.example`을 복사해서 `.env`로 만들고 값 채우기.

### 3. 백엔드 실행
```bash
cd backend
./gradlew bootRun
```

### 4. 프론트엔드 실행
```bash
cd frontend
npm install
npm run dev
```

→ http://localhost:5173 접속

## API 엔드포인트

| Method | URL | 설명 |
|--------|-----|------|
| GET | /api/auth/me | 내 정보 조회 |
| GET | /api/retrospects/calendar?year=&month= | 월별 캘린더 |
| GET | /api/retrospects/date/{date} | 특정 날짜 회고 |
| POST | /api/retrospects | 회고 생성 |
| PUT | /api/retrospects/{id} | 회고 수정 |
| DELETE | /api/retrospects/{id} | 회고 삭제 |
| POST | /api/ai/suggest-try | AI Try 제안 |
| GET | /api/ai/guide/{field} | 작성 가이드 |

## OAuth 설정

### 카카오
- [Kakao Developers](https://developers.kakao.com) → 앱 생성
- 리다이렉트 URI: `http://localhost:8080/login/oauth2/code/kakao`
- 동의항목: 닉네임, 이메일

### GitHub
- GitHub → Settings → Developer settings → OAuth Apps
- Callback URL: `http://localhost:8080/login/oauth2/code/github`
