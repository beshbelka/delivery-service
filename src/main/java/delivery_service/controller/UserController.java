package delivery_service.controller;

import delivery_service.ApiResponse;
import delivery_service.DTO.request.ChangePasswordRequest;
import delivery_service.DTO.request.EditProfileRequest;
import delivery_service.DTO.response.UserDataResponse;
import delivery_service.entity.User;
import delivery_service.service.AuthService;
import delivery_service.service.BlacklistService;
import delivery_service.service.TokenService;
import delivery_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final TokenService tokenService;
    private final BlacklistService blacklistService;

    @GetMapping("")
    public ApiResponse<String> getUserData(Authentication authentication) {
        String login = authentication.getName();
        UserDataResponse response = userService.getUserData(login);
        Map<String, String> data = new HashMap<>(3);
        data.put("login", response.login());
        data.put("name", response.name());
        data.put("role", response.role());
        data.put("emoji", response.emoji());
        data.put("color", response.color());
        return ApiResponse.success(data);
    }

    @PostMapping("/changePassword")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
                                            Authentication authentication,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {
        String oldPass = changePasswordRequest.oldPass();
        String newPass1 = changePasswordRequest.newPass1();
        String login = authentication.getName();
        User user = authService.verifyPassword(login, oldPass);
        if (user != null) {
            authService.changePassword(user, newPass1);
            String token = tokenService.extractTokenFromCookies(request);
            blacklistService.add(token);
            tokenService.clearTokenCookie(response);
            token = tokenService.generateToken(login);
            tokenService.addTokenCookie(response, token);
            return ApiResponse.success();
        } else {
            return ApiResponse.error(400, "Старый пароль не совпадает");
        }
    }

    @PostMapping("/editProfile")
    public ApiResponse<Void> editProfile(@Valid @RequestBody EditProfileRequest editProfileRequest, Authentication authentication) {
        String login = authentication.getName();
        userService.editProfile(login, editProfileRequest);
        return ApiResponse.success();
    }
}
