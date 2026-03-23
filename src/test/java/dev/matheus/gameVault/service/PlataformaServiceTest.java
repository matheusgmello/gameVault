package dev.matheus.gameVault.service;

import dev.matheus.gameVault.entity.Plataforma;
import dev.matheus.gameVault.repository.PlataformaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlataformaServiceTest {

    @Mock
    private PlataformaRepository repository;

    @InjectMocks
    private PlataformaService plataformaService;

    @Test
    @DisplayName("Deve salvar uma plataforma")
    void deveSalvarPlataforma() {
        Plataforma plataforma = Plataforma.builder().nome("PC").build();
        when(repository.save(any(Plataforma.class))).thenReturn(plataforma);

        Plataforma resultado = plataformaService.salvar(plataforma);

        assertThat(resultado.getNome()).isEqualTo("PC");
        verify(repository, times(1)).save(any(Plataforma.class));
    }

    @Test
    @DisplayName("Deve buscar todas as plataformas")
    void deveBuscarTodas() {
        when(repository.findAll()).thenReturn(Collections.singletonList(new Plataforma()));

        List<Plataforma> resultado = plataformaService.buscarTodos();

        assertThat(resultado).hasSize(1);
        verify(repository, times(1)).findAll();
    }
}
