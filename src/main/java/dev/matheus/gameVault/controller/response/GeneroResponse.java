package dev.matheus.gameVault.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record GeneroResponse(
        @Schema(type = "integer", format = "int64", description = "ID do Gênero", example = "1")
        Long id,

        @Schema(type = "string", description = "Nome do Gênero", example = "Ação")
        String nome
) {
}