package com.cso_nickname_tracker.Jasper.controller;

import com.cso_nickname_tracker.Jasper.Dto.UserActionLogDto;
import com.cso_nickname_tracker.Jasper.service.UserActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class LogController {

    private final UserActionLogService userActionLogService;

    @GetMapping("/logs")
    public String viewLogs(
            @RequestParam(name = "page", defaultValue = "0") int pageNumber,
            @RequestParam(name = "size", defaultValue = "20") int size,
            Model model
    ) {
        pageNumber = Math.max(pageNumber, 0);

        Page<UserActionLogDto> page = userActionLogService.getLogs(
                PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        model.addAttribute("logs", page.getContent());
        model.addAttribute("pageNumber", page.getNumber());
        model.addAttribute("size", page.getSize());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalLogs", page.getTotalElements());

        return "logs";
    }
}
