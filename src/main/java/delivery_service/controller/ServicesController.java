package delivery_service.controller;

import delivery_service.ApiResponse;
import delivery_service.DTO.request.AddToBucketRequest;
import delivery_service.entity.BaseItem;
import delivery_service.enums.ITEM_TYPE;
import delivery_service.exception.IllegalSkuTypeException;
import delivery_service.repository.ServicesInterface;
import delivery_service.service.SkuTypeService;
import delivery_service.service.TokenService;
import delivery_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServicesController {

    private final List<ServicesInterface> services;
    private final TokenService tokenService;
    private final UserService userService;

    @GetMapping("/data/{type}")
    public ApiResponse getData(@PathVariable String type) {
        ServicesInterface servicesInterface = services.stream()
                .filter(s -> s.getType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(IllegalSkuTypeException::new);
        List<? extends BaseItem> items = servicesInterface.getAll();
        return ApiResponse.success(Map.of("items", items));
    }

    @PostMapping("/add")
    public ApiResponse addToBucket(@RequestBody AddToBucketRequest addToBucketRequest,
                                   HttpServletRequest request) {
        String sku = addToBucketRequest.sku();
        ITEM_TYPE type = SkuTypeService.getTypeOfSku(sku);
        String token = tokenService.extractTokenFromCookies(request);
        String login = tokenService.extractLogin(token);
        userService.addToBucket(login, sku, type);
        return ApiResponse.success();
    }
}
