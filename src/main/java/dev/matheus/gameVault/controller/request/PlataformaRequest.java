package dev.matheus.gameVault.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record PlataformaRequest(
        @Schema(type = "string", description = "Nome da Plataforma", example = "Steam")
        @NotEmpty(message = "O nome da plataforma é obrigatório")
        String nome) {
}