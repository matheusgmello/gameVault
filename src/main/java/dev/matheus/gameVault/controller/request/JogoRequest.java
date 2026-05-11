package dev.matheus.gameVault.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.matheus.gameVault.entity.JogoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record JogoRequest(
        @Schema(type = "string", description = "Titulo do jogo")
        @NotBlank(message = "O titulo do jogo e obrigatorio")
        @Size(max = 255, message = "O titulo do jogo deve ter no maximo 255 caracteres")
        String titulo,

        @Schema(type = "string", description = "Descricao do jogo")
        @Size(max = 2000, message = "A descricao deve ter no maximo 2000 caracteres")
        String descricao,

        @Schema(type = "string", format = "yyyy-MM-dd", description = "Data de lancamento do jogo")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @NotNull(message = "A data de lancamento e obrigatoria")
        LocalDate dataLancamento,

        @Schema(type = "string", description = "URL da capa do jogo")
        @Size(max = 1000, message = "A URL da capa deve ter no maximo 1000 caracteres")
        String capaUrl,

        @Schema(type = "number", format = "double", description = "Avaliacao do jogo")
        @DecimalMin(value = "0.0", message = "A nota deve ser maior ou igual a 0")
        @DecimalMax(value = "10.0", message = "A nota deve ser menor ou igual a 10")
        double nota,

        @Schema(type = "string", description = "Status pessoal do jogo na biblioteca")
        JogoStatus status,

        @Schema(type = "boolean", description = "Indica se o jogo esta marcado como favorito")
        Boolean favorito,

        @Schema(type = "string", description = "Review pessoal do usuario sobre o jogo")
        @Size(max = 1200, message = "A review deve ter no maximo 1200 caracteres")
        String review,

        @Schema(type = "integer", description = "Quantidade de horas jogadas")
        @Min(value = 0, message = "As horas jogadas nao podem ser negativas")
        Integer horasJogadas,

        @Schema(type = "array", description = "Lista de IDs dos generos do jogo")
        @NotEmpty(message = "Selecione pelo menos um genero")
        List<Long> generos,

        @Schema(type = "array", description = "Lista de IDs das plataformas onde o jogo esta disponivel")
        @NotEmpty(message = "Selecione pelo menos uma plataforma")
        List<Long> plataformas) {
}
