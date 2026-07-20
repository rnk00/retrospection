package com.retrospective.app.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User Entity의 DB 조회/저장을 담당하는 Repository
 *
 * 역할
 * - User에 대한 기본 CRUD 제공 (JpaRepository 상속으로 자동 획득)
 * - 로그인 콜백에서 필요한 provider 기반 조회 제공
 */
/* JpaRepository<User, Long>를 상속하면 save/findById/findAll/delete 같은
기본 CRUD 메서드를 직접 구현하지 않아도 자동으로 사용할 수 있음. */
// <User, Long>은 "이 Repository가 다루는 Entity 타입, 그 Entity의 PK 타입"을 의미
public interface UserRepository extends JpaRepository<User, Long> {

    // 메서드 이름 자체가 쿼리가 됨: findBy + provider + And + providerUserId
    // -> WHERE provider = ? AND provider_user_id = ? 쿼리를 Spring이 자동 생성
    // 로그인 콜백에서 "이 provider 계정으로 가입된 유저가 이미 있는지" 확인할 때 사용.
    // 없을 수도 있어서 null 대신 Optional로 감싸서 반환
    Optional<User> findByProviderAndProviderUserId(String provider, String providerUserId);
}
