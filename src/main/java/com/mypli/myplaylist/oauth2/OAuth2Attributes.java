package com.mypli.myplaylist.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.domain.MemberProfile;
import com.mypli.myplaylist.domain.Role;
import com.mypli.myplaylist.domain.SocialCode;
import com.mypli.myplaylist.oauth2.exception.OAuth2RegistrationException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class OAuth2Attributes {

    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String oauthId;
    private final String email;
    private final String name;
    private final String picture;
    private final Provider provider;

    @Builder
    public OAuth2Attributes(Map<String, Object> attributes, String nameAttributeKey, String oauthId, String email, String name, String picture, Provider provider) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.oauthId = oauthId;
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.provider = provider;
    }

    @SneakyThrows
    public static OAuth2Attributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        log.info("userNameAttributeName = {}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(userNameAttributeName));
        log.info("attributes = {}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(attributes));

        String registrationIdToLower = registrationId.toLowerCase();
        switch(registrationIdToLower) {
            case "google":
                return ofGoogle(userNameAttributeName, attributes);
            case "naver":
                return ofNaver(userNameAttributeName, attributes);
            case "kakao":
                return ofKakao(userNameAttributeName, attributes);
            default:
                throw new OAuth2RegistrationException("해당 소셜 로그인은 현재 지원하지 않습니다.");
        }
    }

    private static OAuth2Attributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .oauthId((String) attributes.get(userNameAttributeName))
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .provider(Provider.GOOGLE)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    @SuppressWarnings("unchecked")
    private static OAuth2Attributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuth2Attributes.builder()
                .oauthId((String) response.get("id"))
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_img"))
                .provider(Provider.NAVER)
                .nameAttributeKey("id")
                .build();
    }

    @SuppressWarnings("unchecked")
    private static OAuth2Attributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        return OAuth2Attributes.builder()
                .oauthId(attributes.get(userNameAttributeName).toString())
                .name((String) profile.get("name"))
                .email((String) account.get("email"))
                .picture((String) profile.get("profile_image_url"))
                .provider(Provider.KAKAO)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    /**
     * Member entity 등록
     */
    public Member toEntity() {
        Member member = Member.builder()
                .role(Role.ROLE_USER)
                .socialCode(SocialCode.valueOf(provider.name()))
                .socialId(oauthId)
                .jwtRefreshToken("new member")
                .build();

        MemberProfile memberProfile = MemberProfile.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .build();

        member.setMemberProfile(memberProfile);

        return member;
    }
}
