package dev.matheus.gameVault.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record PlataformaRequest(
        @Schema(type = "string", description = "Nome da plataforma", example = "Steam")
        @NotBlank(message = "O nome da plataforma e obrigatorio")
        @Size(max = 100, message = "O nome da plataforma deve ter no maximo 100 caracteres")
        String nome) {
}
