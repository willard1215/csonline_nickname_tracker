package com.cso_nickname_tracker.Jasper.config;

import com.cso_nickname_tracker.Jasper.service.DiscordBotService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JdaConfig {
    private final DiscordBotConfig config;

    private final DiscordBotService discordBotService;

    @Bean
    public JDA jda() throws Exception {
        JDA jda = JDABuilder.createDefault(config.getToken())
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
//                .addEventListeners(testListener, discordBotService)
                .build();
        jda.awaitReady();

    jda.updateCommands()
            .addCommands(
                            Commands.slash("조회", "닉네임 목록을 조회합니다."),
                            Commands.slash("추가", "닉네임 목록에 추가합니다.")
                                            .addOption(OptionType.STRING, "닉네임", "추가할 닉네임을 입력해주세요.", true),
                            Commands.slash("제거", "닉네임 목록에서 제거합니다. 권한을 부여받은 유저만 사용 가능합니다.")
                                    .addOption(OptionType.STRING, "닉네임", "제거할 닉네임을 입력해주세요.", true),
                            Commands.slash("링크", "제공되는 링크들입니다. 웹페이지에서 확인가능합니다.")
                    ).queue();
    return jda;
    }
}
