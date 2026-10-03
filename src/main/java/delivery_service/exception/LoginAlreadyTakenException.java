package delivery_service.exception;

public class LoginAlreadyTakenException extends BaseException {
    private static final int DEFAULT_ERROR_CODE = 409;
    private static final String DEFAULT_MESSAGE = "Пользователь с таким логином уже существует";

    public LoginAlreadyTakenException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
