package br.edu.ufrn.expensify.transaction.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufrn.expensify.account.service.AccountService;
import br.edu.ufrn.expensify.auth.entity.User;
import br.edu.ufrn.expensify.auth.service.AuthService;
import br.edu.ufrn.expensify.transaction.entity.Expense;
import br.edu.ufrn.expensify.transaction.entity.Income;
import br.edu.ufrn.expensify.transaction.entity.Transaction;
import br.edu.ufrn.expensify.transaction.entity.Transfer;
import br.edu.ufrn.expensify.transaction.exception.TransactionNotFoundException;
import br.edu.ufrn.expensify.transaction.repository.ExpenseRepository;
import br.edu.ufrn.expensify.transaction.repository.IncomeRepository;
import br.edu.ufrn.expensify.transaction.repository.TransactionRepository;
import br.edu.ufrn.expensify.transaction.repository.TransferRepository;

@Service
public class TransactionService {
    
    private final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final TransferRepository transferRepository;

    private final AccountService accountService;
    private final AuthService authService;


    public TransactionService(
        TransactionRepository transactionRepository,
        IncomeRepository incomeRepository,
        ExpenseRepository expenseRepository,
        TransferRepository transferRepository,
        AccountService accountService,
        AuthService authService
    ) {
        this.transactionRepository = transactionRepository;
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.transferRepository = transferRepository;
        this.accountService = accountService;
        this.authService = authService;
    }

    public List<Transaction> retrieveAllTransactions() {
        User user = authService.getAuthenticatedUser();

        logger.info("Fetching all transactions for user: {}", user.getUsername());
    
        return transactionRepository.findAllByUser(user);
    }

    public Transaction retrieveTransaction(UUID id) {
        User user = authService.getAuthenticatedUser();

        logger.info("Fetching transaction with id: {} for user: {}", id, user.getUsername());
    
        return transactionRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id + " for user: " + user.getUsername()));
    }

    // Income methods

    public List<Income> retrieveAllIncomes() {
        User user = authService.getAuthenticatedUser();

        logger.info("Fetching all incomes for user: {}", user.getUsername());
    
        return incomeRepository.findAllByUser(user);
    }

    public Income retrieveIncome(UUID id) {
        User user = authService.getAuthenticatedUser();

        logger.info("Fetching income with id: {} for user: {}", id, user.getUsername());
    
        return incomeRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new TransactionNotFoundException("Income not found with id: " + id + " for user: " + user.getUsername()));
    }

    @Transactional
    public Income createIncome(Income income) {
        User user = authService.getAuthenticatedUser();
        income.setUser(user);

        Income createdIncome = incomeRepository.save(income);
        
        accountService.increaseBalance(createdIncome.getAccount().getId(), createdIncome.getAmount());

        logger.info("Created income: {}", createdIncome);

        return createdIncome;
    }

    @Transactional
    public void deleteIncome(Income income) {
        accountService.decreaseBalance(income.getAccount().getId(), income.getAmount());
        incomeRepository.delete(income);

        logger.info("Deleted income: {}", income);
    }
    
    // Expense methods

    public List<Expense> retrieveAllExpenses() {
        User user = authService.getAuthenticatedUser();

        logger.info("Fetching all expenses for user: {}", user.getUsername());
    
        return expenseRepository.findAllByUser(user);
    }

    public Expense retrieveExpense(UUID id) {
        User user = authService.getAuthenticatedUser();

        logger.info("Fetching expense with id: {} for user: {}", id, user.getUsername());
    
        return expenseRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new TransactionNotFoundException("Expense not found with id: " + id + " for user: " + user.getUsername()));
    }

    @Transactional
    public Expense createExpense(Expense expense) {
        User user = authService.getAuthenticatedUser();
        expense.setUser(user);

        Expense createdExpense = expenseRepository.save(expense);
        
        accountService.decreaseBalance(createdExpense.getAccount().getId(), createdExpense.getAmount());

        logger.info("Created expense: {}", createdExpense);

        return createdExpense;
    }

    @Transactional
    public void deleteExpense(Expense expense) {
        accountService.increaseBalance(expense.getAccount().getId(), expense.getAmount());
        expenseRepository.delete(expense);

        logger.info("Deleted expense: {}", expense);
    }

    // Transfer methods

    public List<Transfer> retrieveAllTransfers() {
        User user = authService.getAuthenticatedUser();

        logger.info("Fetching all transfers for user: {}", user.getUsername());
    
        return transferRepository.findAllByUser(user);
    }

    public Transfer retrieveTransfer(UUID id) {
        User user = authService.getAuthenticatedUser();

        logger.info("Fetching transfer with id: {} for user: {}", id, user.getUsername());
    
        return transferRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new TransactionNotFoundException("Transfer not found with id: " + id + " for user: " + user.getUsername()));
    }

    @Transactional
    public Transfer createTransfer(Transfer transfer) {
        User user = authService.getAuthenticatedUser();
        transfer.setUser(user);

        Transfer createdTransfer = transferRepository.save(transfer);

        accountService.decreaseBalance(createdTransfer.getSourceAccount().getId(), createdTransfer.getAmount());
        accountService.increaseBalance(createdTransfer.getDestinationAccount().getId(), createdTransfer.getAmount());

        logger.info("Created transfer: {}", transfer);

        return createdTransfer;
    }

    @Transactional
    public void deleteTransfer(Transfer transfer) {
        accountService.increaseBalance(transfer.getSourceAccount().getId(), transfer.getAmount());
        accountService.decreaseBalance(transfer.getDestinationAccount().getId(), transfer.getAmount());
        transferRepository.delete(transfer);

        logger.info("Deleted transfer: {}", transfer);
    }

}
