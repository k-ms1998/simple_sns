package com.project.sns.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtTokenUtils {

    public static String generateToken(String username, String secretKLey, Long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("username", username);
        Date date = new Date(System.currentTimeMillis());
        Date expireDate = new Date(System.currentTimeMillis() + expireTime);
        Key key = getKey(secretKLey);


        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String fetchUsername(String token, String key) {
        return extractClaims(token, key).get("username", String.class);
    }

    public static boolean isExpired(String token, String key) {
        Claims claims = extractClaims(token, key);
        Date expiredDate = claims.getExpiration();

        return expiredDate.before(new Date());
    }

    private static Claims extractClaims(String token, String key) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey(key))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private static Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
