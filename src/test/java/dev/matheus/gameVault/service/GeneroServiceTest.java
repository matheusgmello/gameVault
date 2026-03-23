package dev.matheus.gameVault.service;

import dev.matheus.gameVault.entity.Genero;
import dev.matheus.gameVault.repository.GeneroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeneroServiceTest {

    @Mock
    private GeneroRepository repository;

    @InjectMocks
    private GeneroService generoService;

    @Test
    @DisplayName("Deve salvar um gênero")
    void deveSalvarGenero() {
        Genero genero = Genero.builder().nome("Ação").build();
        when(repository.save(any(Genero.class))).thenReturn(genero);

        Genero resultado = generoService.salvar(genero);

        assertThat(resultado.getNome()).isEqualTo("Ação");
        verify(repository, times(1)).save(any(Genero.class));
    }

    @Test
    @DisplayName("Deve buscar todos os gêneros")
    void deveBuscarTodos() {
        when(repository.findAll()).thenReturn(Collections.singletonList(new Genero()));

        List<Genero> resultado = generoService.buscarTodos();

        assertThat(resultado).hasSize(1);
        verify(repository, times(1)).findAll();
    }
}
