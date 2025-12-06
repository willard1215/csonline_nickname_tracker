package com.cso_nickname_tracker.Jasper.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "nickname_history",
        indexes = {
                @Index(name = "idx_nickname_history_user_id", columnList = "user_id"),
                @Index(name = "idx_nickname_history_changed_at", columnList = "changed_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class NicknameHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 닉네임이 변경된 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Setter(AccessLevel.PACKAGE)   // User.addNicknameHistory 에서만 세팅
    private User user;

    @Column(name = "before_nickname", nullable = false, length = 30)
    private String beforeNickname;

    @Column(name = "after_nickname", nullable = false, length = 30)
    private String afterNickname;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    // 정적 팩토리 (편의용)
    public static NicknameHistory of(User user, String beforeNickname, String afterNickname) {
        return NicknameHistory.builder()
                .user(user)
                .beforeNickname(beforeNickname)
                .afterNickname(afterNickname)
                .changedAt(LocalDateTime.now())
                .build();
    }
}
