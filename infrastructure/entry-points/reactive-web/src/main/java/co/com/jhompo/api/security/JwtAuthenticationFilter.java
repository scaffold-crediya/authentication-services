package co.com.jhompo.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import java.util.List;

import static co.com.jhompo.util.Messages.JWT.BEARER;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith(BEARER)) {
            String token = authHeader.substring(7);
            if (jwtProvider.validateToken(token)) {
                String email = jwtProvider.getEmailFromToken(token);

                //Recuperar roles desde el token
                List<String> roles = jwtProvider.getRolesFromToken(token);
                log.info("************Roles obtenidos del token: {}", roles);

                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> {
                            log.info("************Creando autoridad con valor: {}", role);
                            return new SimpleGrantedAuthority(role);
                        })
                        .toList();

                log.info("*************Authorities finales: {}", authorities);

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
            }
        }

        return chain.filter(exchange);
    }
}