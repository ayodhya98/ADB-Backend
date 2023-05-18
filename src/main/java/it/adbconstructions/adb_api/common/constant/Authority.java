package it.adbconstructions.adb_api.common.constant;

public class Authority {
    public static final String[] CONSUMER_AUTHORITIES = {"consumer:read"};
    public static final String[] CONSULTANT_AUTHORITIES = {"user:read"};
    public static final String[] SUPER_ADMIN_AUTHORITIES = {"user:read", "user:create", "user:update", "user:delete"};
}
