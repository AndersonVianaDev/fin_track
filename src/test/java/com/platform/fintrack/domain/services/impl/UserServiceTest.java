package com.platform.fintrack.domain.services.impl;

import com.platform.fintrack.domain.dtos.UserDTO;
import com.platform.fintrack.domain.models.User;
import com.platform.fintrack.domain.services.IPasswordEncodeService;
import com.platform.fintrack.domain.services.ITokenService;
import com.platform.fintrack.infrastructure.exceptions.DataConflictException;
import com.platform.fintrack.infrastructure.exceptions.NotFoundException;
import com.platform.fintrack.infrastructure.exceptions.UnexpectedException;
import com.platform.fintrack.infrastructure.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private IUserRepository repository;

    @Mock
    private IPasswordEncodeService passwordEncodeService;

    @Mock
    private ITokenService tokenService;

    @InjectMocks
    private UserService userService;

    private UserDTO userDTO;
    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userDTO = new UserDTO("John Doe", "john@example.com", "password123");
        user = new User(userId, "John Doe", "john@example.com", "encodedPassword");
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        when(repository.findByEmail(userDTO.email())).thenReturn(Optional.empty());
        when(passwordEncodeService.encode(userDTO.password())).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.create(userDTO);

        assertNotNull(createdUser);
        assertEquals(userDTO.email(), createdUser.getEmail());
        verify(repository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DataConflictException if email exists")
    void shouldThrowDataConflictExceptionIfEmailExists() {
        when(repository.findByEmail(userDTO.email())).thenReturn(Optional.of(user));

        assertThrows(DataConflictException.class, () -> userService.create(userDTO));
        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw UnexpectedException on database error")
    void shouldThrowUnexpectedExceptionOnDatabaseError() {
        when(repository.findByEmail(userDTO.email())).thenReturn(Optional.empty());
        when(passwordEncodeService.encode(userDTO.password())).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(UnexpectedException.class, () -> userService.create(userDTO));
    }

    @Test
    @DisplayName("Should find user by ID successfully")
    void shouldFindUserByIdSuccessfully() {
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.findById(userId);

        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
    }

    @Test
    @DisplayName("Should throw NotFoundException if user not found by ID")
    void shouldThrowNotFoundExceptionIfUserNotFoundById() {
        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    @DisplayName("Should find user by token successfully")
    void shouldFindUserByTokenSuccessfully() {
        when(tokenService.validateToken("validToken")).thenReturn(userId);
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.findByToken("validToken");

        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
    }

    @Test
    @DisplayName("Should throw NotFoundException if user not found by token")
    void shouldThrowNotFoundExceptionIfUserNotFoundByToken() {
        when(tokenService.validateToken("validToken")).thenReturn(userId);
        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findByToken("validToken"));
    }
}