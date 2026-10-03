package delivery_service.security;

public class Path {

    public static final String[] PUBLIC = {
            "/error",
            "/",
            "/login",
            "/register",
            "/login.html",
            "/register.html",
            "/index.html",
    };

    public static final String[] PUBLIC_GET = {
            "/css/**",
            "/js/**",
            "/img/**",
            "/fonts/**",
            "/favicon.ico",
            "/.well-knownn/**",
    };

    public static final String[] PUBLIC_POST = {
            "/auth/**"
    };
}