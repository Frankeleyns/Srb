package com.frankeleyn.srb.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * @author Frankeleyn
 * @date 2022/2/19 16:12
 */
@Component
public class MyFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取 cookie 和 token
        MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();
        if (Objects.nonNull(cookies) && cookies.size() > 0) {
            HttpCookie cookieToken = cookies.get("token").get(0);
        }

        HttpHeaders headers = exchange.getRequest().getHeaders();
        if (Objects.nonNull(headers) && !headers.isEmpty() ) {
            List<String> strings = headers.get("token");
            if (Objects.nonNull(strings) && !strings.isEmpty() && strings.size() > 0) {
                String token = strings.get(0);
            }
        }

        return chain.filter(exchange);
    }
}
