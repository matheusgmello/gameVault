package dev.matheus.gameVault.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record GeneroRequest(
        @Schema(type = "string", description = "Nome do Gênero", example = "RPG")
        @NotEmpty(message = "O nome do gênero é obrigatório")
        String nome) {
}