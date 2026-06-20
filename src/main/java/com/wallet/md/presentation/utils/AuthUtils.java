package com.wallet.md.presentation.utils;

import org.springframework.security.core.userdetails.UserDetails;

// presentation/util/AuthUtils.java
public class AuthUtils {

    public static Long extractUserId(UserDetails userDetails) {
        return Long.parseLong(userDetails.getUsername());
    }
}