package delivery_service.service;

import delivery_service.ApiResponse;
import delivery_service.DTO.request.LoginRequest;
import delivery_service.DTO.request.RegisterRequest;
import delivery_service.DTO.response.UserDataResponse;
import delivery_service.entity.User;
import delivery_service.enums.USER_ROLE;
import delivery_service.exception.BaseException;
import delivery_service.exception.LoginAlreadyTakenException;
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
    public String register(RegisterRequest request) {
        if (userRepository.existsByLogin(request.login())) {
            throw new LoginAlreadyTakenException();
        }
        User user = new User(
                request.login(),
                request.name(),
                passwordEncoder.encode(request.password1())
        );
        userRepository.save(user);
        return tokenService.generateToken(request.login(), USER_ROLE.USER);
    }

    public UserDataResponse getUserData(String login) {
        User user = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
        return new UserDataResponse(
                user.getName(),
                user.getLogin(),
                user.getFormattedRole()
        );
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByLogin(request.login()).orElseThrow(LoginFailedException::new);
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new LoginFailedException();
        }
        return tokenService.generateToken(request.login(), user.getRole());
    }
}
