package delivery_service.controller;

import delivery_service.ApiResponse;
import delivery_service.entity.BaseItem;
import delivery_service.repository.ServicesInterface;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServicesController {

    private final List<ServicesInterface> services;

    @GetMapping("/data/{type}")
    public ApiResponse getData(@PathVariable String type) {
        ServicesInterface servicesInterface = services.stream()
                .filter(s -> s.getType().equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
        List<? extends BaseItem> items = servicesInterface.getAll();
        return ApiResponse.success(Map.of("items", items));
    }
}
