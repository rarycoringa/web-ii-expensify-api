package br.edu.ufrn.expensify.transaction.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufrn.expensify.account.entity.Account;
import br.edu.ufrn.expensify.account.service.AccountService;
import br.edu.ufrn.expensify.transaction.entity.Expense;
import br.edu.ufrn.expensify.transaction.entity.Income;
import br.edu.ufrn.expensify.transaction.entity.Transfer;
import br.edu.ufrn.expensify.transaction.record.CreateExpenseRequest;
import br.edu.ufrn.expensify.transaction.record.CreateIncomeRequest;
import br.edu.ufrn.expensify.transaction.record.CreateTransferRequest;
import br.edu.ufrn.expensify.transaction.record.ExpenseResponse;
import br.edu.ufrn.expensify.transaction.record.IncomeResponse;
import br.edu.ufrn.expensify.transaction.record.TransferResponse;
import br.edu.ufrn.expensify.transaction.service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    
    private final TransactionService transactionService;
    private final AccountService accountService;

    public TransactionController(
        TransactionService transactionService,
        AccountService accountService
    ) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    // Income endpoints

    @GetMapping("/incomes")
    public ResponseEntity<List<IncomeResponse>> retrieveAllIncomes() {
        List<Income> incomes = transactionService.retrieveAllIncomes();

        List<IncomeResponse> response = incomes.stream()
            .map(income -> new IncomeResponse(
                income.getId(),
                income.getDescription(),
                income.getAmount(),
                income.getDate(),
                income.getAccount().getId()))
            .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/incomes/{id}")
    public ResponseEntity<IncomeResponse> retrieveIncome(@PathVariable UUID id) {
        Income income = transactionService.retrieveIncome(id);

        IncomeResponse response = new IncomeResponse(
            income.getId(),
            income.getDescription(),
            income.getAmount(),
            income.getDate(),
            income.getAccount().getId()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/incomes")
    public ResponseEntity<IncomeResponse> createIncome(@RequestBody CreateIncomeRequest request) {
        Account account = accountService.getAccountById(request.accountId());

        Income income = new Income();
        income.setDescription(request.description());
        income.setAmount(request.amount());
        income.setDate(request.date());
        income.setAccount(account);

        Income createdIncome = transactionService.createIncome(income);

        IncomeResponse response = new IncomeResponse(
            createdIncome.getId(),
            createdIncome.getDescription(),
            createdIncome.getAmount(),
            createdIncome.getDate(),
            createdIncome.getAccount().getId()
        );

        return ResponseEntity.created(null).body(response);
    }

    @DeleteMapping("/incomes/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable UUID id) {
        Income income = transactionService.retrieveIncome(id);

        transactionService.deleteIncome(income);

        return ResponseEntity.noContent().build();
    }

    // Expense endpoints

    @GetMapping("/expenses")
    public ResponseEntity<List<ExpenseResponse>> retrieveAllExpenses() {
        List<Expense> expenses = transactionService.retrieveAllExpenses();

        List<ExpenseResponse> response = expenses.stream()
            .map(expense -> new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getDate(),
                expense.getAccount().getId()))
            .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/expenses/{id}")
    public ResponseEntity<ExpenseResponse> retrieveExpense(@PathVariable UUID id) {
        Expense expense = transactionService.retrieveExpense(id);

        ExpenseResponse response = new ExpenseResponse(
            expense.getId(),
            expense.getDescription(),
            expense.getAmount(),
            expense.getDate(),
            expense.getAccount().getId()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/expenses")
    public ResponseEntity<ExpenseResponse> createExpense(@RequestBody CreateExpenseRequest request) {
        Account account = accountService.getAccountById(request.accountId());

        Expense expense = new Expense();
        expense.setDescription(request.description());
        expense.setAmount(request.amount());
        expense.setDate(request.date());
        expense.setAccount(account);

        Expense createdExpense = transactionService.createExpense(expense);

        ExpenseResponse response = new ExpenseResponse(
            createdExpense.getId(),
            createdExpense.getDescription(),
            createdExpense.getAmount(),
            createdExpense.getDate(),
            createdExpense.getAccount().getId()
        );

        return ResponseEntity.created(null).body(response);
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable UUID id) {
        Expense expense = transactionService.retrieveExpense(id);

        transactionService.deleteExpense(expense);

        return ResponseEntity.noContent().build();
    }

    // Transfer endpoints

    @GetMapping("/transfers")
    public ResponseEntity<List<TransferResponse>> retrieveAllTransfers() {
        List<Transfer> transfers = transactionService.retrieveAllTransfers();

        List<TransferResponse> response = transfers.stream()
            .map(transfer -> new TransferResponse(
                transfer.getId(),
                transfer.getDescription(),
                transfer.getAmount(),
                transfer.getDate(),
                transfer.getSourceAccount().getId(),
                transfer.getDestinationAccount().getId()))
            .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/transfers/{id}")
    public ResponseEntity<TransferResponse> retrieveTransfer(@PathVariable UUID id) {
        Transfer transfer = transactionService.retrieveTransfer(id);

        TransferResponse response = new TransferResponse(
            transfer.getId(),
            transfer.getDescription(),
            transfer.getAmount(),
            transfer.getDate(),
            transfer.getSourceAccount().getId(),
            transfer.getDestinationAccount().getId()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfers")
    public ResponseEntity<TransferResponse> createTransfer(@RequestBody CreateTransferRequest request) {
        Account sourceAccount = accountService.getAccountById(request.sourceAccountId());
        Account destinationAccount = accountService.getAccountById(request.destinationAccountId());

        Transfer transfer = new Transfer();
        transfer.setDescription(request.description());
        transfer.setAmount(request.amount());
        transfer.setDate(request.date());
        transfer.setSourceAccount(sourceAccount);
        transfer.setDestinationAccount(destinationAccount);

        Transfer createdTransfer = transactionService.createTransfer(transfer);

        TransferResponse response = new TransferResponse(
            createdTransfer.getId(),
            createdTransfer.getDescription(),
            createdTransfer.getAmount(),
            createdTransfer.getDate(),
            createdTransfer.getSourceAccount().getId(),
            createdTransfer.getDestinationAccount().getId()
        );

        return ResponseEntity.created(null).body(response);
    }

    @DeleteMapping("/transfers/{id}")
    public ResponseEntity<Void> deleteTransfer(@PathVariable UUID id) {
        Transfer transfer = transactionService.retrieveTransfer(id);

        transactionService.deleteTransfer(transfer);

        return ResponseEntity.noContent().build();
    }

}
