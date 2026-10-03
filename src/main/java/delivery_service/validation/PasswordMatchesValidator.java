package delivery_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext ctx) {
        if (obj instanceof PasswordAware p) {
            return Objects.equals(p.getPassword1(), p.getPassword2());
        }
        return true;
    }
}
