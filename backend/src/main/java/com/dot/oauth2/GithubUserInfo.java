package com.dot.oauth2;

import java.util.Map;

public class GithubUserInfo implements OAuth2UserInfo {

    private final String providerId;
    private final String email;
    private final String nickname;
    private final String profileImage;

    public static GithubUserInfo from(Map<String, Object> attributes) {
        return new GithubUserInfo(
                String.valueOf(attributes.get("id")),
                (String) attributes.get("email"),
                (String) attributes.getOrDefault("name", attributes.get("login")),
                (String) attributes.get("avatar_url")
        );
    }

    public GithubUserInfo(String providerId, String email, String nickname, String profileImage) {
        this.providerId = providerId;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    @Override public String getProviderId() { return providerId; }
    @Override public String getEmail() { return email != null ? email : providerId + "@github.com"; }
    @Override public String getNickname() { return nickname; }
    @Override public String getProfileImage() { return profileImage; }
}
