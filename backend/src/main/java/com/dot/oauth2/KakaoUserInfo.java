package com.dot.oauth2;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {

    private final String providerId;
    private final String email;
    private final String nickname;
    private final String profileImage;

    @SuppressWarnings("unchecked")
    public static KakaoUserInfo from(Map<String, Object> attributes) {
        String providerId = String.valueOf(attributes.get("id"));

        // scope 없이 로그인 시 kakao_account/profile 없을 수 있으므로 안전하게 처리
        String nickname = "user_" + providerId;
        String email = providerId + "@kakao.com";
        String profileImage = null;

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount != null) {
            if (kakaoAccount.get("email") != null) email = (String) kakaoAccount.get("email");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile != null) {
                if (profile.get("nickname") != null) nickname = (String) profile.get("nickname");
                if (profile.get("profile_image_url") != null) profileImage = (String) profile.get("profile_image_url");
            }
        }

        return new KakaoUserInfo(providerId, email, nickname, profileImage);
    }

    public KakaoUserInfo(String providerId, String email, String nickname, String profileImage) {
        this.providerId = providerId;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    @Override public String getProviderId() { return providerId; }
    @Override public String getEmail() { return email; }
    @Override public String getNickname() { return nickname; }
    @Override public String getProfileImage() { return profileImage; }
}
