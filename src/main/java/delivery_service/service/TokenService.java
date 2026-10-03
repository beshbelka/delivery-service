package delivery_service.service;

import delivery_service.enums.USER_ROLE;
import delivery_service.exception.InvalidTokenException;
import delivery_service.exception.LoginIsNullException;
import delivery_service.exception.TokenExpiredException;
import delivery_service.exception.TokenISNullException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.io.Decoders;

import javax.crypto.SecretKey;
import javax.xml.crypto.Data;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class TokenService {

    @Value("${secret}")
    private String secretKey;

    @Value("${expiration}")
    private int tokenExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String login, USER_ROLE role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .claims(claims)
                .subject(login)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public void addTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(tokenExpiration / 1000);
        response.addCookie(cookie);
    }

    public String extractTokenFromCookies(@NonNull HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie i : cookies) {
            if ("token".equals(i.getName())) {
                return i.getValue();
            }
        }
        return null;
    }

    public boolean isTokenValid(String token) {
        try {
            String login = extractLogin(token);
            return !isTokenExpired(token) && login != null;
        } catch (Exception e) {
            log.warn("isTokenValid failed: {}", e.getClass().getSimpleName(), e);
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String extractLogin(String token){
        if (token == null || token.isEmpty()) {
            throw new TokenISNullException();
        }
        String login = extractAllClaims(token).getSubject();
        if (login == null || login.isEmpty()) {
            throw new InvalidTokenException();
        }
        return login;
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (JwtException e) {
            throw new InvalidTokenException();
        } catch (IllegalArgumentException e) {
            throw new TokenISNullException();
        }
    }

    public String extractJti(String token) {
        return extractAllClaims(token).getId();
    }

    public Duration getRemainingTtl(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        long millis = expiration.getTime() - System.currentTimeMillis();
        return Duration.ofMillis(Math.max(millis, 0));
    }

    public void clearTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
