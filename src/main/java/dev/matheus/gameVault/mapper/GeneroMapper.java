package dev.matheus.gameVault.mapper;

import dev.matheus.gameVault.controller.request.GeneroRequest;
import dev.matheus.gameVault.controller.response.GeneroResponse;
import dev.matheus.gameVault.entity.Genero;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GeneroMapper {
    public static Genero toGenero(GeneroRequest request) {
        return Genero.builder()
                .nome(request.nome())
                .build();
    }

    public static GeneroResponse toGeneroResponse(Genero genero) {
        return GeneroResponse.builder()
                .id(genero.getId())
                .nome(genero.getNome())
                .build();
    }
}