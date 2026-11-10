package delivery_service.DTO.request;

import jakarta.validation.constraints.NotBlank;

public record AddToCartRequest(
        @NotBlank String sku,
        Integer count
) {
    public AddToCartRequest {
        if (count == null) {
            count = 1;
        }
    }
}
