package com.platform.fintrack.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tb_installments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Installment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer number;
    private BigDecimal amount;
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    @JsonIgnore
    private Expense expense;
}
