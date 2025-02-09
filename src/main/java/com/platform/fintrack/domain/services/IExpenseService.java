package com.platform.fintrack.domain.services;

import com.platform.fintrack.domain.dtos.ExpenseDTO;
import com.platform.fintrack.domain.models.Expense;

public interface IExpenseService {
    Expense create(final String token, final ExpenseDTO dto);
}
