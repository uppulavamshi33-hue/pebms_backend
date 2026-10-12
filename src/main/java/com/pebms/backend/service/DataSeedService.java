package com.pebms.backend.service;

import com.pebms.backend.model.Budget;
import com.pebms.backend.model.Expense;
import com.pebms.backend.model.Income;
import com.pebms.backend.model.User;
import com.pebms.backend.repository.BudgetRepository;
import com.pebms.backend.repository.ExpenseRepository;
import com.pebms.backend.repository.IncomeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataSeedService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final AuthService authService;

    public DataSeedService(IncomeRepository incomeRepository,
                           ExpenseRepository expenseRepository,
                           BudgetRepository budgetRepository,
                           AuthService authService) {
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.authService = authService;
    }

    @PostConstruct
    public void init() {
        User demoUser = authService.getOrCreateDemoUser();
        if (expenseRepository.findByUserIdOrderByDateDesc(demoUser.getId()).isEmpty()) {
            seedSampleData(false);
        }
    }

    @Transactional
    public void seedSampleData(boolean overwrite) {
        User demoUser = authService.getOrCreateDemoUser();

        if (overwrite) {
            budgetRepository.deleteAll();
            expenseRepository.deleteAll();
            incomeRepository.deleteAll();
        }

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        List<Income> incomes = new ArrayList<>();
        List<Expense> expenses = new ArrayList<>();

        // Generate realistic student/individual cashflow data for past 4 months up to current month
        for (int offset = 3; offset >= 0; offset--) {
            LocalDate monthDate = today.minusMonths(offset);
            int y = monthDate.getYear();
            int m = monthDate.getMonthValue();

            // Monthly primary stipend / allowance / salary
            Income inc1 = new Income("Student Stipend & Part-Time Salary", BigDecimal.valueOf(3200.00), "Salary",
                    LocalDate.of(y, m, 1), "Bank Transfer", "Monthly university TA & part-time role stipend");
            inc1.setUser(demoUser);
            incomes.add(inc1);

            // Freelance / Web tutoring
            if (offset != 2) {
                Income inc2 = new Income("Freelance Tutoring / Coding Gig", BigDecimal.valueOf(650.00), "Freelance",
                        LocalDate.of(y, m, 12), "UPI", "Milestone payment for tutoring");
                inc2.setUser(demoUser);
                incomes.add(inc2);
            }

            // Investment / Savings Interest
            Income inc3 = new Income("Savings Interest & Dividend", BigDecimal.valueOf(180.00), "Investment",
                    LocalDate.of(y, m, 20), "Bank Transfer", "Monthly returns");
            inc3.setUser(demoUser);
            incomes.add(inc3);

            // Expenses for this month
            // 1. Rent / Campus Housing
            Expense e1 = new Expense("Campus Housing / Room Rent", BigDecimal.valueOf(950.00), "Housing",
                    LocalDate.of(y, m, 2), "Bank Transfer", "Monthly room rent & water");
            e1.setUser(demoUser);
            expenses.add(e1);

            // 2. Groceries & Food Dining
            Expense e2 = new Expense("Weekly Supermarket & Staples", BigDecimal.valueOf(125.50), "Food & Dining",
                    LocalDate.of(y, m, 5), "Credit Card", "Vegetables, dairy, bread");
            e2.setUser(demoUser);
            expenses.add(e2);

            Expense e3 = new Expense("Campus Cafeteria & Meals", BigDecimal.valueOf(88.00), "Food & Dining",
                    LocalDate.of(y, m, 14), "UPI", "Daily lunches and dinners");
            e3.setUser(demoUser);
            expenses.add(e3);

            Expense e4 = new Expense("Weekend Dining with Friends", BigDecimal.valueOf(54.20), "Food & Dining",
                    LocalDate.of(y, m, 21), "Cash", "Social dinner at pizzeria");
            e4.setUser(demoUser);
            expenses.add(e4);

            // 3. Education & Course Material
            Expense e5 = new Expense("Textbooks & Lab Supplies", BigDecimal.valueOf(110.00), "Education",
                    LocalDate.of(y, m, 8), "Credit Card", "Course materials & stationery");
            e5.setUser(demoUser);
            expenses.add(e5);

            // 4. Utilities & High-Speed Internet
            Expense e6 = new Expense("Mobile Plan & Broadband Internet", BigDecimal.valueOf(45.00), "Utilities",
                    LocalDate.of(y, m, 6), "UPI", "Monthly unlimited data");
            e6.setUser(demoUser);
            expenses.add(e6);

            // 5. Travel & Transit
            Expense e7 = new Expense("Monthly Student Transit Pass", BigDecimal.valueOf(60.00), "Travel",
                    LocalDate.of(y, m, 3), "Debit Card", "Metro / Bus card pass");
            e7.setUser(demoUser);
            expenses.add(e7);

            // 6. Entertainment & Subscriptions
            Expense e8 = new Expense("Music & Video Subscriptions", BigDecimal.valueOf(19.99), "Entertainment",
                    LocalDate.of(y, m, 11), "Credit Card", "Spotify student & Prime");
            e8.setUser(demoUser);
            expenses.add(e8);

            // 7. Shopping
            if (offset % 2 == 0) {
                Expense e9 = new Expense("Casual Apparel & Sneakers", BigDecimal.valueOf(75.00), "Shopping",
                        LocalDate.of(y, m, 16), "Debit Card", "Sale purchase");
                e9.setUser(demoUser);
                expenses.add(e9);
            }
        }

        incomeRepository.saveAll(incomes);
        expenseRepository.saveAll(expenses);

        // Seed Category Budgets for the demo user safely
        createOrUpdateSeedBudget(demoUser, "Housing", currentMonth, currentYear, BigDecimal.valueOf(1000.00), "Monthly housing cap");
        createOrUpdateSeedBudget(demoUser, "Food & Dining", currentMonth, currentYear, BigDecimal.valueOf(350.00), "Groceries & meals");
        createOrUpdateSeedBudget(demoUser, "Education", currentMonth, currentYear, BigDecimal.valueOf(150.00), "Books & materials");
        createOrUpdateSeedBudget(demoUser, "Travel", currentMonth, currentYear, BigDecimal.valueOf(100.00), "Transit & commute");
        createOrUpdateSeedBudget(demoUser, "Utilities", currentMonth, currentYear, BigDecimal.valueOf(80.00), "Internet & phone");
        createOrUpdateSeedBudget(demoUser, "Entertainment", currentMonth, currentYear, BigDecimal.valueOf(60.00), "Subscriptions & outings");
        createOrUpdateSeedBudget(demoUser, "Shopping", currentMonth, currentYear, BigDecimal.valueOf(100.00), "Discretionary clothing");
        createOrUpdateSeedBudget(demoUser, "OVERALL", currentMonth, currentYear, BigDecimal.valueOf(2000.00), "Overall monthly target ceiling");
    }

    private void createOrUpdateSeedBudget(User user, String category, int month, int year, BigDecimal limit, String notes) {
        Budget b = budgetRepository.findByUserIdAndCategoryIgnoreCaseAndMonthAndYear(user.getId(), category, month, year)
                .orElse(new Budget());
        b.setUser(user);
        b.setCategory(category);
        b.setMonth(month);
        b.setYear(year);
        b.setMonthlyLimit(limit);
        b.setNotes(notes);
        budgetRepository.save(b);
    }
}
