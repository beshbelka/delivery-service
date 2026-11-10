package delivery_service.repository;

import delivery_service.entity.Order;
import delivery_service.entity.OrderPart;
import delivery_service.entity.User;
import delivery_service.enums.ITEM_TYPE;
import delivery_service.enums.ORDER_STATUS;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"parts", "parts.items"})
    Optional<Order> findByUserIdAndStatus(Long id, ORDER_STATUS orderStatus);

    @EntityGraph(attributePaths = {"parts", "parts.items"})
    List<Order> findByUserIdAndStatusNot(Long userId, ORDER_STATUS orderStatus);
}
