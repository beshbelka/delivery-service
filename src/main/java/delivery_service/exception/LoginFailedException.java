package delivery_service.exception;

public class LoginFailedException extends BaseException {

    public static final int DEFAULT_ERROR_CODE = 401;
    public static final String DEFAULT_MESSAGE = "Ошибка аутентификации";

    public LoginFailedException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
