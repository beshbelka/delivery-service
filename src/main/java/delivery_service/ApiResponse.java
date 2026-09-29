package delivery_service;


import java.util.HashMap;

public class ApiResponse {

    int code;
    String message;
    HashMap<String, String> data;

    private ApiResponse(int code, String message, HashMap<String, String> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ApiResponse success() {
        return new ApiResponse(200, "ok", new HashMap<>());
    }
    public ApiResponse success(String message) {
        return new ApiResponse(200, message, new HashMap<>());
    }
    public ApiResponse success(HashMap<String, String> data) {
        return new ApiResponse(200, "Операция успешна", data);
    }
    public ApiResponse success(String message, HashMap<String, String> data) {
        return new ApiResponse(200, message, data);
    }

    public ApiResponse error() {
        return new ApiResponse(500, "Ошибка сервера", new HashMap<>());
    }
    public ApiResponse error(int code) {
        return new ApiResponse(code, "Ошибка сервера", new HashMap<>());
    }
    public ApiResponse error(String message) {
        return new ApiResponse(500, message, new HashMap<>());
    }
    public ApiResponse error(HashMap<String, String> data) {
        return new ApiResponse(500, "Ошибка сервера", data);
    }
    public ApiResponse error(int code, String message) {
        return new ApiResponse(code, message, new HashMap<>());
    }
    public ApiResponse error(int code, HashMap<String, String> data) {
        return new ApiResponse(code, "Ошибка сервера", data);
    }
    public ApiResponse error(String message, HashMap<String, String> data) {
        return new ApiResponse(500, message, data);
    }
    public ApiResponse error(int code, String message, HashMap<String, String> data) {
        return new ApiResponse(code, message, data);
    }
}
