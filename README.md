# Retrospective AI

> 기록을 통해 문제를 발견하고, AI와 함께 다음 행동을 설계하는 개인 성장 관리 서비스

## Overview

Retrospective AI는 사용자의 일상적인 회고를 KPT(Keep, Problem, Try) 방식으로 기록하고,
축적된 데이터를 기반으로 AI가 개선 방향과 다음 행동을 제안하는 서비스이다.

목표: 기록을 저장하는 것을 넘어 반복되는 문제를 발견하고 지속적인 발전을 보조



## Motivation

회고는 성장 과정에서 중요한 활동이지만, 대부분 다음과 같은 문제를 가지고 있다.

* 회고를 작성해도 과거 기록을 다시 확인하지 않는다.
* 반복되는 문제를 스스로 발견하기 어렵다.
* "다음에는 어떻게 개선할지" 구체적인 행동으로 이어지기 어렵다.

Retrospective AI는 이러한 문제를 해결하기 위해
회고 데이터를 기반으로 AI 분석과 행동 추천을 제공한다.



# Core Features



## 1. Calendar-based Retrospective

캘린더를 통해 날짜별 회고를 관리한다.

### User Flow

```
Login
 ↓
Calendar
 ↓
Select Date
 ↓
Write Retrospective
 ↓
AI Analysis
 ↓
Next Try Recommendation
```



## 2. KPT Retrospective

하루의 경험을 KPT 방식으로 기록한다.

### Keep

잘했던 점, 유지하고 싶은 행동

Ex. 계획한 시간에 맞춰 작업을 완료했다.


### Problem

개선이 필요한 점, 어려웠던 부분

Ex. Spring Security 인증 흐름 이해가 부족했다.

### Try

문제를 해결하기 위해 다음에 시도할 행동

Ex. JWT 인증 구조를 직접 구현하며 흐름을 정리한다.



## 3. Calendar Visualization

작성된 회고 데이터를 캘린더에서 직관적으로 확인한다.

### Retrospective Status

| 상태         | 표시    |
| ---------- | ----- |
| 작성하지 않은 날짜 | Gray  |
| 작성 완료 날짜   | Green |

### Score Visualization

사용자가 입력한 회고 만족도 점수에 따라 색상 강도를 표현한다.

```
1점 : 낮은 강도
5점 : 높은 강도
```


# AI Features

## 1. AI Try Recommendation

작성된 KPT 데이터를 기반으로
다음 행동(Try)을 추천합니다.

Example:

Input

```
Problem:
최근 API 설계 과정에서 반복적으로 어려움을 겪음
```

Output

```
API 명세 작성 후 구현하는 개발 프로세스를 적용해보세요.
```



## 2. Problem Pattern Analysis

최근 회고 데이터를 분석하여 반복되는 문제를 발견합니다.

Example:

```
최근 30일 동안
"인증 구현" 관련 문제가 5회 반복되었습니다.

추천:
인증 흐름을 정리하는 실습 프로젝트 진행을 추천한다.
```



## 3. Weekly Summary

한 주 동안 작성한 회고를 기반으로 AI 요약을 제공한다.

분석 내용:

* 주요 성취
* 반복되는 문제
* 개선 방향



# GitHub Integration

작성한 회고를 GitHub Repository에 Markdown 파일 형태로 저장한다.

## Flow

```
Write Retrospective
        ↓
Generate Markdown
        ↓
Commit to GitHub Repository
```

Example:

```
retrospective-repository

├── 2026
│   ├── 06
│   │   ├── 2026-06-23.md
│   │   └── 2026-06-24.md
```

개인의 성장 기록을 GitHub 커밋 히스토리와 함께 관리할 수 있다.



# Architecture

```
Client(Vue)
      |
      |
Spring Boot API Server
      |
      |
MySQL Database

      |
      |
OpenAI API
```



# Database Design

## Retrospective

| Column     | Description |
| ---------- | ----------- |
| id         | 회고 ID       |
| user_id    | 사용자 ID      |
| date       | 회고 날짜       |
| keep       | Keep 내용     |
| problem    | Problem 내용  |
| try        | Try 내용      |
| score      | 만족도 점수      |
| created_at | 생성 시간       |



# Tech Stack

## Frontend

* Vue 3
* Vue Router

## Backend

* Spring Boot
* Spring Data JPA
* Spring Security
* MySQL

## AI

* OpenAI API
* Prompt Engineering

## External Service

* Kakao OAuth
* GitHub OAuth
* GitHub API



# Development Roadmap

## MVP

* [ ] 회원가입 / 로그인
* [ ] OAuth 로그인
* [ ] 캘린더 기반 회고 작성
* [ ] 회고 CRUD
* [ ] 회고 상태 시각화



## Advanced

* [ ] AI Try 추천
* [ ] 반복 문제 분석
* [ ] 주간 회고 요약



## Extension

* [ ] GitHub 자동 커밋
* [ ] 회고 통계 Dashboard
* [ ] 회고 Template
* [ ] Tag 기반 검색
