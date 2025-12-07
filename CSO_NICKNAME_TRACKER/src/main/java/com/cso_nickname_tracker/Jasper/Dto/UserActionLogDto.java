package com.cso_nickname_tracker.Jasper.Dto;

import com.cso_nickname_tracker.Jasper.entity.User;
import com.cso_nickname_tracker.Jasper.entity.UserActionLog;
import com.cso_nickname_tracker.Jasper.entity.UserActionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserActionLogDto {

    private Long id;
    private String userKey;
    private String nickname;
    private UserActionType actionType;
    private String actorDiscordId;
    private LocalDateTime createdAt;

    public static UserActionLogDto from(UserActionLog entity) {
        return UserActionLogDto.builder()
                .id(entity.getId())
                .userKey(entity.getUserKey())
                .nickname(entity.getNickname())
                .actionType(entity.getActionType())
                .actorDiscordId(entity.getActorDiscordId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
