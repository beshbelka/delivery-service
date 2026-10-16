package delivery_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final StringRedisTemplate redis;
    private final TokenService tokenService;

    public void add(String token) {
        String jti = tokenService.extractJti(token);
        Duration ttl = tokenService.getRemainingTtl(token);
        redis.opsForValue().set("blacklist:" + jti, "1", ttl);
    }

    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redis.hasKey("blacklist:" + jti));
    }
}
