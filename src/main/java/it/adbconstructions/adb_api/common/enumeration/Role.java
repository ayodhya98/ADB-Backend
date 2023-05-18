package it.adbconstructions.adb_api.common.enumeration;

import static it.adbconstructions.adb_api.common.constant.Authority.*;

public enum Role {
    ROLE_CONSUMER(CONSUMER_AUTHORITIES),
    ROLE_CONSULTANT(CONSULTANT_AUTHORITIES),
    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);

    private String[] authorities;

    Role(String... authorities){
        this.authorities = authorities;
    }

    public String[] getAuthorities(){
        return authorities;
    }
}
