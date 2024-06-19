package com.vodacom.falcon.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.vodacom.falcon.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${jwt.key}")
    private String key;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(key);

            return JWT.create()
                    .withIssuer("falcon")
                    .withSubject(user.getUsername())
                    .withExpiresAt(getExpirationDate())
                    .sign(algorithm);
        } catch (IllegalArgumentException | JWTCreationException e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(key);

            return JWT.require(algorithm)
                    .withIssuer("falcon")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token Not recognized");
        }
    }


    private Instant getExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
