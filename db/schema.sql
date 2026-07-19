-- Retrospective AI - 전체 스키마 (docs/erd.md 기준)
-- createdAt/updatedAt은 DB DEFAULT 없이 애플리케이션(JPA Auditing)이 채운다.
-- 참고: dev-log/deep-dive/02. user 테이블에 createdAt, updateAt이 필요할까.md

CREATE DATABASE IF NOT EXISTS retrospective
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE retrospective;

-- ============================================================
-- 사용자 정보 + 소셜 로그인 + GitHub 연동(PAT) 설정을 저장하는 테이블
-- 소셜 로그인(카카오/GitHub)만 지원하므로 password는 없음
-- provider 계정 간 병합을 하지 않기로 해서, 유저는 평생 정확히 하나의
-- (provider, provider_user_id)만 가짐 -> OAuthAccount를 별도 테이블로
-- 분리할 이유가 없어져서 User에 직접 통합함
-- ============================================================
CREATE TABLE user (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,   -- 사용자 PK
    email                VARCHAR(255)    NOT NULL,            -- 표시용 이메일 (provider가 같아도 여러 유저가 같은 이메일을 가질 수 있어 UNIQUE 아님)
    nickname             VARCHAR(50)     NOT NULL,            -- 사용자 닉네임
    provider             VARCHAR(20)     NOT NULL,            -- 'KAKAO' 또는 'GITHUB'
    provider_user_id     VARCHAR(255)    NOT NULL,            -- provider가 발급한 고유 ID
    repo_owner           VARCHAR(255)    NULL,                -- GitHub 연동 시에만 사용
    repo_name            VARCHAR(255)    NULL,                -- GitHub 연동 시에만 사용
    github_pat           VARCHAR(255)    NULL,                -- GitHub Personal Access Token (암호화 저장 필요)
    github_upload_enabled  BOOLEAN         NULL DEFAULT FALSE,  -- GitHub 자동 커밋 사용 여부
    created_at           DATETIME        NOT NULL,            -- 최초 가입 시간
    updated_at           DATETIME        NOT NULL,            -- 마지막 수정 시간
    UNIQUE KEY uq_user_provider (provider, provider_user_id)  -- 같은 provider 계정으로 중복 가입 방지
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 사용자가 작성한 회고(날짜, 점수)를 저장하는 테이블
-- 관계 : User(1) : Retrospective(N)
-- keep/problem/try는 더 이상 텍스트 컬럼이 아니라 KPT_ITEM으로 분리됨
-- (항목별 드래그 순서 변경, 최대 20개, 항목 단위 자동저장을 지원해야 해서)
-- ============================================================
CREATE TABLE retrospective (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT   NOT NULL,             -- user.id 참조
    date        DATE     NOT NULL,             -- 회고 날짜
    score       INT      NOT NULL DEFAULT 3,   -- 만족도 점수 1~5, 기본값 3
    is_github_synced BOOLEAN NOT NULL DEFAULT FALSE, -- 마지막 GitHub 업로드 이후 수정 여부 (점수/KPT 항목 변경 시 false로 되돌림)
    created_at  DATETIME NOT NULL,
    updated_at  DATETIME NOT NULL,

    FOREIGN KEY (user_id) REFERENCES user (id)
        ON DELETE CASCADE,   -- 회원 탈퇴 시 회고도 함께 삭제
    UNIQUE KEY uq_retrospective_user_date (user_id, date)
        -- 캘린더가 날짜 단위로 작성 여부를 보여주므로 하루 1개로 제한
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 회고 하나에 속한 KPT 항목(Keep/Problem/Try)을 저장하는 테이블
-- 관계 : Retrospective(1) : KptItem(N)
-- type별로 최대 20개까지 (서버에서 개수 검증), 빈 항목(content 공백)은
-- 저장하지 않음. sequence_order로 드래그 순서를 관리하고,
-- 화면의 "K1"/"K2" 같은 라벨은 이 순서로 계산해서 보여줄 뿐 저장하지 않음
-- ============================================================
CREATE TABLE kpt_item (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    retrospective_id  BIGINT       NOT NULL,   -- retrospective.id 참조
    type              VARCHAR(10)  NOT NULL,   -- 'KEEP', 'PROBLEM', 'TRY'
    content           TEXT         NOT NULL,   -- 핵심 내용
    sequence_order     INT          NOT NULL,   -- 같은 type 안에서의 표시 순서

    FOREIGN KEY (retrospective_id) REFERENCES retrospective (id)
        ON DELETE CASCADE   -- 회고 삭제 시 KPT 항목도 함께 삭제
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 날짜/회고에 묶이지 않고 캘린더 화면에 독립적으로 쌓이는 KPT 메모 테이블
-- 관계 : User(1) : KptNote(N)
-- 지키고 싶은 Keep, 반복되는 Problem, 하고 싶은 Try를 사용자가 직접 추가/관리
-- (캘린더 화면과 분석 탭 양쪽에서 추가 가능, AI는 관여하지 않는 순수 메모)
-- type별로 최대 20개까지 (서버에서 개수 검증), sequence_order로 드래그 순서 관리
-- ============================================================
CREATE TABLE kpt_note (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL,   -- user.id 참조
    type            VARCHAR(10)  NOT NULL,   -- 'KEEP', 'PROBLEM', 'TRY'
    content         TEXT         NOT NULL,   -- 메모 내용
    sequence_order  INT          NOT NULL,   -- 같은 type 안에서의 표시 순서
    created_at      DATETIME     NOT NULL,
    updated_at      DATETIME     NOT NULL,

    FOREIGN KEY (user_id) REFERENCES user (id)
        ON DELETE CASCADE   -- 회원 탈퇴 시 메모도 함께 삭제
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 회고 하나에 대해 AI가 생성한 Try 추천 이력을 저장하는 테이블
-- 관계 : Retrospective(1) : AiRecommendation(N)
-- "다시 추천받기"로 재생성될 때마다 이전 이력을 보존하기 위해 분리
-- ============================================================
CREATE TABLE ai_recommendation (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    retrospective_id  BIGINT   NOT NULL,   -- retrospective.id 참조
    suggested_try     TEXT     NOT NULL,   -- AI가 추천한 다음 행동
    created_at        DATETIME NOT NULL,   -- 최신순 정렬 기준 (수정 이력이 없어 updated_at 없음)

    FOREIGN KEY (retrospective_id) REFERENCES retrospective (id)
        ON DELETE CASCADE   -- 회고 삭제 시 추천 이력도 함께 삭제
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 홈(캘린더) 화면에 표시할, 주별 회고를 AI가 요약한 결과를 저장하는 테이블
-- 관계 : User(1) : WeeklySummary(N)
-- 고정 달력 주(월~일) 단위. week_end는 저장하지 않음 (week_start + 6일로 계산)
-- "이번 주"는 방문 시 없으면 자동 생성, 있으면 캐시 반환 (재생성 시 덮어씀).
-- 지난 주들은 캘린더 화면에서 넘겨보기 가능 — 이때도 해당 주의 요약이
-- 없으면 그 자리에서 생성해서 저장함 (읽은 적 없는 과거 주 포함)
-- ============================================================
CREATE TABLE weekly_summary (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT   NOT NULL,   -- user.id 참조
    week_start        DATE     NOT NULL,   -- 그 주의 월요일
    summary_content   TEXT     NOT NULL,   -- AI 요약 본문
    created_at        DATETIME NOT NULL,

    FOREIGN KEY (user_id) REFERENCES user (id)
        ON DELETE CASCADE,
    UNIQUE KEY uq_weekly_summary_user_week (user_id, week_start)
        -- 같은 주에 대한 요약은 하나만 유지 (재생성 시 덮어씀)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 통계 화면에서 사용자가 기간을 직접 골라 저장한 AI 요약을 저장하는 테이블
-- 관계 : User(1) : Summary(N)
-- WEEKLY_SUMMARY와 다르게 기간이 자유롭고, 미리보기 후 사용자가 "저장"을 눌러야 남음
-- 같은 기간을 여러 번 저장해도 되므로 UNIQUE 제약 없음 (개별 삭제로 정리)
-- ============================================================
CREATE TABLE summary (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT   NOT NULL,   -- user.id 참조
    period_start      DATE     NOT NULL,
    period_end        DATE     NOT NULL,
    summary_content   TEXT     NOT NULL,
    created_at        DATETIME NOT NULL,

    FOREIGN KEY (user_id) REFERENCES user (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 사용자가 기간을 직접 골라 저장한, 반복 문제 분석 결과를 저장하는 테이블
-- 관계 : User(1) : ProblemPattern(N)
-- 핵심 컬럼 : problem(반복된 문제) / try(추천 행동)을 분리해 화면에 각각 표시
-- SUMMARY와 동일하게 미리보기 후 저장 방식이라 UNIQUE 제약 없음 (개별 삭제로 정리)
-- ============================================================
CREATE TABLE problem_pattern (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT   NOT NULL,   -- user.id 참조
    period_start   DATE     NOT NULL,
    period_end     DATE     NOT NULL,
    problem        TEXT     NOT NULL,   -- 반복된 문제
    try            TEXT     NOT NULL,   -- 추천 행동
    created_at     DATETIME NOT NULL,

    FOREIGN KEY (user_id) REFERENCES user (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
