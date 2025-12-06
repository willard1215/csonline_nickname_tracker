package com.cso_nickname_tracker.Jasper.repository;

import com.cso_nickname_tracker.Jasper.entity.UserActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActionLogRepository extends JpaRepository<UserActionLog, Long> {
}
