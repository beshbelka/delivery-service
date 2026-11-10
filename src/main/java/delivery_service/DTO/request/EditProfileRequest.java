package delivery_service.DTO.request;

import delivery_service.DTO.ProfileConstants;
import delivery_service.exception.IllegalColorException;
import delivery_service.exception.IllegalEmojiException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditProfileRequest(
        @NotBlank(message = "Имя не может быть пустым")
        @Size(min = 2, max = 50, message = "Длина имени от 2 до 50 символов")
        String name,

        @NotBlank String emoji,
        @NotBlank String color
) {
        public EditProfileRequest {
                if (!ProfileConstants.ALLOWED_EMOJIS.contains(emoji)) {
                        throw new IllegalEmojiException();
                }
                if (!ProfileConstants.ALLOWED_COLORS.contains(color)) {
                        throw new IllegalColorException();
                }
        }
}
