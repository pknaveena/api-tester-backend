package com.apitester.api_tester_backend.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    
    private final long expiration = 3600000;
    private final SecretKey secretKey;

      public JwtService(
            @Value("${security.jwt.secret}") String secret) {

        this.secretKey =
                Keys.hmacShaKeyFor(
                        secret.getBytes(StandardCharsets.UTF_8)
                );
    }
    public String generateToken(UserDetails userDetails){

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);
        return  Jwts.builder()
                    .subject(userDetails.getUsername())
                    .issuedAt(now)
                    .expiration(expirationDate)
                    .signWith(secretKey)
                    .compact();
    }

    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

     public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {

        Date expirationDate =
                Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getExpiration();

        return expirationDate.before(new Date());
    }
    

}
