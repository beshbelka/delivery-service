package delivery_service.service;

import delivery_service.entity.Medicine;
import delivery_service.exception.NoItemsException;
import delivery_service.repository.MedicineRepository;
import delivery_service.repository.ServicesInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineServices implements ServicesInterface {

    private final MedicineRepository medicineRepository;

    @Override
    public List<Medicine> getAll() {
        List<Medicine> medicines = medicineRepository.findAll();
        if (medicines.isEmpty()) {
            throw new NoItemsException();
        }
        return medicines;
    }

    @Override
    public String getType() {
        return "medicine";
    }
}
