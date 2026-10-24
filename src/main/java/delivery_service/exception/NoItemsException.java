package delivery_service.exception;

public class NoItemsException extends BaseException {

    private static final int DEFAULT_ERROR_CODE = 404;
    private static final String DEFAULT_MESSAGE = "Нет доступных товаров или услуг";

    public NoItemsException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
