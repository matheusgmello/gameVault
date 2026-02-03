package dev.matheus.gameVault.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UsuarioResponse(
        @Schema(type = "integer", format = "int64", description = "ID do Usuário", example = "10")
        Long id,
        @Schema(type = "string", description = "Nome completo do usuário", example = "João Silva")
        String nome,
        @Schema(type = "string", description = "E-mail cadastrado", example = "joao@email.com")
        String email) {
}