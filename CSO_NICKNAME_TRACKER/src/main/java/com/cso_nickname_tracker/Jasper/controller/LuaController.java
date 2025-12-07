package com.cso_nickname_tracker.Jasper.controller;

import com.cso_nickname_tracker.Jasper.service.LuaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class LuaController {

    private final LuaService luaService;

    @GetMapping("/banlist.lua")
    public ResponseEntity<byte[]> downloadBanList() {
        byte[] luaBytes = luaService.buildBanListLuaBytes();

        String filename = "BanList.lua";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" +
                                java.net.URLEncoder.encode(filename, StandardCharsets.UTF_8))
                .contentType(MediaType.TEXT_PLAIN)
                .body(luaBytes);
    }

}
