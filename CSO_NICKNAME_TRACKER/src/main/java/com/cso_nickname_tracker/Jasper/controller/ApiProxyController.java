package com.cso_nickname_tracker.Jasper.controller;

import com.cso_nickname_tracker.Jasper.service.ProxyService;
import com.cso_nickname_tracker.Jasper.service.Purpose;
import com.cso_nickname_tracker.Jasper.service.Region;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/proxy")
@RequiredArgsConstructor
public class ApiProxyController {
    private final ProxyService proxyService;

    @RequestMapping(
            value = "/{region}/{purpose}/**",
            method = {
                    RequestMethod.GET,
                    RequestMethod.POST,
                    RequestMethod.PUT,
                    RequestMethod.PATCH,
                    RequestMethod.DELETE,
                    RequestMethod.OPTIONS,
                    RequestMethod.HEAD
            }
    )
    public ResponseEntity<byte[]> proxy(
            @PathVariable String region,
            @PathVariable String purpose,
            HttpMethod method,
            HttpServletRequest request,
            @RequestHeader MultiValueMap<String, String> headers,
            @RequestBody(required = false) byte[] body
    ) {
        Region r = Region.from(region);
        Purpose p = Purpose.valueOf(purpose.trim().toUpperCase());

        String uri = request.getRequestURI();
        String prefix = "/api/proxy/" + region + "/" + purpose;
        String downstreamPath = uri.length() > prefix.length() ? uri.substring(prefix.length()) : "/";

        if (downstreamPath.isBlank()) downstreamPath = "/";

        return proxyService.forward(
                r,
                p,
                downstreamPath,
                method,
                request.getQueryString(),
                request.getRemoteAddr(),
                headers,
                body
        );
    }
}


