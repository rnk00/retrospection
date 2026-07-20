package com.retrospective.app.auth;

import com.retrospective.app.global.BaseCreatedTimeEntity;
import com.retrospective.app.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 로그인 상태 유지를 위한 Refresh Token을 저장하는 Entity
 *
 * 역할
 * - Access Token(JWT) 만료 시, 재로그인 없이 새 토큰을 발급받기 위해 사용
 * - 로그아웃 시 이 Entity(행)를 삭제하면 그 토큰은 즉시 무효화됨
 *   (Access Token은 서명만 검증하므로 이런 식의 무효화가 불가능해서 별도 관리)
 *
 * 대응 테이블 : refresh_token
 */
@Entity
// uniqueConstraints: 토큰 값으로 빠르고 정확하게 조회하기 위함 (갱신 요청 시 이 값으로 찾음)
@Table(
    name = "refresh_token",
    uniqueConstraints = @UniqueConstraint(columnNames = "token")
)
@Getter
@Setter
// token은 탈취되면 그 자체로 로그인이 가능해지는 민감 정보라 toString()에서 제외
@ToString(exclude = "token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User 1 : RefreshToken N (기기별로 여러 개 가질 수 있어서 user_id에 UNIQUE 없음)
    // fetch = LAZY: user 필드를 실제로 쓸 때만 User를 DB에서 조회함.
    // 기본값(EAGER)으로 두면 RefreshToken을 조회할 때마다 매번 User까지 같이 불러와서 낭비가 됨
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 리프레시 토큰 값 자체
    @Column(nullable = false)
    private String token;

    // 만료 시각. 이 시각이 지나면 갱신 요청에 사용할 수 없음
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public RefreshToken(User user, String token, LocalDateTime expiresAt) {
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
    }
}
