package br.edu.ufrn.expensify.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.edu.ufrn.expensify.auth.entity.User;

@Service
public class AuthService {
    
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
        AuthenticationManager authenticationManager,
        JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public String authenticate(String username, String password) {
        logger.info("Authenticating user: {}", username);

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                username,
                password
            )
        );

        logger.info("User {} authenticated successfully", username);

        User user = (User) authentication.getPrincipal();

        logger.info("Generating JWT token for user: {}", username);

        return jwtService.generateToken(user);
    }

}
