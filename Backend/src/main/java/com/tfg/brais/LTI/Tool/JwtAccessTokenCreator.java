package com.tfg.brais.LTI.Tool;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtAccessTokenCreator {

    private static final long EXPIRATION_TIME_MS = 3600;

    private static long getCurrentDate() {
        return new Date().getTime() / 1000;
    }

    public String generateJWT(String privateKeyStr, String subject, String audience) {
        try {
            PrivateKey privateKey = getPrivateKey(privateKeyStr);
            String jwt = createJwtToken(privateKey, subject, audience);
            return jwt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PrivateKey getPrivateKey(String privateKeyStr) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    private static String createJwtToken(PrivateKey privateKey, String subject, String audience) {

        long issuedAt = getCurrentDate();
        long expiration = issuedAt + EXPIRATION_TIME_MS;
        return Jwts.builder()
                .setSubject(subject)
                .setAudience(audience)
                .setIssuedAt(new Date(issuedAt * 1000))
                .setExpiration(new Date(expiration * 1000))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
}
