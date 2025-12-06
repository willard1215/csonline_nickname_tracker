package com.cso_nickname_tracker.Jasper.repository;
import com.cso_nickname_tracker.Jasper.entity.NicknameHistory;
import com.cso_nickname_tracker.Jasper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NicknameHistoryRepository extends JpaRepository<NicknameHistory, Long> {

    /**
     * 특정 유저의 닉네임 변경 이력을 시간 역순으로 조회
     * 예) 최근 변경 내역부터 순서대로 출력
     */
    List<NicknameHistory> findByUserOrderByChangedAtDesc(Optional<User> user);
}
