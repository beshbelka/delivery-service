package delivery_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import delivery_service.enums.ITEM_TYPE;
import delivery_service.enums.ORDER_STATUS;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "order_parts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ITEM_TYPE type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ORDER_STATUS status;

    @OneToMany(mappedBy = "orderPart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> items = new HashSet<>();

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrderPart(this);
    }
}
