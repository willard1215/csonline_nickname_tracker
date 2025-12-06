package com.cso_nickname_tracker.Jasper.controller;

import com.cso_nickname_tracker.Jasper.Dto.NicknameHistoryDto;
import com.cso_nickname_tracker.Jasper.Dto.UserDto;
import com.cso_nickname_tracker.Jasper.entity.User;
import com.cso_nickname_tracker.Jasper.service.NicknameHistoryService;
import com.cso_nickname_tracker.Jasper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserHistoryController {

    private final UserService userService;
    private final NicknameHistoryService nicknameHistoryService;

    @GetMapping("/user/history")
    public String getUserHistory(
            @RequestParam("userKey") String userKey,
            Model model
    ) {
        User user = userService.getUserbyUserKey(userKey);  // userKey로 유저 조회

        List<NicknameHistoryDto> histories = nicknameHistoryService.getByUserKey(userKey);

        model.addAttribute("userKey", userKey);
        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("histories", histories);   // 🔥 리스트 통째로 넘김

        return "user-history";
    }
}


