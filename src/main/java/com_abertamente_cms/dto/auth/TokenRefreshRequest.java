package com_abertamente_cms.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
        @NotBlank(message = "O token de refresh é obrigatório.")
        String refreshToken
) {}
