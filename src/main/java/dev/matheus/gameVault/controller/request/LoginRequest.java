package dev.matheus.gameVault.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(type = "string", description = "E-mail do usuário", example = "admin@gamevault.com")
        String email,
        @Schema(type = "string", description = "Senha de acesso", example = "123456")
        String senha) {
}