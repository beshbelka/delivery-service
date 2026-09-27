package delivery_service.exception;

public class UserNotFoundException extends BaseException {

    private static final String DEFAULT_MESSAGE = "Пользователь не найден";
    private static final int DEFAULT_ERROR_CODE = 401;

    public UserNotFoundException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}