package delivery_service.security;

public class Path {

    public static final String[] PUBLIC = {
            "/css/**",
            "/js/**",
            "/html/**",
            "/error",
            "/favicon.ico"
    };

    public static final String[] PUBLIC_GET = {
            "/",
            "/login",
            "/register"
    };

    public static final String[] PUBLIC_POST = {

    };
}
