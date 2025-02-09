package com.platform.fintrack.infrastructure.repository;

import com.platform.fintrack.domain.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IExpenseRepository extends JpaRepository<Expense, UUID> {

}
