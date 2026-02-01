package br.edu.ufrn.expensify.account.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.edu.ufrn.expensify.account.entity.Account;
import br.edu.ufrn.expensify.account.exception.AccountNotFoundException;
import br.edu.ufrn.expensify.account.repository.AccountRepository;
import br.edu.ufrn.expensify.auth.entity.User;
import br.edu.ufrn.expensify.auth.service.AuthService;

@Service
public class AccountService {
    
    private final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    private final AuthService authService;

    public AccountService(AccountRepository accountRepository, AuthService authService) {
        this.accountRepository = accountRepository;
        this.authService = authService;
    }

    public List<Account> getAllAccounts() {
        User user = authService.getAuthenticatedUser();

        logger.info("Fetching all accounts for user: {}", user.getUsername());
        
        return accountRepository.findAllByUser(user);
    }

    public Account getAccountById(UUID id) {
        User user = authService.getAuthenticatedUser();

        logger.info("Fetching account with id: {} for user: {}", id, user.getUsername());
    
        return accountRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id + " for user: " + user.getUsername()));
    }

    public Account saveAccount(Account account) {
        User user = authService.getAuthenticatedUser();

        logger.info("Creating account for user: {}", user.getUsername());
        
        account.setUser(user);
        
        return accountRepository.save(account);
    }

    public Account updateAccount(Account account) {
        User user = authService.getAuthenticatedUser();

        logger.info("Updating account with id: {} for user: {}", account.getId(), user.getUsername());;

        return accountRepository.save(account);
    }

    public void deleteAccount(UUID id) {
        User user = authService.getAuthenticatedUser();

        logger.info("Deleting account with id: {} for user: {}", id, user.getUsername());

        accountRepository.deleteByIdAndUser(id, user);
    }

}
