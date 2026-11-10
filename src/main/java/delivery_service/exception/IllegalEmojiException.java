package delivery_service.exception;

public class IllegalEmojiException extends BaseException {
    private static final int DEFAULT_ERROR_CODE = 400;
    private static final String DEFAULT_MESSAGE = "Недопустимый смайлик";

    public IllegalEmojiException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
