package dev.matheus.gameVault.mapper;

import dev.matheus.gameVault.controller.request.JogoRequest;
import dev.matheus.gameVault.controller.response.GeneroResponse;
import dev.matheus.gameVault.controller.response.JogoResponse;
import dev.matheus.gameVault.controller.response.PlataformaResponse;
import dev.matheus.gameVault.entity.Genero;
import dev.matheus.gameVault.entity.Jogo;
import dev.matheus.gameVault.entity.Plataforma;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class JogoMapper {

    public static Jogo toJogo(JogoRequest request) {
        // Mapeia IDs de gêneros para entidades Genero
        Set<Genero> generos = request.generos().stream()
                .map(id -> Genero.builder().id(id).build())
                .collect(Collectors.toSet());

        // Mapeia IDs de plataformas para entidades Plataforma
        Set<Plataforma> plataformas = request.plataformas().stream()
                .map(id -> Plataforma.builder().id(id).build())
                .collect(Collectors.toSet());

        return Jogo.builder()
                .titulo(request.titulo())
                .descricao(request.descricao())
                .dataLancamento(request.dataLancamento())
                .nota(request.nota())
                .generos(generos)
                .plataformas(plataformas)
                .build();
    }

    public static JogoResponse toJogoResponse(Jogo jogo) {
        // Converte as entidades internas para seus respectivos Responses
        List<GeneroResponse> generos = jogo.getGeneros().stream()
                .map(GeneroMapper::toGeneroResponse)
                .toList();

        List<PlataformaResponse> plataformas = jogo.getPlataformas().stream()
                .map(PlataformaMapper::toPlataformaResponse)
                .toList();

        return JogoResponse.builder()
                .id(jogo.getId())
                .titulo(jogo.getTitulo())
                .descricao(jogo.getDescricao())
                .dataLancamento(jogo.getDataLancamento())
                .nota(jogo.getNota())
                .generos(generos)
                .plataformas(plataformas)
                .build();
    }
}