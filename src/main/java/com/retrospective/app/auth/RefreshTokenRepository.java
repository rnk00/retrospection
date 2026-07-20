package com.retrospective.app.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * RefreshToken Entity의 DB 조회/저장을 담당하는 Repository
 *
 * 역할
 * - RefreshToken에 대한 기본 CRUD 제공 (JpaRepository 상속으로 자동 획득)
 * - 토큰 갱신 요청 시 필요한 token 값 기반 조회 제공
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 메서드 이름 자체가 쿼리가 됨: findBy + token -> WHERE token = ? 쿼리를 Spring이 자동 생성
    // 갱신 요청에서 "클라이언트가 보낸 이 토큰이 DB에 있는 게 맞는지, 누구 건지" 확인할 때 사용.
    // 없을 수도 있어서(위조/만료 후 삭제 등) null 대신 Optional로 감싸서 반환
    Optional<RefreshToken> findByToken(String token);
}
