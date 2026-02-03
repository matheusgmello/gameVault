package dev.matheus.gameVault.mapper;

import dev.matheus.gameVault.controller.request.PlataformaRequest;
import dev.matheus.gameVault.controller.response.PlataformaResponse;
import dev.matheus.gameVault.entity.Plataforma;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlataformaMapper {
    public static Plataforma toPlataforma(PlataformaRequest request) {
        return Plataforma.builder()
                .nome(request.nome())
                .build();
    }

    public static PlataformaResponse toPlataformaResponse(Plataforma plataforma) {
        return PlataformaResponse.builder()
                .id(plataforma.getId())
                .nome(plataforma.getNome())
                .build();
    }
}