package delivery_service.service;

import delivery_service.entity.BaseItem;
import delivery_service.entity.Flower;
import delivery_service.exception.NoItemsException;
import delivery_service.repository.FlowerRepository;
import delivery_service.repository.ServicesInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FlowerServices implements ServicesInterface {

    private final FlowerRepository flowerRepository;

    @Override
    public List<Flower> getAll() {
        List<Flower> flowers = flowerRepository.findAll();
        if (flowers.isEmpty()) {
            throw new NoItemsException();
        }
        return flowers;
    }

    @Override
    public String getType() {
        return "flower";
    }

    @Override
    public Flower findBySku(String sku) {
        return flowerRepository.findBySku(sku).orElseThrow(NoItemsException::new);
    }
}
