package com.cso_nickname_tracker.Jasper.Dto;

import com.cso_nickname_tracker.Jasper.entity.User;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDto {
    String userKey;
    String nickname;

    public static UserDto from(User user) {
        return UserDto.builder()
                .userKey(user.getUserKey())
                .nickname(user.getNickname())
                .build();
    }
}
