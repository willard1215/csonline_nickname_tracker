package com.cso_nickname_tracker.Jasper.controller;

import com.cso_nickname_tracker.Jasper.Dto.UserDto;
import com.cso_nickname_tracker.Jasper.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserBulkController {

    private final UserService userService;


    @PostMapping("/bulk")
    public ResponseEntity<?> bulkRegister(@RequestBody List<String> nicknames) {

        if (nicknames == null || nicknames.isEmpty()) {
            return ResponseEntity.badRequest().body("닉네임 리스트가 비어있습니다.");
        }

        List<UserDto> results = new ArrayList<>();

        for (String nickname : nicknames) {
            try {
                UserDto saved = userService.createUser(nickname, "Admin");
                results.add(saved);
            } catch (Exception e) {
                log.warn("닉네임 {} 등록 실패: {}", nickname, e.getMessage());
                // 실패한 경우에도 API 전체가 실패하지 않도록 continue
            }
        }

        return ResponseEntity.ok(results);
    }
}
