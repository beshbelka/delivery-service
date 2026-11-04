package delivery_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import delivery_service.enums.ITEM_TYPE;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ITEM_TYPE itemType;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    @Min(1)
    private double price;

    @Column(nullable = false)
    private String name;

}
