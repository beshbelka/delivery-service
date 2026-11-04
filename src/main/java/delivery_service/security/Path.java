package delivery_service.security;

public class Path {

    public static final String[] ANONYMOUS = {
            "/auth/login",
            "/auth/register"
    };

    public static final String[] PERMIT_ALL = {
            "/services/data/**"
    };
}