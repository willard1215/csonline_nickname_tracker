package com.cso_nickname_tracker.Jasper.Dto;

import com.cso_nickname_tracker.Jasper.entity.NicknameHistory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NicknameHistoryDto {

    private Long id;
    private String beforeNickname;
    private String afterNickname;
    private LocalDateTime changedAt;

    public static NicknameHistoryDto from(NicknameHistory entity) {
        return NicknameHistoryDto.builder()
                .id(entity.getId())
                .beforeNickname(entity.getBeforeNickname())
                .afterNickname(entity.getAfterNickname())
                .changedAt(entity.getChangedAt())
                .build();
    }
}
