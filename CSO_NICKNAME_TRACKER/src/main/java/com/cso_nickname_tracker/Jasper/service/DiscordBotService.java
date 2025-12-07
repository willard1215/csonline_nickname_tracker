package com.cso_nickname_tracker.Jasper.service;

import com.cso_nickname_tracker.Jasper.Dto.UserDto;
import com.cso_nickname_tracker.Jasper.utils.CsoRecordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordBotService extends ListenerAdapter {
    private static final int PAGE_SIZE = 5;

    private final UserService userService;
    private final LuaService luaService;

    @Value("${app.web-base-url}")
    private String webBaseUrl;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        switch (event.getName()) {
            case "조회" -> handleSearch(event);
            case "추가" -> handleAdd(event);
            case "제거" -> handleRemove(event);
            case "링크" -> handleLink(event);
            case "다운로드" -> handleDownload(event);
        }
    }

    private void handleSearch(SlashCommandInteractionEvent event) {
        int pageIndex = 0;
        String requesterId = event.getUser().getId();

        MessageCreateData message = renderUserListPage(pageIndex, requesterId);
        if (message == null) {
            event.reply("등록된 유저가 없습니다.")
                 .setEphemeral(true)
                 .queue();
            return;
        }

        event.reply(message).queue();
    }

    private void handleAdd(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        String nickname = event.getOption("닉네임", OptionMapping::getAsString);
        String actorDiscordId = event.getUser().getId();
        try {
            UserDto user = userService.createUser(nickname, actorDiscordId);
            String nexonsn = user.getUserKey();
            event.getHook().sendMessage("유저 추가 완료: " + nickname + ", sn: " + nexonsn).queue();
        } catch (Exception e) {
            event.getHook().sendMessage("유저 추가 실패").queue();
        }
    }



    private MessageCreateData renderUserListPage(int pageIndex, String requesterId) {
        Page<UserDto> page = userService.getUsers(
                PageRequest.of(Math.max(pageIndex, 0), PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"))
        );

        if (page.isEmpty()) {
            return null;
        }

        int totalPages = page.getTotalPages();
        long totalUsers = page.getTotalElements();

        // 범위를 벗어난 pageIndex 보정
        if (pageIndex >= totalPages) {
            pageIndex = Math.max(totalPages - 1, 0);
            page = userService.getUsers(
                    PageRequest.of(pageIndex, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"))
            );
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("비매너 유저 목록");
        eb.setColor(0xFF5555);
        eb.setTimestamp(Instant.now());

        StringBuilder desc = new StringBuilder();
        int startNo = pageIndex * PAGE_SIZE + 1;

        List<UserDto> content = page.getContent();
        for (int i = 0; i < content.size(); i++) {
            UserDto u = content.get(i);

            int no = startNo + i;
            String sn = u.getUserKey();
            String link = "https://stats.csonline.nexon.com/record/match/list?player=" + sn;

            desc.append("`")
                .append(no)
                .append("` ")
                .append("[`").append(u.getNickname()).append("`](").append(link).append(")")
                .append("\n");
        }

        eb.setDescription(desc.toString());
        eb.setFooter(String.format("페이지 %d / %d · 총 %d명",
                pageIndex + 1,
                Math.max(totalPages, 1),
                totalUsers
        ));

        // 버튼 구성
        List<Button> buttons = new ArrayList<>();

        // customId: userlist:page:<pageIndex>:<ownerId>
        if (pageIndex > 0) {
            buttons.add(Button.secondary(
                    "userlist:page:" + (pageIndex - 1) + ":" + requesterId,
                    "〈 이전"
            ));
        }
        if (pageIndex + 1 < totalPages) {
            buttons.add(Button.secondary(
                    "userlist:page:" + (pageIndex + 1) + ":" + requesterId,
                    "다음 〉"
            ));
        }

        MessageCreateBuilder mb = new MessageCreateBuilder()
                .setEmbeds(eb.build());

        if (!buttons.isEmpty()) {
            mb.setComponents(ActionRow.of(buttons));
        }

        return mb.build();
    }


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String id = event.getComponentId(); // 예: userlist:page:2:1234567890

        if (!id.startsWith("userlist:page:")) {
            return;
        }

        String[] parts = id.split(":");
        if (parts.length != 4) {
            return;
        }

        int pageIndex = Integer.parseInt(parts[2]);
        String ownerId = parts[3];

        // 목록을 요청한 유저만 페이지 이동 가능하게 제한
        if (!event.getUser().getId().equals(ownerId)) {
            event.reply("이 목록은 다른 사용자의 요청으로 생성되었습니다.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        MessageCreateData newPage = renderUserListPage(pageIndex, ownerId);
        if (newPage == null) {
            event.reply("더 이상 표시할 유저가 없습니다.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        event.editMessage(MessageEditData.fromCreateData(newPage)).queue();
    }

    public void handleRemove (SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        String nickname = event.getOption("닉네임", OptionMapping::getAsString);
        String actorDiscordId = event.getUser().getId();
        try {
            CsoRecordUtils utils = new CsoRecordUtils();
            String sn = utils.getSnFromNickname(nickname);
            userService.deleteUser(sn, actorDiscordId);
            event.getHook().sendMessage("목록에서 제거했습니다.").queue();
        } catch (Exception e) {
            event.getHook().sendMessage("유저 제거 실패").queue();
        }
    }


    private void handleLink(SlashCommandInteractionEvent event) {
        // 기본 페이지 링크들 (bulk API는 노출 X)
        String userListUrl = webBaseUrl + "/getUser?page=0&size=20";
        String logsUrl     = webBaseUrl + "/logs?page=0&size=20";

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("CSO Nickname Tracker 링크")
                .setDescription("웹 UI에서 닉네임 목록과 수정 로그를 확인할 수 있습니다.")
                .addField("유저 목록 페이지",
                        String.format("[열기](%s)", userListUrl),
                        false)
                .addField("수정 로그 페이지",
                        String.format("[열기](%s)", logsUrl),
                        false)
                .setColor(0xFF5555);

        event.replyEmbeds(eb.build())
                .setEphemeral(true) // 요청자에게만 보이게 하고 싶지 않다면 제거
                .queue();
    }

    public void handleDownload(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        byte[] luaBytes = luaService.buildBanListLuaBytes();

        FileUpload file = FileUpload.fromData(luaBytes, "BanList.lua");
        event.getHook().sendFiles(file).queue();
    }
}
