package delivery_service.exception;

public class OrderNotFoundException extends BaseException {
    private static final int DEFAULT_ERROR_CODE = 404;
    private static final String DEFAULT_MESSAGE = "Заказ не найден";

    public OrderNotFoundException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
