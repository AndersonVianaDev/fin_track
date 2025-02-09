package com.platform.fintrack.domain.dtos;

import com.platform.fintrack.domain.enums.ExpenseType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseDTO(String description, BigDecimal amount, LocalDate date, ExpenseType type,
                         Boolean isInstallments, Integer installments) {
}
