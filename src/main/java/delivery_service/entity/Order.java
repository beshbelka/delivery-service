package delivery_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import delivery_service.enums.ITEM_TYPE;
import delivery_service.enums.ORDER_STATUS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Enumerated(EnumType.STRING)
    private ORDER_STATUS status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<OrderItem> items = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ITEM_TYPE type;

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public static Order cart(User user, ITEM_TYPE type) {
        Order cart = new Order();
        cart.setUser(user);
        cart.setStatus(ORDER_STATUS.CART);
        cart.setType(type);
        return cart;
    }
}
