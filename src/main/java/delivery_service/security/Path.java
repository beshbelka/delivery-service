package delivery_service.security;

public class Path {

    public static final String[] PUBLIC = {
            "/error",
            "/",
            "/login",
            "/register",
            "/login.html",
            "/register.html",
            "/index.html"
    };

    public static final String[] PUBLIC_GET = {
    };

    public static final String[] PUBLIC_POST = {
            "/auth/**"
    };
}