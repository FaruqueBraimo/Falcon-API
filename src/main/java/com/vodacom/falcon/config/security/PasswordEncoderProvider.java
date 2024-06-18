package com.vodacom.falcon.config.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderProvider {
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
