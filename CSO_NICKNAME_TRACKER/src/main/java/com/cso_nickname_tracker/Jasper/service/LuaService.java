package com.cso_nickname_tracker.Jasper.service;

import com.cso_nickname_tracker.Jasper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LuaService {
    private final UserRepository userRepository;

    public byte[] buildBanListLuaBytes() {
        List<String> nicknames = userRepository.findAllNicknames();

        String body = nicknames.stream()
                .map(this::toLuaString)
                .collect(Collectors.joining(",\n"));

        String lua = "BanList = {\n" + body + "\n}\n";
        return lua.getBytes(StandardCharsets.UTF_8);
    }

    private String toLuaString (String nickname) {
        if (nickname == null) {
            nickname = "";
        }
        String escaped = nickname
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");

        return "    \"" + escaped + "\"";
    }
}
