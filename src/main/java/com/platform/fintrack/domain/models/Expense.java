package com.platform.fintrack.domain.models;

import com.platform.fintrack.domain.enums.ExpenseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_expenses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expense {

    // todo: boolean se tem juros e se tiver quanto de juros que vai entrar
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String description;
    private BigDecimal amount;
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private ExpenseType type;

    private Boolean isInstallment;
    private Integer installments;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Installment> installmentList;
}
