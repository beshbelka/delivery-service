package delivery_service.repository;

import delivery_service.entity.Order;
import delivery_service.entity.User;
import delivery_service.enums.ITEM_TYPE;
import delivery_service.enums.ORDER_STATUS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByUserIdAndStatusAndType(Long id, ORDER_STATUS orderStatus, ITEM_TYPE type);

    List<Order> findByUserIdAndStatus(Long id, ORDER_STATUS orderStatus);

    List<Order> findByUserAndStatusNot(User user, ORDER_STATUS orderStatus);
}
