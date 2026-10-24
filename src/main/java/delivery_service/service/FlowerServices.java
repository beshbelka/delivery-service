package delivery_service.service;

import delivery_service.entity.Flower;
import delivery_service.exception.NoItemsException;
import delivery_service.repository.FlowerRepository;
import delivery_service.repository.ServicesInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
