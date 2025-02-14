package com.platform.fintrack.domain.services.impl;

import com.platform.fintrack.domain.dtos.ExpenseDTO;
import com.platform.fintrack.domain.models.Expense;
import com.platform.fintrack.domain.models.Installment;
import com.platform.fintrack.domain.models.User;
import com.platform.fintrack.domain.services.IExpenseService;
import com.platform.fintrack.domain.services.IInstallmentService;
import com.platform.fintrack.domain.services.IUserService;
import com.platform.fintrack.infrastructure.exceptions.InvalidDataException;
import com.platform.fintrack.infrastructure.exceptions.UnexpectedException;
import com.platform.fintrack.infrastructure.repository.IExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseService implements IExpenseService {

    private final IExpenseRepository repository;
    private final IUserService userService;
    private final IInstallmentService installmentService;

    @Override
    public Expense create(final String token, final ExpenseDTO dto) {
        final User user = userService.findByToken(token);

        if(dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("the expense of the user with id {} cannot be less than or equal to 0", user.getId());
            throw new InvalidDataException("the expense cannot be less than or equal to 0");
        }

        final Expense expense = new Expense(null, dto.description(), dto.amount(), dto.date(),
                dto.type(), dto.isInstallments(), dto.installments(), user, null);

        if(Boolean.TRUE.equals(dto.isInstallments())) {

            if(isNull(expense.getInstallments()) || expense.getInstallments() < 2) {
                log.error("Installments must be at least 2 when isInstallments is true for user with id {}", user.getId());
                throw new InvalidDataException("Installments must be at least 2 when isInstallments is true");
            }

            List<Installment> installments = installmentService.generateInstallments(expense);
            expense.setInstallmentList(installments);
        }

        try {
            return repository.save(expense);
        } catch (Exception e) {
            log.error("Unexpected error saving to database, message: {}", e.getMessage());
            throw new UnexpectedException("Unexpected error saving to database");
        }
    }
}
