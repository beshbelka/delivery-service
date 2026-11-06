package delivery_service.DTO.request;

import jakarta.validation.constraints.NotBlank;

public record AddToBucketRequest(
        @NotBlank String sku,
        @NotBlank Integer count
) {
    public AddToBucketRequest {
        if (count == null) {
            count = 1;
        }
    }
}
