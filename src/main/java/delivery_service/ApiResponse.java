package delivery_service;

import lombok.Data;

import java.util.Map;

@Data
public class ApiResponse<T> {

    private final boolean success;
    private final int code;
    private final String message;
    private final Map<String, T> data;

    private ApiResponse (boolean success, int code, String message, Map<String, T> data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, 200, "Операция успешна", Map.of());
    }
    public static <T> ApiResponse<T> success(Map<String, T> data) {
        return new ApiResponse<>(true, 200, "Операция успешна", Map.copyOf(data));
    }

    public static <T> ApiResponse<T> error() {
        return new ApiResponse<>(false, 500, "Ошибка сервера", Map.of());
    }
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(false, code, message, Map.of());
    }
}
