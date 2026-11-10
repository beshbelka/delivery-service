package delivery_service.controller;

import delivery_service.ApiResponse;
import delivery_service.DTO.response.OrdersResponse;
import delivery_service.entity.Order;
import delivery_service.entity.User;
import delivery_service.service.AuthService;
import delivery_service.service.OrderService;
import delivery_service.service.TokenService;
import delivery_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final TokenService tokenService;
    private final UserService userService;
    private final OrderService orderService;
    private final AuthService authService;

    @GetMapping("/cart")
    public ApiResponse<Order> getCarts(HttpServletRequest request) {
        String token = tokenService.extractTokenFromCookies(request);
        String login = tokenService.extractLogin(token);
        User user = userService.findByLogin(login);
        Order cart = orderService.getCart(user);
        return ApiResponse.success(Map.of("cart", cart));
    }

    @PostMapping("/make")
    public ApiResponse<Void> makeAnOrder(HttpServletRequest request) {
        User user = userService.findByLogin(tokenService.extractLogin(tokenService.extractTokenFromCookies(request)));
        orderService.makeAnOrder(user);
        return ApiResponse.success();
    }

    @GetMapping("/orders")
    public ApiResponse<Object> getOrders(Authentication authentication) {
        String login = authentication.getName();
        OrdersResponse response = authService.getOrders(login);
        Map<String, Object> data = new HashMap<>(2);
        data.put("count", response.count());
        data.put("orders", response.orders());
        return ApiResponse.success(data);
    }
}
