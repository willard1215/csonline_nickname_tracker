package com.cso_nickname_tracker.Jasper.service;

import com.cso_nickname_tracker.Jasper.Dto.NicknameHistoryDto;
import com.cso_nickname_tracker.Jasper.Dto.UserDto;
import com.cso_nickname_tracker.Jasper.entity.NicknameHistory;
import com.cso_nickname_tracker.Jasper.entity.User;
import com.cso_nickname_tracker.Jasper.repository.NicknameHistoryRepository;
import com.cso_nickname_tracker.Jasper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class NicknameHistoryService {
    private final NicknameHistoryRepository repository;
    private final UserRepository userRepository;

    public List<NicknameHistoryDto> getByUserKey(String userKey) {
        Optional<User> user = userRepository.findByUserKey(userKey);
        return repository
                .findByUserOrderByChangedAtDesc(user)
                .stream()
                .map(NicknameHistoryDto::from)
                .toList();
    }

    @Transactional
    public NicknameHistory recordNicknameChange(User user,
                                                String oldNickname,
                                                String newNickname) {
        if (oldNickname == null || newNickname == null || oldNickname.equals(newNickname)) {
            return null;
        }

        NicknameHistory history = NicknameHistory.builder()
                .user(user)
                .beforeNickname(oldNickname)
                .afterNickname(newNickname)
                .changedAt(LocalDateTime.now())
                .build();

        return repository.save(history);
    }
}
