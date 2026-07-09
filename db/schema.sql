-- Retrospective AI - MVP schema (docs/erd.md §1 기준)
-- Advanced/Extension 테이블은 별도 파일에서 추가 예정

CREATE DATABASE IF NOT EXISTS retrospective
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE retrospective;

CREATE TABLE user (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    email                VARCHAR(255)    NOT NULL,
    password             VARCHAR(255)    NULL,
    nickname             VARCHAR(50)     NOT NULL,
    repo_owner           VARCHAR(255)    NULL,
    repo_name            VARCHAR(255)    NULL,
    auto_commit_enabled  BOOLEAN         NULL DEFAULT FALSE,
    created_at           DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE oauth_account (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id            BIGINT        NOT NULL,
    provider           VARCHAR(20)   NOT NULL,
    provider_user_id   VARCHAR(255)  NOT NULL,
    access_token       VARCHAR(500)  NULL,
    created_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_oauth_account_user
        FOREIGN KEY (user_id) REFERENCES user (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE retrospective (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT   NOT NULL,
    date        DATE     NOT NULL,
    keep        TEXT     NULL,
    problem     TEXT     NULL,
    try         TEXT     NULL,
    score       INT      NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_retrospective_user
        FOREIGN KEY (user_id) REFERENCES user (id)
        ON DELETE CASCADE,
    UNIQUE KEY uq_retrospective_user_date (user_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
