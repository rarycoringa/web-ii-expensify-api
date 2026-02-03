package br.edu.ufrn.expensify.account.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufrn.expensify.account.entity.Account;
import br.edu.ufrn.expensify.account.record.AccountResponse;
import br.edu.ufrn.expensify.account.record.CreateAccountRequest;
import br.edu.ufrn.expensify.account.record.UpdateAccountRequest;
import br.edu.ufrn.expensify.account.service.AccountService;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();

        List<AccountResponse> responses = accounts.stream()
            .map(account -> new AccountResponse(
                account.getId(),
                account.getName(),
                account.getBalance()
            ))
            .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable UUID id) {
        Account account = accountService.getAccountById(id);

        AccountResponse response = new AccountResponse(
            account.getId(),
            account.getName(),
            account.getBalance()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody CreateAccountRequest request) {
        Account account = new Account();

        account.setName(request.name());
        account.setBalance(request.balance());

        Account savedAccount = accountService.saveAccount(account);
        
        AccountResponse response = new AccountResponse(
            savedAccount.getId(),
            savedAccount.getName(),
            savedAccount.getBalance()
        );

        return ResponseEntity.created(null).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable UUID id, @RequestBody UpdateAccountRequest request) {
        Account account = accountService.getAccountById(id);

        if (request.name() != null) {
            account.setName(request.name());
        }

        Account updatedAccount = accountService.updateAccount(account);

        AccountResponse response = new AccountResponse(
            updatedAccount.getId(),
            updatedAccount.getName(),
            updatedAccount.getBalance()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {

        accountService.deleteAccount(id);

        return ResponseEntity.noContent().build();
    }

}
