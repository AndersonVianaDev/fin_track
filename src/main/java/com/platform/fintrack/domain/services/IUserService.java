package com.platform.fintrack.domain.services;

import com.platform.fintrack.domain.dtos.UserDTO;
import com.platform.fintrack.domain.models.User;

import java.util.UUID;

public interface IUserService {

    User create(final UserDTO dto);
    User findById(final UUID id);
    User findByToken(final String token);
}
