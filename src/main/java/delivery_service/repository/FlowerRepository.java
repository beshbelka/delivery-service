package delivery_service.repository;

import delivery_service.entity.BaseItem;
import delivery_service.entity.Flower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlowerRepository extends JpaRepository<Flower, Long> {
    Optional<Flower> findBySku(String sku);
}
