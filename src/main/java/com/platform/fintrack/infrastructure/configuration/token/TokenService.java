package com.platform.fintrack.infrastructure.configuration.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.platform.fintrack.domain.models.User;
import com.platform.fintrack.domain.services.ITokenService;
import com.platform.fintrack.infrastructure.exceptions.InvalidDataException;
import com.platform.fintrack.infrastructure.exceptions.InvalidTokenException;
import com.platform.fintrack.infrastructure.exceptions.TokenGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class TokenService implements ITokenService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private String expiration;

    @Override
    public String generator(final User user) {
        if(isNull(secretKey) || isNull(expiration) || isNull(user)) {
            throw new InvalidDataException("Invalid input: secretKey, expiration, or user is null");
        }

        try {
            return JWT.create()
                    .withIssuer("auth")
                    .withSubject(user.getId().toString())
                    .withExpiresAt(new Date(new Date().getTime() + Long.parseLong(expiration)))
                    .sign(Algorithm.HMAC256(secretKey));
        } catch (JWTCreationException e) {
            log.error("Error generating token for user {}", user.getId());
            throw new TokenGenerationException("Error generating JWT token ", e);
        }
    }

    @Override
    public UUID validateToken(String token) {
        try {
            return UUID.fromString(
                    JWT.require(Algorithm.HMAC256(secretKey))
                            .withIssuer("auth")
                            .build()
                            .verify(token)
                            .getSubject()
            );
        } catch (JWTVerificationException e) {
            log.error("Invalid or expired token {}", token);
            throw new InvalidTokenException(String.format("Invalid or expired token %s.", token));
        }
    }
}
