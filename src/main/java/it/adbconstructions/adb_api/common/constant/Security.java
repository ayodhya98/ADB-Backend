package it.adbconstructions.adb_api.common.constant;

// initializing all the Http Security headers
public class Security {
    public static final long EXPIRATION_TIME = 432_000_000; //5 days to expiration in mil seconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String ADB_CONSTRUCTION_LTD = "ADB Constructions, LTD";
    public static final String ADB_CONSTRUCTION_ADMINISTRATION = "Construction Management System";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "Login required to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You are not permitted to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {
            "/api/v1/consumer/login/**","/api/v1/consumer/register/**","/api/v1/consumer/password/**",
            "/api/v1/user/login/**","/api/v1/user/password/**", "/ws/**", "/messages/**", "/projects/list/**","/projects/find/**"
    };
}
