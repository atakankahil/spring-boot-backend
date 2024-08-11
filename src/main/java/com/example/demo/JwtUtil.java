package com.example.demo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    private final String SECRET_KEY = "secret";
    private final String REFRESH_SECRET_KEY = "refresh_secret";

    public String extractUsername(String token, String secretKey) {
        return extractClaim(token, Claims::getSubject, secretKey);
    }

    public Date extractExpiration(String token, String secretKey) {
        return extractClaim(token, Claims::getExpiration, secretKey);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secretKey) {
        final Claims claims = extractAllClaims(token, secretKey);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, String secretKey) {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token, String secretKey) {
        return extractExpiration(token, secretKey).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails.getUsername(), SECRET_KEY, 1000 * 60 * 15); // 15 minutes
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails.getUsername(), REFRESH_SECRET_KEY, 1000 * 60 * 60 * 24 * 7); // 7 days
    }

    private String createToken(Map<String, Object> claims, String subject, String secretKey, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, DatatypeConverter.parseBase64Binary(secretKey))
                .compact();
    }

    public boolean validateRefreshToken(String token) {
        return !isTokenExpired(token, REFRESH_SECRET_KEY);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token, SECRET_KEY);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, SECRET_KEY));
    }

    public String getSecretKey() {
        return SECRET_KEY;
    }

    public String getRefreshSecretKey() {
        return REFRESH_SECRET_KEY;
    }
}
