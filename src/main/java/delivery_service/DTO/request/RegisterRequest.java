package delivery_service.DTO.request;

import delivery_service.validation.PasswordAware;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record RegisterRequest(
        @NotBlank @Length(min = 2) String name,
        @NotBlank String login,
        @NotBlank String password1,
        @NotBlank String password2
) implements PasswordAware {
    @Override public String getPassword1() { return password1; }
    @Override public String getPassword2() { return password2; }
}
