package com.cso_nickname_tracker.Jasper.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateUserRequest {

    private String userKey;
    private String nickname;
}
