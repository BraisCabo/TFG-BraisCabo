package com.tfg.brais.security.jwt;

import java.security.Key;

import javax.crypto.KeyGenerator;

import org.springframework.stereotype.Service;

import lombok.Getter;

@Service
@Getter
public class SecretKey {
    
    private Key privateKey;

    public SecretKey(){
        try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("HMACSHA256");
			keyGenerator.init(256); // Tamaño de la clave en bits
			privateKey = keyGenerator.generateKey();
		} catch (Exception e) {
			
		}
    }
}
