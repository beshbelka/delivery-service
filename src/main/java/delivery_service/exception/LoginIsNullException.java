package delivery_service.exception;

public class LoginIsNullException extends BaseException {

    private static final int DEFAULT_ERROR_CODE = 401;
    private static final String DEFAULT_MESSAGE = "Логин не найден";

    public LoginIsNullException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
