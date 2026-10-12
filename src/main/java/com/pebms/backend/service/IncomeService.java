package com.pebms.backend.service;

import com.pebms.backend.dto.IncomeDto;
import com.pebms.backend.exception.ResourceNotFoundException;
import com.pebms.backend.model.Income;
import com.pebms.backend.model.User;
import com.pebms.backend.repository.IncomeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final AuthService authService;

    public IncomeService(IncomeRepository incomeRepository, AuthService authService) {
        this.incomeRepository = incomeRepository;
        this.authService = authService;
    }

    public List<IncomeDto> getAllIncomes() {
        User user = authService.getCurrentAuthenticatedUser();
        return incomeRepository.findByUserIdOrderByDateDesc(user.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<IncomeDto> getIncomesByDateRange(LocalDate startDate, LocalDate endDate) {
        User user = authService.getCurrentAuthenticatedUser();
        return incomeRepository.findByUserIdAndDateBetweenOrderByDateDesc(user.getId(), startDate, endDate)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<IncomeDto> getIncomesByCategory(String category) {
        User user = authService.getCurrentAuthenticatedUser();
        return incomeRepository.findByUserIdAndCategoryIgnoreCaseOrderByDateDesc(user.getId(), category)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public IncomeDto getIncomeById(Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found with id: " + id));

        if (income.getUser() != null && !income.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied to requested income record");
        }
        return mapToDto(income);
    }

    public IncomeDto createIncome(IncomeDto dto) {
        User user = authService.getCurrentAuthenticatedUser();

        Income income = new Income();
        income.setUser(user);
        income.setTitle(dto.getTitle());
        income.setAmount(dto.getAmount());
        income.setCategory(dto.getCategory());
        income.setDate(dto.getDate());
        income.setPaymentMethod(dto.getPaymentMethod());
        income.setNotes(dto.getNotes());

        Income saved = incomeRepository.save(income);
        return mapToDto(saved);
    }

    public IncomeDto updateIncome(Long id, IncomeDto dto) {
        User user = authService.getCurrentAuthenticatedUser();
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found with id: " + id));

        if (income.getUser() != null && !income.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied to modify this income record");
        }

        income.setTitle(dto.getTitle());
        income.setAmount(dto.getAmount());
        income.setCategory(dto.getCategory());
        income.setDate(dto.getDate());
        income.setPaymentMethod(dto.getPaymentMethod());
        income.setNotes(dto.getNotes());

        Income updated = incomeRepository.save(income);
        return mapToDto(updated);
    }

    public void deleteIncome(Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found with id: " + id));

        if (income.getUser() != null && !income.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied to delete this income record");
        }

        incomeRepository.deleteById(id);
    }

    public IncomeDto mapToDto(Income entity) {
        return new IncomeDto(
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
