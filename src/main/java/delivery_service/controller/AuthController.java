package delivery_service.controller;

import delivery_service.ApiResponse;
import delivery_service.DTO.RegisterRequest;
import delivery_service.exception.BaseException;
import delivery_service.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register (@RequestBody RegisterRequest request, HttpServletResponse response) {
        try {
            if (request.name() == null) return ResponseEntity.status(400).body(ApiResponse.error(400, "Имя не может быть пустым"));
            if (request.name().length() < 2) return ResponseEntity.status(400).body(ApiResponse.error(400, "Длина имени должна ыть не меньше 2 символов"));
            if (request.login() == null) return ResponseEntity.status(400).body(ApiResponse.error(400, "Логин не может быть пустым"));
            if (request.password1() == null || request.password2() == null) return ResponseEntity.status(400).body(ApiResponse.error(400, "Пароль не может быть пустым"));
            if (!request.password1().equals(request.password2())) return ResponseEntity.status(400).body(ApiResponse.error(400, "Пароли не совпадают"));
            ApiResponse apiResponse = authService.register(request, response);
            if (apiResponse.isSuccess()) {
                return ResponseEntity
                        .ok(apiResponse);
            } else {
                return ResponseEntity
                        .status(apiResponse.getCode())
                        .body(apiResponse);
            }
        } catch (BaseException e) {
            return ResponseEntity
                    .status(e.getErrorCode())
                    .body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error());
        }
    }
}
