package com.myspringboot.myspringbootgateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class AuthorityFilter implements Ordered,GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        List<String> list = queryParams.get("auth");
        if (list == null || list.size()==0){
            log.warn("auth failed!");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            DataBuffer data = exchange.getResponse().bufferFactory().wrap("<h1>auth failed! no auth param<h1>".getBytes());
            return exchange.getResponse().writeWith(Mono.just(data));
//            exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
