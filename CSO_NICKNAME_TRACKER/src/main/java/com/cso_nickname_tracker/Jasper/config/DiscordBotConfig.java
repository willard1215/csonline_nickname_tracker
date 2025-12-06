package com.cso_nickname_tracker.Jasper.config;

import com.cso_nickname_tracker.Jasper.service.DiscordBotService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Getter
@Component
public class DiscordBotConfig {
    @Value("${discord.bot.token}")
    private String token;

}
