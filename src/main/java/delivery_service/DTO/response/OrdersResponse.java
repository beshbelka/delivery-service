package delivery_service.DTO.response;

import delivery_service.entity.Order;

import java.util.List;

public record OrdersResponse(
        long count,
        List<Order> orders
) {
}
