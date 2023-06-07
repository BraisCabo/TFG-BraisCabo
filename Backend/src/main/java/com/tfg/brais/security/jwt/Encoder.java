package com.tfg.brais.security.jwt;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class Encoder {
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
}
