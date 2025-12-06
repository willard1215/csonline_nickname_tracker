package com.cso_nickname_tracker.Jasper.repository;
import com.cso_nickname_tracker.Jasper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 고유번호 기준으로 조회
    Optional<User> findByUserKey(String userKey);

    // 고유번호 중복 체크
    boolean existsByUserKey(String userKey);

    // 고유번호 기준 삭제
    long deleteByUserKey(String userKey);
}
