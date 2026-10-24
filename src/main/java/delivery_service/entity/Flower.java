package delivery_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "flower")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flower extends BaseItem {

    @Column(nullable = false)
    private String shop;

    @Min(1)
    private int count;
}
