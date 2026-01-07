package com.cso_nickname_tracker.Jasper.service;

import com.cso_nickname_tracker.Jasper.config.ProxyRouteProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProxyService {
    private final ProxyRouteProperties routes;
    private final RestClient restClient = RestClient.builder().build();

    private static final Set<String> HOP_BY_HOP_HEADERS = new HashSet<>();
    static {
        // RFC 7230 hop-by-hop
        HOP_BY_HOP_HEADERS.add("connection");
        HOP_BY_HOP_HEADERS.add("keep-alive");
        HOP_BY_HOP_HEADERS.add("proxy-authenticate");
        HOP_BY_HOP_HEADERS.add("proxy-authorization");
        HOP_BY_HOP_HEADERS.add("te");
        HOP_BY_HOP_HEADERS.add("trailers");
        HOP_BY_HOP_HEADERS.add("transfer-encoding");
        HOP_BY_HOP_HEADERS.add("upgrade");
        // 보통 프록시가 재계산/제거해야 안전한 헤더들
        HOP_BY_HOP_HEADERS.add("host");
        HOP_BY_HOP_HEADERS.add("content-length");
        HOP_BY_HOP_HEADERS.add("expect");
    }

    public ResponseEntity<byte[]> forward(
            Region region,
            Purpose purpose,
            String path,
            HttpMethod method,
            String query,
            String clientIp,
            MultiValueMap<String, String> incomingHeaders,
            byte[] body
    ) {
        String baseUrl = routes.resolve(region, purpose);

        URI target = UriComponentsBuilder.fromUriString(baseUrl)
                .path(path)
                .query(query)
                .build(true)
                .toUri();

        HttpHeaders outboundHeaders = sanitizeAndCopyHeaders(incomingHeaders, clientIp);

        RestClient.RequestBodySpec spec =
                restClient.method(method).uri(target).headers(h -> h.addAll(outboundHeaders));

        // GET/HEAD 등은 body를 붙이지 않는 것이 안전합니다.
        boolean canHaveBody =
                HttpMethod.POST.equals(method)
                        || HttpMethod.PUT.equals(method)
                        || HttpMethod.PATCH.equals(method)
                        || HttpMethod.DELETE.equals(method);

        if (canHaveBody && body != null && body.length > 0) {
            spec = spec.body(body);
        }

        return spec.exchange((req, res) -> {
            byte[] bytes;
            try {
                bytes = res.getBody().readAllBytes();
            } catch (IOException e) {
                throw new IllegalStateException("프록시 응답 바디 읽기 실패", e);
            }

            HttpHeaders responseHeaders = new HttpHeaders();
            res.getHeaders().forEach((k, v) -> {
                if (k == null) return;
                if (isHopByHop(k)) return;
                responseHeaders.put(k, v);
            });

            return ResponseEntity.status(res.getStatusCode())
                    .headers(responseHeaders)
                    .body(bytes);
        });
    }

    private HttpHeaders sanitizeAndCopyHeaders(MultiValueMap<String, String> incoming, String clientIp) {
        HttpHeaders out = new HttpHeaders();

        incoming.forEach((k, v) -> {
            if (k == null) return;
            if (isHopByHop(k)) return;
            out.put(k, v);
        });

        // X-Forwarded-For: 기존 값이 있으면 뒤에 clientIp를 붙입니다.
        if (clientIp != null && !clientIp.isBlank()) {
            String existing = out.getFirst("X-Forwarded-For");
            if (existing == null || existing.isBlank()) {
                out.set("X-Forwarded-For", clientIp);
            } else if (!existing.contains(clientIp)) {
                out.set("X-Forwarded-For", existing + ", " + clientIp);
            }
        }

        return out;
    }

    private boolean isHopByHop(String headerName) {
        return HOP_BY_HOP_HEADERS.contains(headerName.toLowerCase(Locale.ROOT));
    }
}


