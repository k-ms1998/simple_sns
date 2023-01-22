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

    private static Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
