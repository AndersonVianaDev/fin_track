package com.platform.fintrack.domain.services.impl;

import com.platform.fintrack.domain.dtos.ExpenseDTO;
import com.platform.fintrack.domain.enums.ExpenseType;
import com.platform.fintrack.domain.models.Expense;
import com.platform.fintrack.domain.models.Installment;
import com.platform.fintrack.domain.models.User;
import com.platform.fintrack.infrastructure.exceptions.DataConflictException;
import com.platform.fintrack.infrastructure.exceptions.InvalidDataException;
import com.platform.fintrack.infrastructure.exceptions.UnexpectedException;
import com.platform.fintrack.infrastructure.repository.IExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private IExpenseRepository repository;

    @InjectMocks
    private ExpenseService expenseService;

    private User user;
    private String token;
    private ExpenseDTO expenseDTO;
    private ExpenseDTO expenseDTOWithInstallments;
    private ExpenseDTO invalidExpenseDTO;
    private Expense expense;
    private Expense expenseWithInstallments;


    @BeforeEach
    void setUp() {
        user = new User(UUID.randomUUID(), "John Doe", "john@example.com", "encodedPassword");
        token = "token";
        expenseDTO = new ExpenseDTO("Lunch", new BigDecimal("50.00"), LocalDate.now(), ExpenseType.FIXED, false, null);
        expenseDTOWithInstallments = new ExpenseDTO("Phone", new BigDecimal("1200.00"), LocalDate.now(), ExpenseType.INSTALLMENTS, true, 12);
        invalidExpenseDTO = new ExpenseDTO("Invalid", BigDecimal.ZERO, LocalDate.now(), ExpenseType.VARIABLE, false, null);

        expense = new Expense();
        expense.setId(UUID.randomUUID());
        expense.setDescription(expenseDTO.description());
        expense.setAmount(expenseDTO.amount());
        expense.setDate(expenseDTO.date());
        expense.setType(expenseDTO.type());
        expense.setIsInstallment(false);
        expense.setUser(user);

        expenseWithInstallments = new Expense();
        expenseWithInstallments.setId(UUID.randomUUID());
        expenseWithInstallments.setDescription(expenseDTOWithInstallments.description());
        expenseWithInstallments.setAmount(expenseDTOWithInstallments.amount());
        expenseWithInstallments.setDate(expenseDTOWithInstallments.date());
        expenseWithInstallments.setType(expenseDTOWithInstallments.type());
        expenseWithInstallments.setIsInstallment(true);
        expenseWithInstallments.setInstallments(12);
        expenseWithInstallments.setUser(user);
        expenseWithInstallments.setInstallmentList(List.of(new Installment()));
    }


    @Test
    @DisplayName("Should create expense successfully")
    void shouldCreateExpenseSuccessfully() {
        when(userService.findByToken(token)).thenReturn(user);
        when(repository.save(any(Expense.class))).thenReturn(expense);

        Expense createdExpense = expenseService.create(token, expenseDTO);

        assertNotNull(createdExpense);
        assertEquals(expenseDTO.amount(), createdExpense.getAmount());
        verify(repository).save(any(Expense.class));
    }

    @Test
    @DisplayName("Should throw InvalidDataException if expense amount is less than or equal to zero")
    void shouldThrowInvalidDataExceptionIfAmountIsZeroOrNegative() {
        when(userService.findByToken(token)).thenReturn(user);

        assertThrows(InvalidDataException.class, () -> expenseService.create(token, invalidExpenseDTO));
    }

    @Test
    @DisplayName("Should handle unexpected exception when saving to database")
    void shouldHandleUnexpectedExceptionOnDatabaseError() {
        when(userService.findByToken(token)).thenReturn(user);
        when(repository.save(any(Expense.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(UnexpectedException.class, () -> expenseService.create(token, expenseDTO));
    }

    @Test
    @DisplayName("Should create installments when isInstallments is true")
    void shouldCreateInstallmentsSuccessfully() {
        when(userService.findByToken(token)).thenReturn(user);
        when(repository.save(any(Expense.class))).thenReturn(expenseWithInstallments);

        Expense createdExpense = expenseService.create(token, expenseDTOWithInstallments);

        assertNotNull(createdExpense);
        assertTrue(createdExpense.getInstallmentList().size() > 0);
    }

}