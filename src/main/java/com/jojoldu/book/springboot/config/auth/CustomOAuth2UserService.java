package com.jojoldu.book.springboot.config.auth;

import com.jojoldu.book.springboot.config.auth.dto.OAuthAttributes;
import com.jojoldu.book.springboot.config.auth.dto.SessionUser;
import com.jojoldu.book.springboot.domain.user.User;
import com.jojoldu.book.springboot.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

/**
 * 구글 로그인 이후 가져온 사용자의 정보(email, name, picture 등) 기반으로
 * 가입, 정보수정, 세션 저장 기능 지원
 */
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>{

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        /** 로그인 진행중인 서비스 구분코드(구글, 네이버 등).
         * /oauth2/authorization/{service}에서 {service}의 값을 말한다.
         */
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        /** OAuth2 로그인 진행시 키가 되는 필드값(PK). */
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                                                    .getUserInfoEndpoint().getUserNameAttributeName();

        /** OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스. */
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        /** SessionUser : 세션에 사용자 정보를 저장
         * 직렬화(Serializable) 클래스를 대입.
         * User를 직렬화 하여 대입할 수 있으나 차후 다른 Entity와 관계를 형성하게 되면
         * 직렬화 대상에 자식들 마저 포함되어 성능문제나 사이드 이펙트가 있을 수 있다.
         * 따라서 직렬화 기능을 가진 Session Dto를 따로 만들어 두는 것이 유지보수에 있어 효율적이다.
         */
        httpSession.setAttribute("user",new SessionUser(user));

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                                     attributes.getAttributes(),
                                     attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes){
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture() ))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}
