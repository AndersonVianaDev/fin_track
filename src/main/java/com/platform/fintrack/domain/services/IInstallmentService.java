package com.platform.fintrack.domain.services;

import com.platform.fintrack.domain.models.Expense;
import com.platform.fintrack.domain.models.Installment;

import java.util.List;

public interface IInstallmentService {
    List<Installment> generateInstallments(Expense expense);
}
