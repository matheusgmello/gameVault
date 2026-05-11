package dev.matheus.gameVault.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GeneroRequest(
        @Schema(type = "string", description = "Nome do genero", example = "RPG")
        @NotBlank(message = "O nome do genero e obrigatorio")
        @Size(max = 100, message = "O nome do genero deve ter no maximo 100 caracteres")
        String nome) {
}
