package delivery_service.repository;

import delivery_service.entity.BaseItem;

import java.util.List;

public interface ServicesInterface {

    List<? extends BaseItem> getAll();

    String getType();

}
