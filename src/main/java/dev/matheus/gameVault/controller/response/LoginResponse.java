package dev.matheus.gameVault.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse (
        @Schema(type = "string", description = "Token JWT gerado para autenticação")
        String token) {
}