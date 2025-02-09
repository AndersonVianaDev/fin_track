package com.platform.fintrack.domain.services;

import java.util.UUID;

public interface ITokenService {
    UUID validateToken(final String token);
}
