package delivery_service.service;

import delivery_service.entity.Food;
import delivery_service.exception.NoItemsException;
import delivery_service.repository.FoodRepository;
import delivery_service.repository.ServicesInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FoodServices implements ServicesInterface {

    private final FoodRepository foodRepository;

    @Override
    public List<Food> getAll() {
        List<Food> foods = foodRepository.findAll();
        if (foods.isEmpty()) {
            throw new NoItemsException();
        }
        return foods;
    }

    @Override
    public String getType() {
        return "food";
    }

    @Override
    public Optional<Food> findBySku(String sku) {
        return foodRepository.findBySku(sku);
    }
}
