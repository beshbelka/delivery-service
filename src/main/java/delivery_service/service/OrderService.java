package delivery_service.service;

import delivery_service.entity.BaseItem;
import delivery_service.entity.Order;
import delivery_service.entity.OrderItem;
import delivery_service.entity.User;
import delivery_service.enums.ITEM_TYPE;
import delivery_service.enums.ORDER_STATUS;
import delivery_service.exception.IllegalSkuTypeException;
import delivery_service.exception.ItemNotFoundException;
import delivery_service.repository.OrderRepository;
import delivery_service.repository.ServicesInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final List<ServicesInterface> services;

    public Order findCart(User user, ITEM_TYPE type) {
        return orderRepository.findByUserIdAndStatusAndType(user.getId(), ORDER_STATUS.CART, type)
                .orElseGet(() -> orderRepository.save(Order.cart(user, type)));
    }

    public void save(Order cart) {
        orderRepository.save(cart);
    }

    public HashMap<String, Object> getCarts(User user) {
        HashMap<String, Object> carts = new HashMap<>(4);
        List<Order> existingCarts = orderRepository.findByUserIdAndStatus(
                user.getId(),
                ORDER_STATUS.CART);
        for (Order cart : existingCarts) {
            carts.put(cart.getType().name().toLowerCase(), cart);
        }
        return carts;
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserAndStatusNot(user, ORDER_STATUS.CART);
    }

    public List<String> getCartSkus(User user) {
        HashMap<String, Object> carts = getCarts(user);
        List<String> skus = new ArrayList<>();
        for (Object cartobj : carts.values()) {
            Order cart = (Order) cartobj;
            if (cart != null && cart.getItems() != null) {
                for (OrderItem oi : cart.getItems()) {
                    skus.add(oi.getSku());
                }
            }
        }
        return skus;
    }

    public void addToCart(String login, String sku, ITEM_TYPE type, Integer count) {
        User user = userService.findByLogin(login);
        ServicesInterface servicesInterface = services.stream()
                .filter(s -> s.getType().equalsIgnoreCase(type.toString().toLowerCase()))
                .findFirst()
                .orElseThrow(IllegalSkuTypeException::new);
        BaseItem item = servicesInterface.findBySku(sku).orElseThrow(ItemNotFoundException::new);
        Order cart = findCart(user, type);
        Optional<OrderItem> existing = cart.getItems().stream()
                .filter(i -> i.getItemId().equals(item.getId()))
                .findFirst();
        if (existing.isPresent()) {
            OrderItem orderItem = existing.get();
            if (orderItem.getQuantity() + count < 1) {
                deleteFromCart(cart, sku);
            } else {
                orderItem.setQuantity(orderItem.getQuantity() + count);
            }
        } else {
            if (count < 1) return;
            OrderItem newItem = new OrderItem();
            newItem.setItemId(item.getId());
            newItem.setItemType(type);
            newItem.setQuantity(count);
            newItem.setPrice(item.getPrice());
            newItem.setName(item.getName());
            newItem.setSku(item.getSku());
            cart.addItem(newItem);
        }
        save(cart);
    }

    private void deleteFromCart(Order cart, String sku) {
        cart.getItems().stream()
                .filter(item -> item.getSku().equals(sku))
                .findFirst()
                .ifPresent(item -> cart.getItems().remove(item));
    }
}
