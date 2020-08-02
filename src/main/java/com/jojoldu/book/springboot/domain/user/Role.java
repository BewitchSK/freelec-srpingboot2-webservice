package com.jojoldu.book.springboot.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자의 권한을 관리하는 Enum Class
 */
@Getter
@RequiredArgsConstructor
public enum Role {

    /**
     * Spring Security 에서는 권한코드에 항상 ROLE_이 앞에 붙어야 된다.
     */
    GUEST("ROLE_GUEST","손님"),
    USER("ROLE_USER","일반 사용자");

    private final String key;
    private final String title;
}
