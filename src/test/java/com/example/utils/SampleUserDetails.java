package com.example.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

import static com.example.utils.Constants.ENCODED_PASSWORD;
import static com.example.utils.Constants.GENERAL_USER_ID;

public class SampleUserDetails {
    public static UserDetails createUserDetails() {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_GENERAL");
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(grantedAuthority);

        return new User(GENERAL_USER_ID, ENCODED_PASSWORD, authorities);
    }
}
