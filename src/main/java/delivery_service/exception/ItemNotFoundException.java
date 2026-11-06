package delivery_service.exception;

public class ItemNotFoundException extends BaseException {
    private static final int DEFAULT_ERROR_CODE = 400;
    private static final String DEFAULT_MESSAGE = "Товар не найден";

    public ItemNotFoundException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
