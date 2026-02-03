package dev.matheus.gameVault.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record JogoResponse(
        @Schema(type = "integer", format = "int64", description = "ID do Jogo")
        Long id,
        @Schema(type = "string", description = "Título do Jogo")
        String titulo,
        @Schema(type = "string", description = "Descrição do Jogo")
        String descricao,
        @Schema(type = "string", format = "dd/MM/yyyy", description = "Data de lançamento do Jogo")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate dataLancamento,
        @Schema(type = "number", format = "double", description = "Avaliação do Jogo")
        double nota,
        @Schema(type = "array", description = "Lista de Gêneros do Jogo")
        List<GeneroResponse> generos,
        @Schema(type = "array", description = "Lista de Plataformas do Jogo")
        List<PlataformaResponse> plataformas) {
}