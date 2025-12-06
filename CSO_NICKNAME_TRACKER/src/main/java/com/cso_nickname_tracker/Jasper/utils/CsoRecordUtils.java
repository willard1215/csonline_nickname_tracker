package com.cso_nickname_tracker.Jasper.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsoRecordUtils {
    private final RestTemplate restTemplate = new RestTemplate();

    public String getSnFromNickname(String nickname) {
        try {

            String url = "https://statsapi-csonline.nexon.com/v1/api/common/nexonsn?nickname=" + nickname;
            log.warn("REQ → {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Host", "statsapi-csonline.nexon.com");
            headers.add("Origin", "https://stats.csonline.nexon.com");

            HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

            ResponseEntity<Map> entity =
                    restTemplate.exchange(url, HttpMethod.GET, httpEntity, Map.class);

            Map<String, Object> response = entity.getBody();
            log.warn("RES → {}", response);

            if (response == null) {
                throw new RuntimeException("API 응답이 비어있습니다.");
            }

            Integer resultCode = (Integer) response.get("result_code");
            if (resultCode == null || resultCode != 0) {
                throw new RuntimeException("닉네임 조회 실패: result_code = " + resultCode);
            }

            Map<String, Object> res = (Map<String, Object>) response.get("res");
            if (res == null) {
                throw new RuntimeException("API 응답에서 res 필드를 찾을 수 없습니다.");
            }

            String nexonsn = (String) res.get("nexonsn");
            if (nexonsn == null) {
                throw new RuntimeException("API 응답에서 nexonsn 값을 찾을 수 없습니다.");
            }

            return nexonsn;

        } catch (Exception e) {
            throw new RuntimeException("알 수 없는 에러: " + e.getMessage(), e);
        }
    }



    public String searchNicknameFromSn(String nexonsn) {
        try {
            String url = "https://statsapi-csonline.nexon.com/v1/api/common/nickname?nexonsn=" + nexonsn;

            // ===== 헤더 생성 =====
            HttpHeaders headers = new HttpHeaders();
            headers.add("Host", "statsapi-csonline.nexon.com");
            headers.add("Origin", "https://stats.csonline.nexon.com");



            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // ===== 요청 보내기 =====
            ResponseEntity<Map> responseEntity =
                    restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            Map<String, Object> response = responseEntity.getBody();
            if (response == null) {
                throw new RuntimeException("API 응답이 비어있습니다.");
            }

            Integer resultCode = (Integer) response.get("result_code");
            if (resultCode == null || resultCode != 0) {
                throw new RuntimeException("nexonsn 조회 실패: result_code = " + resultCode);
            }

            Map<String, Object> res = (Map<String, Object>) response.get("res");
            if (res == null) {
                throw new RuntimeException("API 응답에서 res 필드를 조회하는데 실패했습니다.");
            }

            String nickname = (String) res.get("nickname");
            if (nickname == null) {
                throw new RuntimeException("API 응답에서 nickname 을 조회하는데 실패했습니다.");
            }

            return nickname;

        } catch (Exception e) {
            throw new RuntimeException("알 수 없는 에러: " + e.getMessage(), e);
        }
    }

}
