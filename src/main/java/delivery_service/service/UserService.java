package delivery_service.service;

import delivery_service.entity.BaseItem;
import delivery_service.entity.Order;
import delivery_service.entity.OrderItem;
import delivery_service.entity.User;
import delivery_service.enums.ITEM_TYPE;
import delivery_service.exception.IllegalSkuTypeException;
import delivery_service.exception.UserNotFoundException;
import delivery_service.repository.ServicesInterface;
import delivery_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final List<ServicesInterface> services;
    private final OrderService orderService;

    public User findByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
    }

    public void addToBucket(String login, String sku, ITEM_TYPE type) {
        User user = findByLogin(login);
        System.out.println("TYPE: " + type.toString().toLowerCase());
        ServicesInterface servicesInterface = services.stream()
                .filter(s -> s.getType().equalsIgnoreCase(type.toString().toLowerCase()))
                .findFirst()
                .orElseThrow(IllegalSkuTypeException::new);
        BaseItem item = servicesInterface.findBySku(sku);
        Order cart = orderService.findCart(user, type);
        Optional<OrderItem> existing = cart.getItems().stream()
                .filter(i -> i.getItemId().equals(item.getId()))
                .findFirst();
        if (existing.isPresent()) {
            OrderItem orderItem = existing.get();
            orderItem.setQuantity(orderItem.getQuantity() + 1);
        } else {
            OrderItem newItem = new OrderItem();
            newItem.setItemId(item.getId());
            newItem.setItemType(type);
            newItem.setQuantity(1);
            newItem.setPrice(item.getPrice());
            newItem.setName(item.getName());
            cart.addItem(newItem);
        }
        orderService.save(cart);
    }



}
