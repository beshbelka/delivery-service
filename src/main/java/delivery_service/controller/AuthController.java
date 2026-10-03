package delivery_service.controller;

import delivery_service.ApiResponse;
import delivery_service.DTO.request.LoginRequest;
import delivery_service.DTO.request.RegisterRequest;
import delivery_service.DTO.response.UserDataResponse;
import delivery_service.service.AuthService;
import delivery_service.service.BlacklistService;
import delivery_service.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final BlacklistService blacklistService;

    @PostMapping("/register")
    public ApiResponse register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        String token = authService.register(request);
        tokenService.addTokenCookie(response, token);
        return ApiResponse.success();
    }

    @GetMapping("/profile")
    public ApiResponse getUserData(HttpServletRequest request) {
        String token = tokenService.extractTokenFromCookies(request);
        String login = tokenService.extractLogin(token);
        UserDataResponse response = authService.getUserData(login);
        Map<String, Object> data = new HashMap<>();
        data.put("login", response.login());
        data.put("name", response.name());
        data.put("role", response.role());
        return ApiResponse.success(data);
    }

    @PostMapping("/login")
    public ApiResponse login(@Valid @RequestBody LoginRequest loginRequest,
                                             HttpServletResponse response) {
        String token = authService.login(loginRequest);
        tokenService.addTokenCookie(response,token);
        return ApiResponse.success();
    }

    @PostMapping("/logout")
    public ApiResponse logout(HttpServletRequest request, HttpServletResponse response) {
        String token = tokenService.extractTokenFromCookies(request);
        if (token != null) {
            String jti = tokenService.extractJti(token);
            Duration ttl = tokenService.getRemainingTtl(token);
            blacklistService.add(jti, ttl);
        }
        tokenService.clearTokenCookie(response);
        return ApiResponse.success();
    }
}
