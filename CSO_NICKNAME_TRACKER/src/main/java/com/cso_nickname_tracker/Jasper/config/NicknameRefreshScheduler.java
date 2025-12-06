package com.cso_nickname_tracker.Jasper.config;

import com.cso_nickname_tracker.Jasper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class NicknameRefreshScheduler {
    private final UserService userService;

    @Scheduled(fixedDelay = 10 * 60 * 1000L)
    public void refreshNicknames() {
        userService.refreshAllUserNicknames();
    }
}
