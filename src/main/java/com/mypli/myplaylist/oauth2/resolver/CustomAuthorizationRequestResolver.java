package com.mypli.myplaylist.oauth2.resolver;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private OAuth2AuthorizationRequestResolver defaultAuthorizationRequestResolver;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo, String authorizationRequestBaseUri) {
        defaultAuthorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, authorizationRequestBaseUri);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = defaultAuthorizationRequestResolver.resolve(request);
        if (authorizationRequest != null) {
            authorizationRequest = customAuthorizationRequest(authorizationRequest);
        }
        return authorizationRequest;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = defaultAuthorizationRequestResolver.resolve(request, clientRegistrationId);
        if (authorizationRequest != null) {
            authorizationRequest = customAuthorizationRequest(authorizationRequest);
        }
        return authorizationRequest;
    }

    /**
     * OAuth2 승인 요청을 할 때, 필요한 매개 변수를 추가하는 곳
     */
    private OAuth2AuthorizationRequest customAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest) {
        Map<String, Object> additionalParameters = new LinkedHashMap<>(authorizationRequest.getAdditionalParameters());
        additionalParameters.put("access_type", "offline");
        //additionalParameters.put("prompt", "consent"); //앱 재승인 요청 (개발 환경에서만 사용, refresh_token을 안 주는 아이디들이 있으면 이걸로 재승인 받아야 함)

        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .additionalParameters(additionalParameters)
                .build();
    }
}
