package delivery_service.DTO;

public record RegisterRequest(
        String name,
        String login,
        String password1,
        String password2
) {
}
