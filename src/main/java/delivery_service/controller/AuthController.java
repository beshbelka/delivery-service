package delivery_service.controller;

import delivery_service.ApiResponse;
import delivery_service.DTO.request.LoginRequest;
import delivery_service.DTO.request.RegisterRequest;
import delivery_service.DTO.response.UserDataResponse;
import delivery_service.exception.BaseException;
import delivery_service.service.AuthService;
import delivery_service.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ApiResponse register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        String token = authService.register(request);
        tokenService.addTokenCookie(response, token);
        return ApiResponse.success();
    }

    @GetMapping("/profile")
    public UserDataResponse getUserData(HttpServletRequest request) {
        String token = tokenService.extractTokenFromCookies(request);
        String login = tokenService.extractLogin(token);
        return authService.getUserData(login);
    }

    @PostMapping("/login")
    public ApiResponse login(@Valid @RequestBody LoginRequest loginRequest,
                                             HttpServletResponse response) {
        String token = authService.login(loginRequest);
        tokenService.addTokenCookie(response,token);
        return ApiResponse.success();
    }
}
