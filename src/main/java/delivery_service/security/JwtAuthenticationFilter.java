package delivery_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
/*
        String accessToken = jwtService.extractAccessTokenFromCookies(request);
        String refreshToken = jwtService.extractRefreshTokenFromCookies(request);

        if (accessToken == null) {
            log.error("access token is null (JwtAuthenticationFilter)");
            if (refreshToken != null) {
                refreshTokenService.refresh(refreshToken, response, request);
            }
            filterChain.doFilter(request, response);
            return;
        }

        if (blacklistService.isBlackListed(accessToken)) {
            log.info("token blacklisted");
            jwtService.clearAccessTokenCookie(response);
            jwtService.clearRefreshTokenCookie(response);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtService.isTokenValid(accessToken)) {
                final String email = jwtService.extractEmail(accessToken);
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    try {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } catch (UserNotFoundException e) {
                        log.warn("JwtAuthenticationFilter: user not found, {}", e.getMessage());
                        throw new UserNotFoundException();
                    }
                } else {
                    throw new UserNotFoundException();
                }
            } else if (jwtService.isRefreshTokenValid(refreshToken)) {
                refreshTokenService.refresh(refreshToken, response, request);
                log.info("Authentication set for user: {}",
                        SecurityContextHolder.getContext().getAuthentication() != null
                                ? SecurityContextHolder.getContext().getAuthentication().getName()
                                : "null");
            } else {
                log.warn("JwtAuthenticationFilter: token invalid");
            }
        } catch (TokenExpiredException e) {
            log.error("JwtAuthenticationFilter: Токен истек, ошибка: {}", e.getMessage());
            if (refreshToken != null && !blacklistService.isBlackListed(refreshToken)
                    && jwtService.isRefreshTokenValid(refreshToken)) {
                refreshTokenService.refresh(refreshToken, response, request);
                String email = jwtService.extractEmail(refreshToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
*/
    filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (method.equals("GET")) {
            for (String publicPath : Path.PUBLIC_GET) {
                if (publicPath.endsWith("/**")) {
                    String prefix = publicPath.replace("/**", "");
                    if (path.startsWith(prefix)) return true;
                } else if (path.equals(publicPath)) return true;
            }
        }

        if (method.equals("POST")) {
            for (String publicPath : Path.PUBLIC_POST) {
                if (publicPath.endsWith("/**")) {
                    String prefix = publicPath.replace("/**", "");
                    if (path.startsWith(prefix)) return true;
                } else if (path.equals(publicPath)) return true;
            }
        }

        for (String publicPath : Path.PUBLIC) {
            if (publicPath.endsWith("/**")) {
                String prefix = publicPath.replace("/**", "");
                if (path.startsWith(prefix)) return true;
            } else if (path.equals(publicPath)) return true;
        }
        return false;
    }
}
