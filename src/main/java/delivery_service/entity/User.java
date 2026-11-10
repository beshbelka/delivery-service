package delivery_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import delivery_service.enums.USER_ROLE;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

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

    @Enumerated(EnumType.STRING)
    private USER_ROLE role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Order> orders = new HashSet<>();

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

    @Column(nullable = false)
    private String emoji;
    @Column(nullable = false)
    private String color;

    public User (String login, String name, String password) {
        this.login = login;
        this.name = name;
        this.password = password;
        this.role = USER_ROLE.USER;
        this.emoji = "🐙";
        this.color = "#b8dcc6";
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
