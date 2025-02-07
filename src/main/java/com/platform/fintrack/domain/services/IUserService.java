package com.platform.fintrack.domain.services;

import com.platform.fintrack.domain.dtos.UserDTO;
import com.platform.fintrack.domain.models.User;

public interface IUserService {

    User create(final UserDTO dto);
}
