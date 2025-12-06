package com.cso_nickname_tracker.Jasper.service;

import com.cso_nickname_tracker.Jasper.Dto.UserActionLogDto;
import com.cso_nickname_tracker.Jasper.repository.UserActionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserActionLogService {

    private final UserActionLogRepository userActionLogRepository;

    public Page<UserActionLogDto> getLogs(Pageable pageable) {
        return userActionLogRepository.findAll(pageable)
                .map(UserActionLogDto::from);
    }
}
