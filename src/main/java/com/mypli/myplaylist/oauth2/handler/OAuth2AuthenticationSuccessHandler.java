package com.mypli.myplaylist.oauth2.handler;

import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.domain.Role;
import com.mypli.myplaylist.oauth2.Provider;
import com.mypli.myplaylist.oauth2.cookie.HttpCookieOAuth2AuthorizationRequestRepository;
import com.mypli.myplaylist.oauth2.jwt.JwtToken;
import com.mypli.myplaylist.oauth2.jwt.JwtTokenService;
import com.mypli.myplaylist.repository.MemberRepository;
import com.mypli.myplaylist.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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
import java.util.List;
import java.util.Optional;

import static com.mypli.myplaylist.oauth2.cookie.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final JwtTokenService tokenService;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //TODO: oauth2_auth_request (파라미터 이용하라던가 뭐랬나)
        log.info("Authentication Success");
        //log.info("authentication = {}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(authentication));

        /*String[] path = request.getRequestURI().split("/");
        Provider provider = Provider.valueOf(path[path.length - 1].toUpperCase());
        String oauthId = authentication.getName();

        //1. Token 생성
        JwtToken token = tokenService.generateToken(oauthId, Role.ROLE_USER.getKey());

        //2. Member 엔티티에 RefreshToken 입력
        updateMemberRefreshToken(memberRepository.findBySocialId(oauthId).get(), token.getRefreshToken());

        //3. Header에 Token 정보 추가
        writeTokenResponse(request, response, token);

        //4. Redirect
        String uri = UriComponentsBuilder.fromUriString("http://localhost:8080/social")
                .queryParam("provider", provider)
                .queryParam("oauthId", oauthId)
                .build().toUriString();
        response.sendRedirect(uri);*/

        String targetUrl = determineTargetUrl(request, response, authentication);
        log.info("targetUrl = {}", targetUrl);

        if (response.isCommitted()) {
            log.debug("응답이 이미 커밋되었습니다. " + targetUrl + "로 리다이렉션을 할 수 없습니다.");
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /*
    private void writeTokenResponse(HttpServletRequest request, HttpServletResponse response, JwtToken token) {
        response.setContentType("text/html;charset=UTF-8");
        tokenService.responseAccessToken(response, token.getAccessToken());
        tokenService.responseRefreshToken(request, response, token.getRefreshToken());
        response.setContentType("application/json;charset=UTF-8");
    }*/

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
        String oauthId = authentication.getName();
        JwtToken token = tokenService.generateToken(oauthId, Role.ROLE_USER.getKey()); //TODO: 한번에 만들지 말고 refresh token은 다르게

        //3. Member 엔티티에 RefreshToken 입력
        updateMemberRefreshToken(memberRepository.findBySocialId(oauthId).get(), token.getRefreshToken());

        //4. Cookie에 RefreshToken 추가
        tokenService.responseRefreshToken(request, response, token.getRefreshToken());

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

        /**
         * 사람들 한거 보면 application.yml에 써놓음
         * 안드로이드, 아이폰 막 그런거에 따라서 달라지는듯
         */
        List<String> authorizedRedirectUris = new ArrayList<>();
        authorizedRedirectUris.add("http://localhost:8080/oauth2/redirect");

        return authorizedRedirectUris
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    // Only validate host and port. Let the clients use different paths if they want to
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                        && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }

}
