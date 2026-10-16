package delivery_service.DTO.request;

import delivery_service.validation.PasswordAware;
import delivery_service.validation.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@PasswordMatches
public record RegisterRequest(
        @NotBlank @Length(min = 2, message = "Имя слишком короткое") String name,
        @NotBlank String login,
        @NotBlank String password1,
        @NotBlank String password2
) implements PasswordAware {
    @Override public String getPassword1() { return password1; }
    @Override public String getPassword2() { return password2; }
}
