package delivery_service.service;

import delivery_service.enums.ITEM_TYPE;
import delivery_service.exception.IllegalSkuTypeException;
import org.springframework.stereotype.Service;

@Service
public class SkuTypeService {

    public static ITEM_TYPE getTypeOfSku(String sku) {
        switch (sku.substring(0,4)) {
            case "FOOD" -> { return ITEM_TYPE.FOOD; }
            case "PROD" -> { return ITEM_TYPE.PRODUCT; }
            case "MED-" -> { return ITEM_TYPE.MEDICINE; }
            case "FLWR" -> { return ITEM_TYPE.FLOWER; }
        }
        throw new IllegalSkuTypeException();
    }
}
