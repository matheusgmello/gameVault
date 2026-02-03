package dev.matheus.gameVault.mapper;

import dev.matheus.gameVault.controller.request.UsuarioRequest;
import dev.matheus.gameVault.controller.response.UsuarioResponse;
import dev.matheus.gameVault.entity.Usuario;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UsuarioMapper {
    public static Usuario toUsuario(UsuarioRequest request) {
        return Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(request.senha())
                .build();
    }

    public static UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .build();
    }
}