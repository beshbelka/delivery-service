package delivery_service.service;

import delivery_service.entity.*;
import delivery_service.enums.ITEM_TYPE;
import delivery_service.enums.ORDER_STATUS;
import delivery_service.exception.CartIsEmptyException;
import delivery_service.exception.IllegalSkuTypeException;
import delivery_service.exception.ItemNotFoundException;
import delivery_service.repository.OrderRepository;
import delivery_service.repository.ServicesInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final List<ServicesInterface> services;

    public Order findCart(User user) {
        return orderRepository.findByUserIdAndStatus(user.getId(), ORDER_STATUS.CART)
                .orElseGet(() -> {
                    Order cart = new Order();
                    cart.setUser(user);
                    cart.setStatus(ORDER_STATUS.CART);
                    return orderRepository.save(cart);
                });
    }

    public void save(Order order) {
        orderRepository.save(order);
    }

    @Transactional
    public void addToCart(String login, String sku, ITEM_TYPE type, Integer count) {
        User user = userService.findByLogin(login);
        ServicesInterface servicesInterface = services.stream()
                .filter(s -> s.getType().equalsIgnoreCase(type.toString().toLowerCase()))
                .findFirst()
                .orElseThrow(IllegalSkuTypeException::new);
        BaseItem item = servicesInterface.findBySku(sku).orElseThrow(ItemNotFoundException::new);
        Order cart = findCart(user);
        OrderPart part = cart.getParts().stream()
                .filter(p -> p.getType() == type)
                .findFirst()
                .orElseGet(() -> {
                    OrderPart newPart = new OrderPart();
                    newPart.setType(type);
                    newPart.setStatus(ORDER_STATUS.CART);
                    cart.addPart(newPart);
                    return newPart;
                });
        Optional<OrderItem> existing = part.getItems().stream()
                .filter(i -> i.getItemId().equals(item.getId()))
                .findFirst();

        if (existing.isPresent()) {
            OrderItem orderItem = existing.get();
            if (orderItem.getQuantity() + count < 1) {
                part.getItems().remove(orderItem);
                if (part.getItems().isEmpty()) {
                    cart.getParts().remove(part);
                }
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
            part.addItem(newItem);
        }
        recalculateTotalPrice(cart);
        save(cart);
    }

    private void recalculateTotalPrice(Order order) {
        double total = 0;
        for (OrderPart part : order.getParts()) {
            for (OrderItem item : part.getItems()) {
                total += item.getPrice() * item.getQuantity();
            }
        }
        order.setTotalPrice(total);
    }

    public Order getCart(User user) {
        return orderRepository.findByUserIdAndStatus(user.getId(), ORDER_STATUS.CART).orElse(new Order());
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserIdAndStatusNot(user.getId(), ORDER_STATUS.CART);
    }

    @Transactional
    public void makeAnOrder(User user) {
        Order cart = orderRepository.findByUserIdAndStatus(user.getId(), ORDER_STATUS.CART)
                .orElseThrow(CartIsEmptyException::new);
        cart.setStatus(ORDER_STATUS.CREATED);
        for (OrderPart part : cart.getParts()) {
            part.setStatus(ORDER_STATUS.CREATED);
        }
        save(cart);
    }

    public List<String> getCartSkus(User user) {
        Order cart = orderRepository.findByUserIdAndStatus(user.getId(), ORDER_STATUS.CART)
                .orElse(null);
        if (cart == null || cart.getParts() == null) {
            return new ArrayList<>();
        }
        List<String> skus = new ArrayList<>();
        for (OrderPart part : cart.getParts()) {
            if (part.getItems() != null) {
                for (OrderItem item : part.getItems()) {
                    skus.add(item.getSku());
                }
            }
        }
        return skus;
    }
}