package dev.matheus.gameVault.service;

import dev.matheus.gameVault.entity.Usuario;
import dev.matheus.gameVault.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve salvar um usuário com senha criptografada")
    void deveSalvarUsuarioComSucesso() {
        Usuario usuario = Usuario.builder()
                .email("test@test.com")
                .senha("senha123")
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn("senhaCripto");
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.salvar(usuario);

        assertThat(resultado).isNotNull();
        assertThat(usuario.getSenha()).isEqualTo("senhaCripto");
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(repository, times(1)).save(any(Usuario.class));
    }
}
