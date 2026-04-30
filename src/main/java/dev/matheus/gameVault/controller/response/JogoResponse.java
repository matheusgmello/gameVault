package dev.matheus.gameVault.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.matheus.gameVault.entity.JogoStatus;
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
        @Schema(type = "string", format = "yyyy-MM-dd", description = "Data de lançamento do Jogo")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate dataLancamento,
        @Schema(type = "string", description = "URL da capa do jogo")
        String capaUrl,
        @Schema(type = "number", format = "double", description = "Avaliação do Jogo")
        double nota,
        @Schema(type = "string", description = "Status pessoal do jogo na biblioteca")
        JogoStatus status,
        @Schema(type = "boolean", description = "Indica se o jogo esta marcado como favorito")
        Boolean favorito,
        @Schema(type = "string", description = "Review pessoal do usuario sobre o jogo")
        String review,
        @Schema(type = "integer", description = "Quantidade de horas jogadas")
        Integer horasJogadas,
        @Schema(type = "array", description = "Lista de Gêneros do Jogo")
        List<GeneroResponse> generos,
        @Schema(type = "array", description = "Lista de Plataformas do Jogo")
        List<PlataformaResponse> plataformas) {
}
