package delivery_service.service;

import delivery_service.ApiResponse;
import delivery_service.DTO.request.LoginRequest;
import delivery_service.DTO.request.RegisterRequest;
import delivery_service.entity.User;
import delivery_service.enums.USER_ROLE;
import delivery_service.exception.BaseException;
import delivery_service.exception.LoginFailedException;
import delivery_service.exception.UserNotFoundException;
import delivery_service.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional
    public ApiResponse register(RegisterRequest request, HttpServletResponse response) {
        try {
            if (userRepository.existsByLogin(request.login())) {
                return ApiResponse.error(409, "Пользователь с таким логином уже существует");
            }
            User user = new User(
                    request.login(),
                    request.name(),
                    passwordEncoder.encode(request.password1())
            );
            userRepository.save(user);
            String token = tokenService.generateToken(request.login(), USER_ROLE.USER);
            tokenService.addTokenCookie(response, token);
            return ApiResponse.success();
        } catch (BaseException e) {
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error();
        }
    }

    public ApiResponse getUserData(String login) {
        try {
            User user = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
            Map<String, Object> data = new HashMap<>(3);
            data.put("name", user.getName());
            data.put("login", user.getLogin());
            data.put("role", user.getFormattedRole());
            return ApiResponse.success(data);
        } catch (BaseException e) {
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error();
        }
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByLogin(request.login()).orElseThrow(LoginFailedException::new);
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new LoginFailedException();
        }
        return tokenService.generateToken(request.login(), user.getRole());
    }
}
