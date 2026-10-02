package delivery_service.exception;

public class TokenExpiredException extends BaseException {

    private static final int DEFAULT_ERROR_CODE = 401;
    private static final String DEFAULT_MESSAGE = "Истёк срок действия токена";

    public TokenExpiredException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
