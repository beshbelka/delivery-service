package delivery_service.exception;

public class IllegalSkuTypeException extends BaseException {
    private static final int DEFAULT_ERROR_CODE = 400;
    private static final String DEFAULT_MESSAGE = "Неверный артикул";

    public IllegalSkuTypeException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
