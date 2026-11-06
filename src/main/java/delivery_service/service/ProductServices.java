package delivery_service.service;

import delivery_service.entity.BaseItem;
import delivery_service.entity.Product;
import delivery_service.exception.NoItemsException;
import delivery_service.repository.ProductRepository;
import delivery_service.repository.ServicesInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServices implements ServicesInterface {

    private final ProductRepository productRepository;

    @Override
    public List<Product> getAll() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new NoItemsException();
        }
        return products;
    }

    @Override
    public String getType() {
        return "product";
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return productRepository.findBysku(sku);
    }
}
