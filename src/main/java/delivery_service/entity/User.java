package delivery_service.entity;

import delivery_service.enums.USER_ROLE;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(unique = true, nullable = false)
    private String login;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private USER_ROLE role;

    @NullMarked
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @NullMarked
    @Override
    public String getUsername() {
        return login;
    }

    public User (String login, String name, String password) {
        this.login = login;
        this.name = name;
        this.password = password;
        this.role = USER_ROLE.USER;
        this.orders = new ArrayList<>();
    }

    public String getFormattedRole() {
        switch (this.role) {
            case USER_ROLE.USER -> {
                return "Пользователь";
            }
            case USER_ROLE.ADMIN -> {
                return "Администратор";
            }
            case USER_ROLE.MANAGER -> {
                return "Менеджер";
            }
            default -> {
                return "Неизвестно";
            }
        }
    }
}
