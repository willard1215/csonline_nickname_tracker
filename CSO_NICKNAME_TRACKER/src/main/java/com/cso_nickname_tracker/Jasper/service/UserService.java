package com.cso_nickname_tracker.Jasper.service;

import com.cso_nickname_tracker.Jasper.Dto.UserDto;
import com.cso_nickname_tracker.Jasper.entity.NicknameHistory;
import com.cso_nickname_tracker.Jasper.entity.User;
import com.cso_nickname_tracker.Jasper.entity.UserActionLog;
import com.cso_nickname_tracker.Jasper.entity.UserActionType;
import com.cso_nickname_tracker.Jasper.repository.NicknameHistoryRepository;
import com.cso_nickname_tracker.Jasper.repository.UserActionLogRepository;
import com.cso_nickname_tracker.Jasper.repository.UserRepository;
import com.cso_nickname_tracker.Jasper.utils.CsoRecordUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NicknameHistoryRepository nicknameHistoryRepository;
    private final CsoRecordUtils csoRecordUtils;
    private final UserActionLogRepository userActionLogRepository;

    @Transactional(readOnly = true)
    public Page<UserDto> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserDto::from);
    }

    public UserDto createUser(String nickname, String actorDiscordId) {
        String nexonsn = csoRecordUtils.getSnFromNickname(nickname);

        if (userRepository.existsByUserKey(nexonsn)) {
            throw new IllegalArgumentException("이미 존재하는 userKey 입니다: " + nexonsn);
        }

        User user = User.create(nexonsn, nickname);

        NicknameHistory history = NicknameHistory.of(user, nickname, nickname);
        user.addNicknameHistory(history);

        User saved = userRepository.save(user);

        userActionLogRepository.save(
                UserActionLog.of(saved, UserActionType.SUBMIT, actorDiscordId)
        );

        return UserDto.from(saved);
    }

    public void deleteUser(String userKey, String actorDiscordId) {
        User user = userRepository.findByUserKey(userKey)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다.: "+ userKey));
        userActionLogRepository.save(
                UserActionLog.of(user, UserActionType.DELETE, actorDiscordId)
        );

        long number = userRepository.deleteByUserKey(userKey);

        log.warn("------");
        log.warn("{}",number);
        log.warn("------");
    }

    private final ReentrantLock refreshLock = new ReentrantLock();

    @Transactional
    public void refreshAllUserNicknames() {
        // 이미 다른 스레드가 돌리고 있으면 그냥 리턴
        if (!refreshLock.tryLock()) {
            log.info("닉네임 전체 업데이트가 이미 실행 중이므로 이번 호출은 무시합니다.");
            return;
        }

        try {
            List<User> users = userRepository.findAll();
            log.info("닉네임 전체 업데이트 시작. 대상 유저 수: {}", users.size());

            for (User user : users) {
                String nexonsn = user.getUserKey();

                try {
                    // 외부 API 호출로 최신 닉네임 조회
                    String latestNickname = csoRecordUtils.searchNicknameFromSn(nexonsn);

                    // 닉네임이 변경되지 않았으면 스킵
                    String currentNickname = user.getNickname();
                    if (currentNickname != null && currentNickname.equals(latestNickname)) {
                        continue;
                    }

                    log.info("닉네임 변경 감지: userKey={}, {} -> {}",
                            nexonsn, currentNickname, latestNickname);

                    // 엔티티 업데이트
                    user.changeNickname(latestNickname);

                    // 히스토리 기록
                    NicknameHistory history = NicknameHistory.of(user, currentNickname, latestNickname);
                    nicknameHistoryRepository.save(history);

                    // user는 영속 상태이므로 별도의 save() 호출 없이도 flush 시 반영됨
                    // (원하면 userRepository.save(user) 호출해도 됨)

                } catch (Exception ex) {
                    // 특정 유저에서 에러가 나도 전체 배치가 중단되지 않도록 처리
                    log.warn("유저 닉네임 갱신 실패: userKey={}. 에러={}",
                            user.getUserKey(), ex.getMessage(), ex);
                }
            }

            log.info("닉네임 전체 업데이트 완료.");

        } finally {
            refreshLock.unlock();
        }
    }

    public void changeNickname(String userKey, String newNickname) {
        User user = userRepository.findByUserKey(userKey)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 userKey 입니다: " + userKey));

        String oldNickname = user.getNickname();
        if (oldNickname.equals(newNickname)) {
            return;
        }

        user.changeNickname(newNickname);

        NicknameHistory history = NicknameHistory.of(user, oldNickname, newNickname);
        nicknameHistoryRepository.save(history);
    }

    public User getUserbyUserKey(String userKey) {
        User user = userRepository.findByUserKey(userKey).orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));

        return user;
    }
}

