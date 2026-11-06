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

    public User findByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
    }

}
