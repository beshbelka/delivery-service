package delivery_service.service;

import delivery_service.DTO.request.EditProfileRequest;
import delivery_service.DTO.response.UserDataResponse;
import delivery_service.entity.Order;
import delivery_service.entity.OrderPart;
import delivery_service.entity.User;
import delivery_service.enums.ORDER_STATUS;
import delivery_service.exception.UserNotFoundException;
import delivery_service.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.ColumnTransformers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
    }

    public UserDataResponse getUserData(String login) {
        User user = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
        return new UserDataResponse(
                user.getName(),
                user.getLogin(),
                user.getFormattedRole(),
                user.getEmoji(),
                user.getColor()
        );
    }

    public void editProfile(String login, @Valid EditProfileRequest editProfileRequest) {
        User user = findByLogin(login);
        user.setName(editProfileRequest.name());
        user.setEmoji(editProfileRequest.emoji());
        user.setColor(editProfileRequest.color());
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(String login) {
        User user = findByLogin(login);
        user.setActive(false);
        user.setPassword("");
        user.setLogin("deleted_" + user.getId() + "_" + user.getLogin());
        for (Order orders : user.getOrders()) {
            for (OrderPart orderPart : orders.getParts()) {
                orderPart.setStatus(ORDER_STATUS.CANCELLED);
            }
            orders.setStatus(ORDER_STATUS.CANCELLED);
        }
        userRepository.save(user);
    }
}
