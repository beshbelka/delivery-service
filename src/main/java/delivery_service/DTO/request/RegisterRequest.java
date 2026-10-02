package delivery_service.DTO.request;

public record RegisterRequest(
        String name,
        String login,
        String password1,
        String password2
) {
}
