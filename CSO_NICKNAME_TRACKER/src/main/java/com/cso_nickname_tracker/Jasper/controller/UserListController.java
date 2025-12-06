package com.cso_nickname_tracker.Jasper.controller;

import com.cso_nickname_tracker.Jasper.Dto.UserDto;
import com.cso_nickname_tracker.Jasper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class UserListController {

    private final UserService userService;


    @GetMapping("/getUser")
    public String getUser(
            @RequestParam(name = "page", defaultValue = "0") int pageNumber,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model
    ) {
        Page<UserDto> page = userService.getUsers(
                PageRequest.of(Math.max(pageNumber, 0), size, Sort.by(Sort.Direction.DESC, "id"))
        );

        if (page.isEmpty()) {
            model.addAttribute("users", List.of());
            model.addAttribute("pageNumber", 0);
            model.addAttribute("size", size);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalUsers", 0L);
            return "user-list";
        }

        int totalPages = page.getTotalPages();
        long totalUsers = page.getTotalElements();

        if (pageNumber >= totalPages) {
            pageNumber = Math.max(totalPages - 1, 0);
            page = userService.getUsers(
                    PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.DESC, "id"))
            );
        }

        List<UserDto> content = page.getContent();

        model.addAttribute("users", content);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalUsers", totalUsers);

        return "user-list";
    }

}
