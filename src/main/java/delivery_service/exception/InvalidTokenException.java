package delivery_service.exception;

public class InvalidTokenException extends BaseException {

    private static final int DEFAULT_ERROR_CODE = 401;
    private static final String DEFAULT_MESSAGE = "Невалидный токен";

    public InvalidTokenException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
