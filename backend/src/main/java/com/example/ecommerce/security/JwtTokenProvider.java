package com.example.ecommerce.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return JWT.create()
                .withSubject(userPrincipal.getId().toString())
                .withClaim("username", userPrincipal.getUsername())
                .withClaim("role", userPrincipal.getRole())
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    public String generateToken(Long userId, String username, Integer role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return JWT.create()
                .withSubject(userId.toString())
                .withClaim("username", username)
                .withClaim("role", role)
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    public Long getUserIdFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return Long.parseLong(decodedJWT.getSubject());
    }

    public String getUsernameFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim("username").asString();
    }

    public Integer getRoleFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim("role").asInt();
    }

    public boolean validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtSecret)).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}
