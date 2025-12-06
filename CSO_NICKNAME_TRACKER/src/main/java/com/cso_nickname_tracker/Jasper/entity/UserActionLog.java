package com.cso_nickname_tracker.Jasper.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_action_log")
public class UserActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", length = 16, nullable = false)
    private UserActionType actionType;

    @Column(name = "actor_discord_id", length = 32, nullable = false)
    private String actorDiscordId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static UserActionLog of(User user, UserActionType type, String actorDiscordId) {
        return UserActionLog.builder()
                .user(user)
                .actionType(type)
                .actorDiscordId(actorDiscordId)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
