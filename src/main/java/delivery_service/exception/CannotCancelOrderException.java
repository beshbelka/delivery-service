package delivery_service.exception;

public class CannotCancelOrderException extends BaseException {
    public static final int DEFAULT_ERROR_CODE = 409;
    public static final String DEFAULT_MESSAGE = "Невозможно отменить заказ";

    public CannotCancelOrderException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
