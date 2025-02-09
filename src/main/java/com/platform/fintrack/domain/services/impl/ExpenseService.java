package com.platform.fintrack.domain.services.impl;

import com.platform.fintrack.domain.dtos.ExpenseDTO;
import com.platform.fintrack.domain.models.Expense;
import com.platform.fintrack.domain.models.Installment;
import com.platform.fintrack.domain.models.User;
import com.platform.fintrack.domain.services.IExpenseService;
import com.platform.fintrack.domain.services.IUserService;
import com.platform.fintrack.infrastructure.exceptions.DataConflictException;
import com.platform.fintrack.infrastructure.exceptions.UnexpectedException;
import com.platform.fintrack.infrastructure.repository.IExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseService implements IExpenseService {

    private final IExpenseRepository repository;
    private final IUserService userService;

    @Override
    public Expense create(final String token, final ExpenseDTO dto) {
        final User user = userService.findByToken(token);

        if(dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("the expense of the user with id {} cannot be less than or equal to 0", user.getId());
            throw new DataConflictException("the expense cannot be less than or equal to 0");
        }

        final Expense expense = new Expense(null, dto.description(), dto.amount(), dto.date(),
                dto.type(), dto.isInstallments(), dto.installments(), user, null);

        if(dto.isInstallments()) {
            List<Installment> installments = generateInstallments(expense);
            expense.setInstallmentList(installments);
        }

        try {
            return repository.save(expense);
        } catch (Exception e) {
            log.error("Unexpected error saving to database, message: {}", e.getMessage());
            throw new UnexpectedException("Unexpected error saving to database");
        }
    }

    private List<Installment> generateInstallments(Expense expense) {
        final List<Installment> installments = new ArrayList<>();
        final BigDecimal installmentAmount = expense.getAmount()
                .divide(BigDecimal.valueOf(expense.getInstallments()), RoundingMode.HALF_UP);

        for (int i = 1; i <= expense.getInstallments(); i++) {
            Installment installment = new Installment();
            installment.setNumber(i);
            installment.setAmount(installmentAmount);
            installment.setDueDate(expense.getDate().plusMonths(i - 1));
            installment.setExpense(expense);

            installments.add(installment);
        }

        return installments;
    }
}
