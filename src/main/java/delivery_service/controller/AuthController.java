package delivery_service.controller;

import delivery_service.ApiResponse;
import delivery_service.DTO.request.ChangePasswordRequest;
import delivery_service.DTO.request.EditProfileRequest;
import delivery_service.DTO.request.LoginRequest;
import delivery_service.DTO.request.RegisterRequest;
import delivery_service.DTO.response.OrdersResponse;
import delivery_service.DTO.response.UserDataResponse;
import delivery_service.entity.User;
import delivery_service.service.AuthService;
import delivery_service.service.BlacklistService;
import delivery_service.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        String token = authService.register(request);
        tokenService.addTokenCookie(response, token);
        return ApiResponse.success();
    }

    @GetMapping("/profile")
    public ApiResponse<Void> getAuth(Authentication authentication) {
        if (authentication.isAuthenticated()) {
            return ApiResponse.success();
        } else {
            return ApiResponse.error();
        }
    }

    @PostMapping("/login")
    public ApiResponse<Void> login(@Valid @RequestBody LoginRequest loginRequest,
                                             HttpServletResponse response) {
        String token = authService.login(loginRequest);
        tokenService.addTokenCookie(response,token);
        return ApiResponse.success();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = tokenService.extractTokenFromCookies(request);
        if (token != null) {
            blacklistService.add(token);
        }
        tokenService.clearTokenCookie(response);
        return ApiResponse.success();
    }
}
