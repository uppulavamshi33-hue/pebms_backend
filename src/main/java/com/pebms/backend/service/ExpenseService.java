package com.pebms.backend.service;

import com.pebms.backend.dto.ExpenseDto;
import com.pebms.backend.exception.ResourceNotFoundException;
import com.pebms.backend.model.Expense;
import com.pebms.backend.model.User;
import com.pebms.backend.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final AuthService authService;

    public ExpenseService(ExpenseRepository expenseRepository, AuthService authService) {
        this.expenseRepository = expenseRepository;
        this.authService = authService;
    }

    public List<ExpenseDto> getAllExpenses() {
        User user = authService.getCurrentAuthenticatedUser();
        return expenseRepository.findByUserIdOrderByDateDesc(user.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ExpenseDto> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        User user = authService.getCurrentAuthenticatedUser();
        return expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(user.getId(), startDate, endDate)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ExpenseDto> getExpensesByCategory(String category) {
        User user = authService.getCurrentAuthenticatedUser();
        return expenseRepository.findByUserIdAndCategoryIgnoreCaseOrderByDateDesc(user.getId(), category)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ExpenseDto getExpenseById(Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (expense.getUser() != null && !expense.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied to requested expense record");
        }

        return mapToDto(expense);
    }

    public ExpenseDto createExpense(ExpenseDto dto) {
        User user = authService.getCurrentAuthenticatedUser();

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setTitle(dto.getTitle());
        expense.setAmount(dto.getAmount());
        expense.setCategory(dto.getCategory());
        expense.setDate(dto.getDate());
        expense.setPaymentMethod(dto.getPaymentMethod());
        expense.setNotes(dto.getNotes());

        Expense saved = expenseRepository.save(expense);
        return mapToDto(saved);
    }

    public ExpenseDto updateExpense(Long id, ExpenseDto dto) {
        User user = authService.getCurrentAuthenticatedUser();
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (expense.getUser() != null && !expense.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied to modify this expense record");
        }

        expense.setTitle(dto.getTitle());
        expense.setAmount(dto.getAmount());
        expense.setCategory(dto.getCategory());
        expense.setDate(dto.getDate());
        expense.setPaymentMethod(dto.getPaymentMethod());
        expense.setNotes(dto.getNotes());

        Expense updated = expenseRepository.save(expense);
        return mapToDto(updated);
    }

    public void deleteExpense(Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (expense.getUser() != null && !expense.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied to delete this expense record");
        }

        expenseRepository.deleteById(id);
    }

    public ExpenseDto mapToDto(Expense entity) {
        return new ExpenseDto(
                entity.getId(),
                entity.getTitle(),
                entity.getAmount(),
                entity.getCategory(),
                entity.getDate(),
                entity.getPaymentMethod(),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
