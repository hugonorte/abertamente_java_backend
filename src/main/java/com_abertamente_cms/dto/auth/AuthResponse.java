package com_abertamente_cms.dto.auth;

import java.util.UUID;

public record AuthResponse(
        String token,
        String refreshToken,
        UUID id,
        String name,
        String email,
        String role
) {}
