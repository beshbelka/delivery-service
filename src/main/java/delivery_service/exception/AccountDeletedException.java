package delivery_service.exception;

public class AccountDeletedException extends BaseException {
    private static final int DEFAULT_ERROR_CODE = 409;
    private static final String DEFAULT_MESSAGE = "Аккаунт удалён";

    public AccountDeletedException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
