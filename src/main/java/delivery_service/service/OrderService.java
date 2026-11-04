package delivery_service.service;

import delivery_service.entity.Order;
import delivery_service.entity.User;
import delivery_service.enums.ITEM_TYPE;
import delivery_service.enums.ORDER_STATUS;
import delivery_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order findCart(User user, ITEM_TYPE type) {
        return orderRepository.findByUserIdAndStatusAndType(user.getId(), ORDER_STATUS.CART, type)
                .orElseGet(() -> orderRepository.save(Order.cart(user, type)));
    }

    public void save(Order cart) {
        orderRepository.save(cart);
    }

    public HashMap<String, Object> getCarts(User user) {
        HashMap<String, Object> carts = new HashMap<>(4);
        List<Order> existingVarts = orderRepository.findByUserIdAndStatus(
                user.getId(),
                ORDER_STATUS.CART);
        for (Order cart : existingVarts) {
            carts.put(cart.getType().name().toLowerCase(), cart);
        }
        return carts;
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserAndStatusNot(user, ORDER_STATUS.CART);
    }
}
