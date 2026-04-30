package dev.matheus.gameVault.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.matheus.gameVault.entity.JogoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.List;

public record JogoRequest(
        @Schema(type = "string", description = "Título do Jogo")
        @NotEmpty(message = "O título do jogo é obrigatório")
        String titulo,

        @Schema(type = "string", description = "Descrição do Jogo")
        String descricao,

        @Schema(type = "string", format = "yyyy-MM-dd", description = "Data de lançamento do jogo")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate dataLancamento,

        @Schema(type = "string", description = "URL da capa do jogo")
        String capaUrl,

        @Schema(type = "number", format = "double", description = "Avaliação do jogo (nota)")
        double nota,

        @Schema(type = "string", description = "Status pessoal do jogo na biblioteca")
        JogoStatus status,

        @Schema(type = "boolean", description = "Indica se o jogo esta marcado como favorito")
        Boolean favorito,

        @Schema(type = "string", description = "Review pessoal do usuario sobre o jogo")
        String review,

        @Schema(type = "integer", description = "Quantidade de horas jogadas")
        @Min(value = 0, message = "As horas jogadas nao podem ser negativas")
        Integer horasJogadas,

        @Schema(type = "array", description = "Lista de IDs dos Gêneros do Jogo")
        List<Long> generos,

        @Schema(type = "array", description = "Lista de IDs das Plataformas onde o jogo está disponível")
        List<Long> plataformas) {
}
