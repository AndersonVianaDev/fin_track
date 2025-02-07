package com.platform.fintrack.infrastructure.repository;

import com.platform.fintrack.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(final String email);
}
