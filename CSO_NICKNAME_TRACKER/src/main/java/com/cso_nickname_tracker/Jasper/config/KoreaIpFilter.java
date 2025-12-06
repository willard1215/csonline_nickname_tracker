package com.cso_nickname_tracker.Jasper.config;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Configuration
public class KoreaIpFilter implements Filter {

    private DatabaseReader reader;

    public KoreaIpFilter() throws IOException {
        File db = new File("/opt/geoip/GeoLite2-Country.mmdb");
        this.reader = new DatabaseReader.Builder(db).build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String ip = getClientIp(req);

        if (!isKoreanIp(ip)) {
            res.setStatus(403);
            res.getWriter().write("Access blocked: KR only");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isKoreanIp(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            String countryIso = reader.country(address).getCountry().getIsoCode();

            return "KR".equalsIgnoreCase(countryIso);

        } catch (GeoIp2Exception | IOException e) {
            // GeoIP lookup 실패 시 차단 (안전 옵션)
            return false;
        }
    }

    private String getClientIp(HttpServletRequest req) {
        String xf = req.getHeader("X-Forwarded-For");
        if (xf != null && xf.length() != 0) {
            return xf.split(",")[0]; // Cloudflare 등 프록시 환경 대응
        }
        return req.getRemoteAddr();
    }
}

