package delivery_service.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{
    private final int errorCode;

    public BaseException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
