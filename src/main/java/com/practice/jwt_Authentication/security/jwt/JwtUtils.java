package com.practice.jwt_Authentication.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;                           //for create signature

    @Value("${jwt.jwtExpiration}")
    private int jwtExpirationMs;                        //for define Validate period

    public String generateJwtToken(Authentication authentication){              //Generate JWT token
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();                                          //token convert String type
    }

    public Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }


    public boolean validateJwtToken(String authToken){
        try{
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        }catch (MalformedJwtException e){
            logger.error("Invalid JWT Token: {}", e.getMessage());
        }catch (ExpiredJwtException e){
            logger.error("JWT Token is Expired : {}",e.getMessage());
        }catch (UnsupportedJwtException e){
            logger.error("Unsupported JWT : {}", e.getMessage());
        }catch (IllegalArgumentException e){
            logger.error("JWT payload is empty : {}",e.getMessage());
        }
        return false;
    }


    public String getUsernameFromJwtToken(String authToken){
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJwt(authToken).getBody().getSubject();
    }
}
