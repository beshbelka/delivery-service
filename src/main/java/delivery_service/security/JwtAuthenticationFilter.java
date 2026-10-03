package delivery_service.security;

import delivery_service.exception.UserNotFoundException;
import delivery_service.service.BlacklistService;
import delivery_service.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final BlacklistService blacklistService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = tokenService.extractTokenFromCookies(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            if (tokenService.isTokenValid(token)) {
                String jti = tokenService.extractJti(token);
                if (blacklistService.isBlacklisted(jti)) {
                    filterChain.doFilter(request, response);
                }
                final String login = tokenService.extractLogin(token);
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    try {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(login);
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
                        log.warn("user not found");
                    }
                } else {
                    throw new UserNotFoundException();
                }
            } else {
                log.warn("token invalid");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        filterChain.doFilter(request, response);
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
