package com.reward.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

import com.reward.exception.UnauthorizedException;
import com.reward.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import io.jsonwebtoken.io.Decoders;

import java.util.UUID;
import java.util.Set;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}") 
    private String secretKeyBase64;

    @Value("${jwt.expiration}") 
    private long expirationMs;

    private SecretKey secretKey;

    private final Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet(); 

    private final UserRepository userRepository;

    public JwtUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("Token has expired");
        } catch (MalformedJwtException e) {
            throw new UnauthorizedException("Invalid token format");
        } catch (UnsupportedJwtException e) {
            throw new UnauthorizedException("Token type not supported");
        } catch (io.jsonwebtoken.security.SignatureException e) {  
            throw new UnauthorizedException("Token signature is invalid");
        } catch (Exception e) {
            throw new UnauthorizedException("Token validation failed");
        }
    }
    
    

    public boolean validateToken(String token, UserDetails userDetails) {
        if (isTokenInvalidated(token)) {
            throw new UnauthorizedException("Token is invalid or expired");
        }

        final String userName = extractUserName(token);
        if (!userName.equals(userDetails.getUsername()) || isTokenExpired(token)) {
            throw new UnauthorizedException("Invalid or expired token");
        }
        return true;
    }

    public void invalidateToken(String token) {
        invalidatedTokens.add(token);  // ✅ Add token to blacklist
    }

    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);  // ✅ Check if token is blacklisted
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractTokenFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() instanceof String token) {
            return token;
        }
        throw new UnauthorizedException("Token not found in security context");
    }
    

    
    public UUID extractUserIdFromToken() {
        // ✅ Automatically extract token from SecurityContext
        String token = extractTokenFromSecurityContext();
    
        String email = extractUserName(token); // subject = email
        return userRepository.findByEmailAndIsDeletedFalse(email)
            .orElseThrow(() -> new UnauthorizedException("Invalid or unauthorized user"))
            .getId();
    }
    
}
