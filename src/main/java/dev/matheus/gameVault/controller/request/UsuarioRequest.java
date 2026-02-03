package dev.matheus.gameVault.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record UsuarioRequest(
        @Schema(type = "string", description = "Nome completo do usuário", example = "Jogador Número 1")
        @NotEmpty(message = "O nome é obrigatório")
        String nome,

        @Schema(type = "string", description = "E-mail para cadastro e login", example = "player1@gamevault.com")
        @NotEmpty(message = "O e-mail é obrigatório")
        String email,

        @Schema(type = "string", description = "Senha de acesso (mínimo 6 caracteres)", example = "123456")
        @NotEmpty(message = "A senha é obrigatória")
        String senha) {
}