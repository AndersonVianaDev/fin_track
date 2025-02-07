package com.platform.fintrack.domain.services.impl;

import com.platform.fintrack.domain.dtos.UserDTO;
import com.platform.fintrack.domain.models.User;
import com.platform.fintrack.domain.services.IPasswordEncodeService;
import com.platform.fintrack.domain.services.IUserService;
import com.platform.fintrack.infrastructure.exceptions.DataConflictException;
import com.platform.fintrack.infrastructure.exceptions.UnexpectedException;
import com.platform.fintrack.infrastructure.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository repository;
    private final IPasswordEncodeService passwordEncodeService;

    @Override
    public User create(final UserDTO dto) {
        Optional<User> userAlreadyExists = repository.findByEmail(dto.email());

        if(userAlreadyExists.isPresent()) {
            log.error("E-mail {} already registered", dto.email());
            throw new DataConflictException("E-mail already registered");
        }

        final String password = passwordEncodeService.encode(dto.password());

        User user = new User(null, dto.name(), dto.email(), password);

        try {
            return repository.save(user);
        } catch (Exception e) {
            log.error("Unexpected error saving to database, message: {}", e.getMessage());
            throw new UnexpectedException("Unexpected error saving to database");
        }
    }
}
