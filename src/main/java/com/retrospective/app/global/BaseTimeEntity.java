package com.retrospective.app.global;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 생성/수정 시각을 자동으로 기록하는 공통 부모 클래스
 *
 * 역할
 * - createdAt/updatedAt 둘 다 필요한 Entity(User, Retrospective, KptNote)가 상속해서 씀
 * - 매 Entity마다 이 두 필드를 반복해서 안 쓰기 위해 분리
 *
 * 대응 테이블 : 없음 (아래 참고)
 */
@Getter
// 이 클래스 자체는 테이블이 아니고, 상속한 Entity의 테이블에 컬럼으로 끼워 넣어지는 용도
@MappedSuperclass
// @CreatedDate/@LastModifiedDate 값을 실제로 채워주는 리스너를 연결
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    // Entity가 처음 저장될 때 한 번만 자동으로 채워짐 (이후 안 바뀜)
    @CreatedDate
    private LocalDateTime createdAt;

    // Entity가 수정될 때마다 자동으로 갱신됨
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
