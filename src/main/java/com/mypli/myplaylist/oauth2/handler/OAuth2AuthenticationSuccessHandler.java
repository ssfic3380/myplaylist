package com.mypli.myplaylist.oauth2.handler;

import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.oauth2.cookie.HttpCookieOAuth2AuthorizationRequestRepository;
import com.mypli.myplaylist.oauth2.jwt.JwtToken;
import com.mypli.myplaylist.oauth2.jwt.JwtTokenProvider;
import com.mypli.myplaylist.service.MemberService;
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

    private final MemberService memberService;
    private final JwtTokenProvider tokenProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Value("${app.oauth2.authorizedRedirectUris}")
    private final ArrayList<String> AUTHORIZED_REDIRECT_URIS;
    private final String REFRESH_TOKEN = "refresh-token";
    private final int COOKIE_PERIOD = 60 * 60 * 24; //하루

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("Authentication Success: socialId = {}", authentication.getName());

        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to {}", targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Transactional
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        //1. 인증 시작 당시에 등록한 redirect URI 획득
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new IllegalArgumentException(("Authentication Failed: Unauthorized Redirect URI"));
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        //2. Token 생성
        JwtToken token = tokenProvider.generateToken(authentication);

        //3-1. Member 엔티티에 RefreshToken 입력
        String socialId = authentication.getName();
        Member member = memberService.findBySocialId(socialId);
        updateMemberRefreshToken(member, token.getRefreshToken());

        //3-2. Cookie에 RefreshToken 추가
        CookieUtils.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtils.addCookie(response, REFRESH_TOKEN, token.getRefreshToken(), COOKIE_PERIOD);

        //4. AccessToken과 함께 원래 있던 곳으로 redirect
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

        return AUTHORIZED_REDIRECT_URIS
                .stream()
                .anyMatch(uris -> {
                    // host와 port만 검증한다. path는 클라이언트가 원하는 대로 정하도록 한다.
                    URI authorizedURI = URI.create(uris);
                    if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }
}
