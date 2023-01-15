package com.mypli.myplaylist.oauth2.handler;

import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.oauth2.cookie.HttpCookieOAuth2AuthorizationRequestRepository;
import com.mypli.myplaylist.oauth2.jwt.JwtToken;
import com.mypli.myplaylist.oauth2.jwt.JwtTokenProvider;
import com.mypli.myplaylist.repository.MemberRepository;
import com.mypli.myplaylist.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;

import static com.mypli.myplaylist.oauth2.cookie.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider tokenProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Value("${app.oauth2.authorizedRedirectUris}")
    private final ArrayList<String> AUTHORIZED_REDIRECT_URIS;
    private final String REFRESH_TOKEN = "refresh-token";
    private final int COOKIE_PERIOD = 60 * 60 * 24; //하루

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("Authentication Success");

        String targetUrl = determineTargetUrl(request, response, authentication);
        log.info("targetUrl = {}", targetUrl);

        if (response.isCommitted()) {
            log.debug("응답이 이미 커밋되었습니다. " + targetUrl + "로 리다이렉션을 할 수 없습니다.");
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }

    @Transactional
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        //1. 인증 시작 당시의 URI 획득
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new IllegalArgumentException(("승인되지 않은 리다이렉션 URI가 있어 인증을 진행할 수 없습니다."));
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        //2. Token 생성
        JwtToken token = tokenProvider.generateToken(authentication);

        //3. Member 엔티티에 RefreshToken 입력
        String socialId = authentication.getName();
        updateMemberRefreshToken(memberRepository.findBySocialId(socialId).get(), token.getRefreshToken());

        //4. Cookie에 RefreshToken 추가
        CookieUtils.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtils.addCookie(response, REFRESH_TOKEN, token.getRefreshToken(), COOKIE_PERIOD);

        //5. AccessToken과 함께 원래 있던 곳으로 redirect
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token.getAccessToken())
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    @Transactional
    public void updateMemberRefreshToken(Member member, String refreshToken) {
        member.updateJwtRefreshToken(refreshToken);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        log.info("authorized redirect uris = {}", AUTHORIZED_REDIRECT_URIS);

        return AUTHORIZED_REDIRECT_URIS
                .stream()
                .anyMatch(uris -> {
                    // Only validate host and port. Let the clients use different paths if they want to
                    URI authorizedURI = URI.create(uris);
                    if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                        && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }

}
