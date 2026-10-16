package delivery_service.DTO.request;

import delivery_service.validation.PasswordAware;
import delivery_service.validation.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@PasswordMatches
public record ChangePasswordRequest(
        @NotBlank @Length(min = 6, message = "Длина пароля не менее 6 символов") String oldPass,
        @NotBlank @Length(min = 6, message = "Длина пароля не менее 6 символов") String newPass1,
        @NotBlank @Length(min = 6, message = "Длина пароля не менее 6 символов") String newPass2
) implements PasswordAware {
    @Override public String getPassword1() { return newPass1; }
    @Override public String getPassword2() { return newPass2; }
}
