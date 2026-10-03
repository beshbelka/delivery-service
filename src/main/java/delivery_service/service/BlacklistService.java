package delivery_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final StringRedisTemplate redis;

    public void add(String jti, Duration ttl) {
        redis.opsForValue().set("blacklist:" + jti, "1", ttl);
    }

    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redis.hasKey("blacklist:" + jti));
    }
}
