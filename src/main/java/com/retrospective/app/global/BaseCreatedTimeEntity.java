package com.retrospective.app.global;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 생성 시각만 자동으로 기록하는 공통 부모 클래스
 *
 * 역할
 * - createdAt만 필요한 이력성 Entity(AiRecommendation, WeeklySummary, Summary,
 *   ProblemPattern 등)가 상속해서 씀 — 이 Entity들은 수정되지 않는 데이터라
 *   updatedAt 자체가 필요 없음 (BaseTimeEntity와 다른 점)
 *
 * 대응 테이블 : 없음 (BaseTimeEntity와 같은 이유)
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseCreatedTimeEntity {

    @CreatedDate
    private LocalDateTime createdAt;
}
