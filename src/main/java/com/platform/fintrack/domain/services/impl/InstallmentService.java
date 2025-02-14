package com.platform.fintrack.domain.services.impl;

import com.platform.fintrack.domain.models.Expense;
import com.platform.fintrack.domain.models.Installment;
import com.platform.fintrack.domain.services.IInstallmentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class InstallmentService implements IInstallmentService {
    @Override
    public List<Installment> generateInstallments(Expense expense) {

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
