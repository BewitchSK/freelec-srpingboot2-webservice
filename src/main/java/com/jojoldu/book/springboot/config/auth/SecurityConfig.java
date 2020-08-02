package com.jojoldu.book.springboot.config.auth;

import com.jojoldu.book.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
/**
 * Spring Security 설정 활성화
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                /**
                 * h2-console 화면을 사용하시 위하여 해당 옵션을 비활성화 한다.
                 */
                .csrf().disable()
                .headers().frameOptions().disable()

                .and()
                    /**
                     * URL별로 권한관리를 설정하는 옵션의 시작점.
                     * authorizeRequests가 선언되야 antMatcher 옵션 사용이 가능
                     */
                    .authorizeRequests()
                    /**
                     * 권한 관리 대상을 지정하는 옵션.
                     * URL, HTTP Method 별로 관리가 가능.
                     * permitAll() : 전체 열람 권한 부여.
                     * hasRole() : USER 권한을 가진 사용자에게 부여.
                     * anyRequest() : 설정된 값 외 나머지 URL에 대한 권한 설정.
                     * authenticated() : 인증된 사용자들(로그인한 사람들)에게만 허용.
                     */
                    .antMatchers("/","/css/**","/images/**","/js/**","/h2-console/**").permitAll()
                    .antMatchers("/api/v1/**").hasRole(Role.USER.name())
                    .anyRequest().authenticated()
                .and()
                    /**
                     * 로그아웃 기능에 대한 여러 설정에 진입점.
                     * 성공하면 / 주소로 이동.
                     */
                    .logout().logoutSuccessUrl("/")
                .and()
                    /**
                     * OAuth2 로그인 기능에 대한 여러 설정의 진입점
                     */
                    .oauth2Login()
                        /**
                         * OAuth2 로그인 성공 이후 사용자 정보를 가져올 때 설정
                         */
                        .userInfoEndpoint()
                            /**
                             * 소셜 로그인 성공 후, 후속 조치를 진행할 UserService 인터페이스의 구현체 등록.
                             * 소셜 서비스들(resource server)에서 사용자 정보를 가져온 상태에서 추가로 진행하려는 기능 명시.
                             */
                            .userService(customOAuth2UserService);


        super.configure(http);
    }
}
