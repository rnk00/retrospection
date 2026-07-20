package com.retrospective.app.user;

import com.retrospective.app.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 사용자 정보를 저장하는 Entity
 *
 * 역할
 * - 카카오/GitHub 소셜 로그인 사용자 관리 (일반 회원가입 없음)
 * - GitHub 저장소 자동 커밋 연동 정보(PAT, repo) 관리
 * - Retrospective, KptNote 등 다른 Entity의 부모 (User 1 : N)
 *
 * 대응 테이블 : user
 */
// 이 클래스는 그냥 자바 클래스가 아니라 DB 테이블과 연결된 클래스라는 표시.
// 이게 있어야 JPA가 이 클래스를 저장/조회 대상으로 인식함
@Entity
// name="user": 클래스 이름(User)만으로는 테이블명을 확신할 수 없어서 명시적으로 지정
// uniqueConstraints: 같은 provider 계정으로 중복 가입되는 것을 DB 레벨에서 막음
@Table(
    name = "user",
    uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_user_id"})
)
@Getter // 모든 필드의 getXxx()를 Lombok이 자동 생성
@Setter // 아무 필드나 자유롭게 바꿀 수 있게 허용하기로 결정 (도메인 메서드 대신 선택)
// githubPat은 절대 로그에 노출되면 안 되는 민감 정보라 toString() 대상에서 제외.
// (단, Controller가 Entity를 그대로 응답으로 반환하지 않는 것이 더 중요한 방어선 — DTO 분리는 Controller 작성 시 적용)
@ToString(exclude = "githubPat")
// JPA는 리플렉션으로 객체를 만들 때 파라미터 없는 생성자가 필요함.
// public으로 열어두면 코드 어디서나 값 없는 빈 User를 실수로 만들 수 있어서 protected로 막음
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    // IDENTITY: DB의 AUTO_INCREMENT에 채번을 맡김 (MySQL 기준 일반적인 선택)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 표시용 이메일. provider가 다르면 같은 이메일이어도 별개 유저로 취급하므로 UNIQUE 아님
    @Column(nullable = false)
    private String email;

    // 사용자에게 보여지는 이름. 최초 로그인 시 GitHub는 username, 카카오는 프로필 이름을 기본값으로 사용
    @Column(nullable = false, length = 50)
    private String nickname;

    // 소셜 로그인 제공자 ('KAKAO' 또는 'GITHUB'). 계정 병합을 안 하기로 해서 유저는 평생 하나만 가짐
    @Column(nullable = false, length = 20)
    private String provider;

    // provider가 발급한 고유 ID (예: 카카오 회원번호, GitHub user id)
    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    // GitHub 자동 커밋 대상 저장소 소유자/이름. 연동 전엔 null
    // (@Column이 없으면 JPA가 필드명을 그대로 컬럼명으로 쓰고, nullable 허용으로 기본 처리함)
    private String repoOwner;

    private String repoName;

    // GitHub Personal Access Token. 연동 전엔 null (암호화 저장은 구현 시 반영 예정)
    private String githubPat;

    // PAT/repo가 등록돼 있어도 자동 커밋 자체를 껐다 켰다 할 수 있어야 해서 별도 boolean으로 둠
    @Column(nullable = false)
    private Boolean githubUploadEnabled = false;

    // 실제로 새 User를 만들 때 쓰는 생성자. 가입 시점에 정해지는 값만 받고,
    // repoOwner 등 GitHub 연동 정보는 나중에 설정 화면에서 채워지므로 여기서 받지 않음
    public User(String email, String nickname, String provider, String providerUserId) {
        this.email = email;
        this.nickname = nickname;
        this.provider = provider;
        this.providerUserId = providerUserId;
    }
}
