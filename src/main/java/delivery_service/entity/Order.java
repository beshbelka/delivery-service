package delivery_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import delivery_service.enums.ORDER_STATUS;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ORDER_STATUS status;

    @Column(nullable = false)
    private double totalPrice = 0.0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderPart> parts = new HashSet<>();

    public void addPart(OrderPart part) {
        parts.add(part);
        part.setOrder(this);
    }

}
