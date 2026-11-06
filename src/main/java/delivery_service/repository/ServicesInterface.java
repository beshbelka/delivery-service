package delivery_service.repository;

import delivery_service.entity.BaseItem;

import java.util.List;
import java.util.Optional;

public interface ServicesInterface {

    List<? extends BaseItem> getAll();

    String getType();

    Optional<? extends BaseItem> findBySku(String sku);
}
