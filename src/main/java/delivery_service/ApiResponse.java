package delivery_service;

import lombok.Data;

import java.util.Map;

@Data
public class ApiResponse {

    private final boolean success;
    private final int code;
    private final String message;
    private final Map<String, Object> data;

    private ApiResponse (boolean success, int code, String message, Map<String, Object> data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ApiResponse success() {
        return new ApiResponse(true, 200, "Операция успешна", Map.of());
    }
    public static ApiResponse success(String message) {
        return new ApiResponse(true, 200, message, Map.of());
    }
    public static ApiResponse success(Map<String, Object> data) {
        return new ApiResponse(true, 200, "Операция успешна", Map.copyOf(data));
    }
    public static ApiResponse success(String message, Map<String, Object> data) {
        return new ApiResponse(true, 200, message, Map.copyOf(data));
    }

    public static ApiResponse error() {
        return new ApiResponse(false, 500, "Ошибка сервера", Map.of());
    }
    public static ApiResponse error(int code, String message) {
        return new ApiResponse(false, code, message, Map.of());
    }
    public static ApiResponse error(int code, String message, Map<String, Object> data) {
        return new ApiResponse(false, code, message, Map.copyOf(data));
    }
}
