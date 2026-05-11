package dev.matheus.gameVault.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @Schema(type = "string", description = "Nome completo do usuario", example = "Jogador Numero 1")
        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 255, message = "O nome deve ter no maximo 255 caracteres")
        String nome,

        @Schema(type = "string", description = "E-mail para cadastro e login", example = "player1@gamevault.com")
        @NotBlank(message = "O e-mail e obrigatorio")
        @Email(message = "Informe um e-mail valido")
        String email,

        @Schema(type = "string", description = "Senha de acesso", example = "12345")
        @NotBlank(message = "A senha e obrigatoria")
        @Size(min = 5, max = 255, message = "A senha deve ter entre 5 e 255 caracteres")
        String senha) {
}
