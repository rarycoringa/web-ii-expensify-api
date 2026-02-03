package br.edu.ufrn.expensify.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufrn.expensify.auth.entity.User;
import br.edu.ufrn.expensify.auth.record.AuthRequest;
import br.edu.ufrn.expensify.auth.record.AuthResponse;
import br.edu.ufrn.expensify.auth.record.UserResponse;
import br.edu.ufrn.expensify.auth.service.AuthService;
import br.edu.ufrn.expensify.auth.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        String token = authService.authenticate(
            request.username(),
            request.password()
        );

        AuthResponse response = new AuthResponse(token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody AuthRequest request) {
        User user = userService.registerUser(request.username(), request.password());
        
        UserResponse response = new UserResponse(user.getId(), user.getUsername());
        
        return ResponseEntity.created(null).body(response);
    }

}
