package dev.matheus.gameVault.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record JogoEstatisticasResponse(
        @Schema(type = "integer", description = "Total de jogos do usuario")
        int totalJogos,
        @Schema(type = "integer", description = "Total de generos cadastrados")
        int totalGeneros,
        @Schema(type = "integer", description = "Total de plataformas cadastradas")
        int totalPlataformas,
        @Schema(type = "number", description = "Media de notas da biblioteca")
        double mediaNota,
        @Schema(type = "integer", description = "Total de jogos favoritos")
        int totalFavoritos,
        @Schema(type = "integer", description = "Total de horas jogadas")
        int totalHoras,
        @Schema(type = "array", description = "Distribuicao de jogos por plataforma")
        List<EstatisticaItemResponse> jogosPorPlataforma,
        @Schema(type = "array", description = "Distribuicao de jogos por status")
        List<EstatisticaItemResponse> jogosPorStatus,
        @Schema(type = "array", description = "Top jogos por nota")
        List<EstatisticaItemResponse> topJogos
) {
}
