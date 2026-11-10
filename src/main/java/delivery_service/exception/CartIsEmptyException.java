package delivery_service.exception;

public class CartIsEmptyException extends BaseException{
    private static final int DEFAULT_ERROR_CODE = 400;
    private static final String DEFAULT_MESSAGE = "Корзина пуста";

    public CartIsEmptyException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
