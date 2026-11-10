package delivery_service.service;

import delivery_service.DTO.request.EditProfileRequest;
import delivery_service.DTO.response.UserDataResponse;
import delivery_service.entity.User;
import delivery_service.exception.UserNotFoundException;
import delivery_service.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
