package dev.matheus.gameVault.service;

import dev.matheus.gameVault.entity.Genero;
import dev.matheus.gameVault.entity.Jogo;
import dev.matheus.gameVault.entity.Plataforma;
import dev.matheus.gameVault.repository.JogoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JogoServiceTest {

    @Mock
    private JogoRepository jogoRepository;

    @Mock
    private GeneroService generoService;

    @Mock
    private PlataformaService plataformaService;

    @InjectMocks
    private JogoService jogoService;

    private Jogo jogo;
    private Genero genero;
    private Plataforma plataforma;

    @BeforeEach
    void setUp() {
        genero = Genero.builder().id(1L).nome("Ação").build();
        plataforma = Plataforma.builder().id(1L).nome("PC").build();

        jogo = Jogo.builder()
                .id(1L)
                .titulo("Elden Ring")
                .descricao("RPG de Ação")
                .dataLancamento(LocalDate.now())
                .nota(9.5)
                .generos(new HashSet<>(Collections.singletonList(genero)))
                .plataformas(new HashSet<>(Collections.singletonList(plataforma)))
                .build();
    }

    @Test
    @DisplayName("Deve salvar um jogo com sucesso")
    void deveSalvarJogoComSucesso() {
        when(generoService.buscarPorId(anyLong())).thenReturn(Optional.of(genero));
        when(plataformaService.buscarPorId(anyLong())).thenReturn(Optional.of(plataforma));
        when(jogoRepository.save(any(Jogo.class))).thenReturn(jogo);

        Jogo resultado = jogoService.salvar(jogo);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTitulo()).isEqualTo("Elden Ring");
        verify(jogoRepository, times(1)).save(any(Jogo.class));
    }

    @Test
    @DisplayName("Deve buscar todos os jogos")
    void deveBuscarTodosOsJogos() {
        when(jogoRepository.findAll()).thenReturn(Collections.singletonList(jogo));

        List<Jogo> resultado = jogoService.buscarTodos();

        assertThat(resultado).isNotEmpty().hasSize(1);
        verify(jogoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar jogo por ID")
    void deveBuscarJogoPorId() {
        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));

        Optional<Jogo> resultado = jogoService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getTitulo()).isEqualTo("Elden Ring");
    }

    @Test
    @DisplayName("Deve atualizar um jogo")
    void deveAtualizarJogo() {
        Jogo jogoAtualizado = Jogo.builder()
                .titulo("Elden Ring Shadow of the Erdtree")
                .descricao("Expansão")
                .dataLancamento(LocalDate.now())
                .nota(10.0)
                .generos(new HashSet<>(Collections.singletonList(genero)))
                .plataformas(new HashSet<>(Collections.singletonList(plataforma)))
                .build();

        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));
        when(generoService.buscarPorId(anyLong())).thenReturn(Optional.of(genero));
        when(plataformaService.buscarPorId(anyLong())).thenReturn(Optional.of(plataforma));
        when(jogoRepository.save(any(Jogo.class))).thenReturn(jogo);

        Optional<Jogo> resultado = jogoService.atualizar(1L, jogoAtualizado);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getTitulo()).isEqualTo("Elden Ring Shadow of the Erdtree");
        verify(jogoRepository, times(1)).save(any(Jogo.class));
    }

    @Test
    @DisplayName("Deve deletar um jogo")
    void deveDeletarJogo() {
        doNothing().when(jogoRepository).deleteById(1L);

        jogoService.deletar(1L);

        verify(jogoRepository, times(1)).deleteById(1L);
    }
}
