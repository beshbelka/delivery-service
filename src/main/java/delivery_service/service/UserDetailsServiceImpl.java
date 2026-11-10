package delivery_service.service;

import delivery_service.entity.User;
import delivery_service.exception.AccountDeletedException;
import delivery_service.exception.UserNotFoundException;
import delivery_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String login) {
        User user = userRepository.findByLogin(login).orElseThrow(UserNotFoundException::new);
        if (!user.isActive()) throw new AccountDeletedException();
        return user;
    }
}
