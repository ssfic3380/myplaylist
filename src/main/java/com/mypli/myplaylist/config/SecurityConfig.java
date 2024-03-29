package com.mypli.myplaylist.config;

import com.mypli.myplaylist.oauth2.converter.CustomRequestEntityConverter;
import com.mypli.myplaylist.oauth2.converter.CustomTokenResponseConverter;
import com.mypli.myplaylist.oauth2.cookie.HttpCookieOAuth2AuthorizationRequestRepository;
import com.mypli.myplaylist.oauth2.handler.JwtAccessDeniedHandler;
import com.mypli.myplaylist.oauth2.exception.JwtAuthenticationEntryPoint;
import com.mypli.myplaylist.oauth2.jwt.JwtAuthFilter;
import com.mypli.myplaylist.oauth2.jwt.JwtTokenProvider;
import com.mypli.myplaylist.oauth2.resolver.CustomAuthorizationRequestResolver;
import com.mypli.myplaylist.oauth2.service.CustomOAuth2AuthService;
import com.mypli.myplaylist.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.mypli.myplaylist.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.mypli.myplaylist.oauth2.service.CustomOidcUserService;
import com.mypli.myplaylist.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;
    private final CustomOidcUserService customOidcUserService;
    private final CustomOAuth2AuthService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    private final JwtTokenProvider tokenProvider;
    private final MemberService memberService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .antMatchers("/css/**", "/img/**", "/js/**", "/webjars/**", "/favicon.ico", "/h2-console/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    /**
     * CSRF(Cross Site Request Forgery): 사용자가 자신의 의지와는 무관하게 공격자가 의도한 행위(수정, 삭제, 등록 등)를 특정 웹사이트에 요청하게 하는 공격
     *                                   (인증된 사용자가 위조된 Request를 보냈는데 서버가 이를 믿는 것)
     *                                   => 공격대상: 서버 (권한 남용)
     *                                   => Rest Api는 non-browser client만을 위한 서비스이기 때문에 CSRF 공격으로부터 안전해서 비활성화해도 됨
     *
     * XSS(Cross Site Scripting): 권한이 없는 사용자가 공격하려는 사이트에 스크립트를 삽입하는 기법
     *                            웹 애플리케이션이 사용자로부터 입력 받은 값을 제대로 검사하지 않고 사용할 때 나타나고,
     *                            공격에 성공하면 사이트에 접속한 사용자는 삽입된 코드를 실행해서, 의도치 않은 행동을 수행시키거나 쿠키나 세션 등의 민감한 정보를 탈취한다.
     *                            => 공격대상: 클라이언트 (쿠키/세션 갈취, 웹사이트 변조 등)
     *                            - Stored XSS: 게시판이나 댓글, 닉네임 등 스크립트가 서버에 저장되어 실행되는 방식
     *                            - Reflected XSS: URL 파라미터(특히 GET방식)에 스크립트를 넣어 서버에 저장하지 않고 그 즉시 스크립트를 만드는 방식
     *
     * CORS(Cross-Origin Resource Sharing): 서로 다른 출처(origin)를 가진 Applicaiton이 서로의 Resource에 접근할 수 있도록 하는 것
     *                                      (서로 같은 출처 = 프로토콜(http, https), 호스트(localhost), 포트(8080, 3000 등)이 모두 동일한 출처)
     */
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource())
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //스프링 시큐리티가 세션을 생성하지도 않고, 기존 것을 사용하지도 않음 (JWT 쓰니까)
                .and()
                    .csrf().disable() //csrf 토큰 없이 POST 가능
                    .formLogin().disable()
                    .httpBasic().disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                    .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
                    .authorizeRequests()
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    .antMatchers("/oauth2/**", "/").permitAll()
                    .anyRequest().authenticated()
//                    .anyRequest().permitAll()
                .and()
                    .oauth2Login()
                    .authorizationEndpoint()
                    .authorizationRequestResolver(new CustomAuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization")) //OAuth2 인증을 요청할 때, 매개 변수 추가(구글 refreshToken을 위함)
                    //.baseUri("/oauth2/authorization") //클라이언트가 로그인 페이지로 이동하기 위해 사용할 URI (위에 CustomAuthorizationRequestResolver 때문에 주석 처리)
                    .authorizationRequestRepository(authorizationRequestRepository) //사이트 로그인 이후 리다이렉션할 URI를 저장하고 있는 저장소 (?redirect_uri= 의 값을 가지고 있음)
                .and()
                    .redirectionEndpoint()
                    .baseUri("/*/oauth2/code/*") //클라이언트가 로그인을 성공하면 인가 코드를 받아올 URI
                .and()
                    .tokenEndpoint()
                    .accessTokenResponseClient(customAccessTokenResponseClient()) //토큰 요청, 응답 내용을 재정의
                .and()
                    .userInfoEndpoint()
                    .oidcUserService(customOidcUserService)
                    .userService(customOAuth2UserService)
                .and()
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler);

        http.addFilterBefore(new JwtAuthFilter(tokenProvider, memberService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));                          //자원을 공유할 오리진(출처) 지정
        configuration.setAllowedMethods(Arrays.asList("HEAD","POST","GET","DELETE","PUT"));  //요청을 허용할 메소드
        configuration.setAllowedHeaders(Arrays.asList("*"));                                 //요청을 허용할 헤더
        configuration.setAllowCredentials(true);                                             //쿠키 허용
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);                       //CORS를 적용할 URL 패턴
        return source;
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> customAccessTokenResponseClient() {

        //토큰 요청을 사용자 정의
        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        accessTokenResponseClient.setRequestEntityConverter(new CustomRequestEntityConverter());

        //토큰 응답 처리를 사용자 정의
        OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
        tokenResponseHttpMessageConverter.setTokenResponseConverter(new CustomTokenResponseConverter());
        RestTemplate restTemplate = new RestTemplate(Arrays.asList(new FormHttpMessageConverter(), tokenResponseHttpMessageConverter));
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

        accessTokenResponseClient.setRestOperations(restTemplate);

        return accessTokenResponseClient;
    }

}
