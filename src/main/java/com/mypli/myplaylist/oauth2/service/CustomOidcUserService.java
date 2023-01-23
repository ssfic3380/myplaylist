package com.mypli.myplaylist.oauth2.service;

import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.domain.MemberProfile;
import com.mypli.myplaylist.domain.Role;
import com.mypli.myplaylist.oauth2.OAuth2Attributes;
import com.mypli.myplaylist.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final MemberRepository memberRepository;

    @Transactional
    @SneakyThrows
    @Override
    public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {

        log.info("CustomOidcUserService");

        //1. 성공 정보를 바탕으로 Service 객체를 생성하고, 이로부터 User를 받아온다.
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(request);

        //2. 받은 OAuth2User로부터 정보를 뽑는다.
        String registrationId = request.getClientRegistration().getRegistrationId();
        String userNameAttributeName = request.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        log.info("registrationId = {}", registrationId);
        log.info("userNameAttributeName = {}", userNameAttributeName);

        //3. of() 메서드를 통해서 각각에 맞는 third party 앱에서 데이터를 뽑아서 OAuth2Attribute로 만든다.
        OAuth2Attributes attributes = OAuth2Attributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        //4. DB에 User 정보를 저장하거나, 바뀐 정보를 업데이트한다. (회원 가입)
        Member member = memberRepository.findBySocialId(attributes.getOauthId());
        if(member != null) {
            member = updateMember(member, attributes);
        }
        else {
            member = registerMember(attributes);
        }

        //5. 권한을 ROLE_USER로 설정하고, SuccessHandler 혹은 FailureHandler가 사용할 수 있도록 등록한다.
        return (OidcUser) new DefaultOAuth2User(
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
