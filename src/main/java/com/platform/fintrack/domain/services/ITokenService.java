package com.platform.fintrack.domain.services;

import com.platform.fintrack.domain.models.User;

import java.util.UUID;

public interface ITokenService {
    String generator(final User user);
    UUID validateToken(final String token);
}
