package com.cso_nickname_tracker.Jasper.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_user_key", columnList = "user_key", unique = true),
                @Index(name = "idx_users_nickname", columnList = "nickname")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // 빌더 전용
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // 내부 PK

    // 서비스 레이어에서 사용하는 문자열 고유번호
    @Column(name = "user_key", nullable = false, unique = true, length = 64)
    private String userKey;

    @Column(name = "nickname", nullable = false, length = 30)
    private String nickname;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<NicknameHistory> nicknameHistories = new ArrayList<>();

    // 정적 팩토리 메서드 (원하면 빌더 대신 이걸 사용)
    public static User create(String userKey, String nickname) {
        return User.builder()
                .userKey(userKey)
                .nickname(nickname)
                .build();
    }

    // ==== 비즈니스 메서드 ====
    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void addNicknameHistory(NicknameHistory history) {
        this.nicknameHistories.add(history);
        history.setUser(this);
    }
}
