package com.cso_nickname_tracker.Jasper.events;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TestListener extends ListenerAdapter {
    @Override
    public void onReady(ReadyEvent e) {
        // 채널명
        Guild g = e.getJDA().getGuildById("");

        if (g != null) {
            log.warn("GUILD FOUND!!");
            log.warn("------------");
            log.warn("{}",g.getName());
            log.warn("------------");
            g.updateCommands()
                    .addCommands(
                            Commands.slash("조회", "dev: 닉네임 목록을 조회합니다."),
                            Commands.slash("추가", "dev: 닉네임 목록에 추가합니다.")
                                            .addOption(OptionType.STRING, "닉네임", "추가할 닉네임을 입력해주세요.", true),
                            Commands.slash("제거", "dev: 닉네임 목록에서 제거합니다. 권한을 부여받은 유저만 사용 가능합니다.")
                                    .addOption(OptionType.STRING, "닉네임", "제거할 닉네임을 입력해주세요.", true),
                            Commands.slash("링크", "dev: 제공되는 링크들입니다. 웹페이지에서 확인가능합니다.")
                    ).queue();
        } else {
            log.warn("GUILD NOT FOUND");
        }

    }
}
