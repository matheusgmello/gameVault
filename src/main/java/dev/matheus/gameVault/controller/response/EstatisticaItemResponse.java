package dev.matheus.gameVault.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record EstatisticaItemResponse(
        @Schema(type = "string", description = "Nome do item da estatistica")
        String name,
        @Schema(type = "number", description = "Valor numerico do item da estatistica")
        double value
) {
}
