package delivery_service.controller;

import delivery_service.ApiResponse;
import delivery_service.entity.Order;
import delivery_service.entity.User;
import delivery_service.service.OrderService;
import delivery_service.service.TokenService;
import delivery_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final TokenService tokenService;
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/bucket")
    public ApiResponse getCarts(HttpServletRequest request) {
        String token = tokenService.extractTokenFromCookies(request);
        String login = tokenService.extractLogin(token);
        User user = userService.findByLogin(login);
        HashMap<String, Object> carts = orderService.getCarts(user);
        return ApiResponse.success(carts);
    }
}
