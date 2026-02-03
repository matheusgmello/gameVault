package dev.matheus.gameVault.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.List;

public record JogoRequest(
        @Schema(type = "string", description = "Título do Jogo")
        @NotEmpty(message = "O título do jogo é obrigatório")
        String titulo,

        @Schema(type = "string", description = "Descrição do Jogo")
        String descricao,

        @Schema(type = "string", format = "dd/MM/yyyy", description = "Data de lançamento do jogo")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate dataLancamento,

        @Schema(type = "number", format = "double", description = "Avaliação do jogo (nota)")
        double nota,

        @Schema(type = "array", description = "Lista de IDs dos Gêneros do Jogo")
        List<Long> generos,

        @Schema(type = "array", description = "Lista de IDs das Plataformas onde o jogo está disponível")
        List<Long> plataformas) {
}