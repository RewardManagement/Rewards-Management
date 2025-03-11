package com.reward.security;

import org.springframework.security.core.userdetails.UserDetails;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import io.jsonwebtoken.io.Decoders;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}") 
    private String secretKeyBase64;

    @Value("${jwt.expiration}") 
    private long expirationMs;

    private SecretKey secretKey;

    public String generateToken(String email){

        Map<String,Object> claims = new HashMap<> ();

        return Jwts.builder()
            .claims()
            .add(claims)
            .subject(email)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expirationMs)) 
            .and()
            .signWith(secretKey)
            .compact();

    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyBase64);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
}
