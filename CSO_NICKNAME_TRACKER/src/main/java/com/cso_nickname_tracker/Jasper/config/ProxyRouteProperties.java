package com.cso_nickname_tracker.Jasper.config;

import com.cso_nickname_tracker.Jasper.service.Purpose;
import com.cso_nickname_tracker.Jasper.service.Region;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.EnumMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "proxy")
public class ProxyRouteProperties {
    /**
     * application.yml 예시:
     * proxy:
     *   base-urls:
     *     KR:
     *       QUERY: "http://1.2.3.4:3000"
     *       CDN: "http://example.com"
     */
    private Map<Region, Map<Purpose, String>> baseUrls = new EnumMap<>(Region.class);

    /**
     * (호환용) 과거 설정키(proxy.routes)를 썼던 경우를 위한 setter.
     * 현재 권장 키는 proxy.base-urls 입니다.
     */
    @Deprecated
    public void setRoutes(Map<Region, Map<Purpose, String>> routes) {
        this.baseUrls = routes;
    }

    public String resolve(Region region, Purpose purpose) {
        var byRegion = baseUrls.get(region);
        if (byRegion == null) {
            throw new IllegalStateException("해당 국가에 대한 ip가 존재하지 않습니다");
        }

        var base = byRegion.get(purpose);
        if (base == null || base.isBlank()) {
            throw new IllegalStateException(region + " 지역과 " + purpose + "에 해당하는 ep를 찾지 못했습니다");
        }

        return base;
    }
}


