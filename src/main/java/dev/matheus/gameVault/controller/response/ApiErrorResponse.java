package dev.matheus.gameVault.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.Map;

@Builder
public record ApiErrorResponse(
        @Schema(type = "string", description = "Data e hora do erro")
        OffsetDateTime timestamp,
        @Schema(type = "integer", description = "Codigo HTTP")
        int status,
        @Schema(type = "string", description = "Nome do erro HTTP")
        String error,
        @Schema(type = "string", description = "Mensagem principal do erro")
        String message,
        @Schema(type = "object", description = "Erros de validacao por campo")
        Map<String, String> fieldErrors
) {
}
