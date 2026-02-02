package br.edu.ufrn.expensify.auth.record;

import java.util.UUID;

public record UserResponse(
    UUID id,
    String username
) {}
