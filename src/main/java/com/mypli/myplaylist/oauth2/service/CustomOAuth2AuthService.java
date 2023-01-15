package com.mypli.myplaylist.oauth2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.domain.MemberProfile;
import com.mypli.myplaylist.domain.Role;
import com.mypli.myplaylist.oauth2.OAuth2Attributes;
import com.mypli.myplaylist.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2AuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Transactional
    @SneakyThrows
    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {

        //1. 성공 정보를 바탕으로 Service 객체를 생성하고, 이로부터 OAuth2User 객체를 받아온다.
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(request);

        //2. 받은 OAuth2User로부터 등록 ID(registration ID)와 PK(userNameAttributeName)를 뽑는다.
        /**
         * registrationId: 현재 로그인을 진행 중인 소셜 서비스를 구분하는 코드 (구글, 네이버, 카카오, ...)
         * userNameAttributeName: OAuth2 로그인 진행 시 키가 되는 필드값 (=Primary Key)
         *                        구글 기본 코드는 sub, 네이버 카카오 등은 기본 지원 X (이후 구글, 네이버 등 로그인 동시 지원시 사용)
         */
        String registrationId = request.getClientRegistration().getRegistrationId();
        String userNameAttributeName = request.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        log.info("registrationId = {}", registrationId);
        log.info("userNameAttributeName = {}", userNameAttributeName);

        //3. of() 메서드를 통해서 각각에 맞는 third party 앱에서 데이터를 뽑아서 OAuth2Attribute로 만든다.
        /**
         * attributes: OAuth2UserService를 통해 가져온 OAuth2User의 attribute
         */
        OAuth2Attributes attributes = OAuth2Attributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        //log.info("attributes = {}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(attributes));

        //4. DB에 User 정보를 저장하거나, 바뀐 정보를 업데이트한다. (회원 가입)
        Optional<Member> optionalMember = memberRepository.findBySocialId(attributes.getOauthId());
        Member member;
        if(optionalMember.isPresent()) {
            member = optionalMember.get();
            member = updateMember(member, attributes);
        }
        else {
            member = registerMember(attributes);
        }

        //5. 권한을 ROLE_USER로 설정하고, SuccessHandler 혹은 FailureHandler가 사용할 수 있도록 등록한다.
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.getKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    @Transactional
    Member updateMember(Member member, OAuth2Attributes attributes) {
        MemberProfile memberProfile = member.getMemberProfile();
        memberProfile.updateProfile(attributes.getName(), attributes.getEmail(), attributes.getPicture());
        return memberRepository.save(member);
    }

    @Transactional
    Member registerMember(OAuth2Attributes attributes) {
        Member member = attributes.toEntity();
        return memberRepository.save(member);
    }

}
