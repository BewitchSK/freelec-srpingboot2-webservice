package com.jojoldu.book.springboot.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User의 CRUD를 담당
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 소셜 로그인으로 반환되는 값중 email을 통해
     * 이미 생성된 사용자인지 처음 가입하는 사용자인지 파악하기 위해
     * 사용되는 Method
     */
    Optional<User> findByEmail(String email);

}
