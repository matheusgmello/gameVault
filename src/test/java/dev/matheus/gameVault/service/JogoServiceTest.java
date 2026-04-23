package dev.matheus.gameVault.service;

import dev.matheus.gameVault.entity.Genero;
import dev.matheus.gameVault.entity.Jogo;
import dev.matheus.gameVault.entity.JogoStatus;
import dev.matheus.gameVault.entity.Plataforma;
import dev.matheus.gameVault.entity.Usuario;
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
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        genero = Genero.builder().id(1L).nome("Ação").build();
        plataforma = Plataforma.builder().id(1L).nome("PC").build();
        usuario = Usuario.builder().id(1L).nome("Player").email("player@test.com").build();

        jogo = Jogo.builder()
                .id(1L)
                .titulo("Elden Ring")
                .descricao("RPG de Ação")
                .dataLancamento(LocalDate.now())
                .nota(9.5)
                .status(JogoStatus.JOGANDO)
                .favorito(true)
                .review("Uma jornada excelente.")
                .horasJogadas(80)
                .usuario(usuario)
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

        Jogo resultado = jogoService.salvar(jogo, usuario.getId());

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTitulo()).isEqualTo("Elden Ring");
        assertThat(jogo.getUsuario().getId()).isEqualTo(usuario.getId());
        assertThat(resultado.getStatus()).isEqualTo(JogoStatus.JOGANDO);
        assertThat(resultado.getFavorito()).isTrue();
        assertThat(resultado.getHorasJogadas()).isEqualTo(80);
        verify(jogoRepository, times(1)).save(any(Jogo.class));
    }

    @Test
    @DisplayName("Deve buscar todos os jogos")
    void deveBuscarTodosOsJogos() {
        when(jogoRepository.findByUsuarioId(usuario.getId())).thenReturn(Collections.singletonList(jogo));

        List<Jogo> resultado = jogoService.buscarTodos(usuario.getId());

        assertThat(resultado).isNotEmpty().hasSize(1);
        verify(jogoRepository, times(1)).findByUsuarioId(usuario.getId());
    }

    @Test
    @DisplayName("Deve buscar jogo por ID")
    void deveBuscarJogoPorId() {
        when(jogoRepository.findByIdAndUsuarioId(1L, usuario.getId())).thenReturn(Optional.of(jogo));

        Optional<Jogo> resultado = jogoService.buscarPorId(1L, usuario.getId());

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
                .status(JogoStatus.ZERADO)
                .favorito(false)
                .review("DLC finalizada.")
                .horasJogadas(100)
                .usuario(usuario)
                .generos(new HashSet<>(Collections.singletonList(genero)))
                .plataformas(new HashSet<>(Collections.singletonList(plataforma)))
                .build();

        when(jogoRepository.findByIdAndUsuarioId(1L, usuario.getId())).thenReturn(Optional.of(jogo));
        when(generoService.buscarPorId(anyLong())).thenReturn(Optional.of(genero));
        when(plataformaService.buscarPorId(anyLong())).thenReturn(Optional.of(plataforma));
        when(jogoRepository.save(any(Jogo.class))).thenReturn(jogo);

        Optional<Jogo> resultado = jogoService.atualizar(1L, usuario.getId(), jogoAtualizado);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getTitulo()).isEqualTo("Elden Ring Shadow of the Erdtree");
        assertThat(resultado.get().getStatus()).isEqualTo(JogoStatus.ZERADO);
        assertThat(resultado.get().getFavorito()).isFalse();
        assertThat(resultado.get().getReview()).isEqualTo("DLC finalizada.");
        assertThat(resultado.get().getHorasJogadas()).isEqualTo(100);
        verify(jogoRepository, times(1)).save(any(Jogo.class));
    }

    @Test
    @DisplayName("Deve deletar um jogo")
    void deveDeletarJogo() {
        when(jogoRepository.findByIdAndUsuarioId(1L, usuario.getId())).thenReturn(Optional.of(jogo));

        jogoService.deletar(1L, usuario.getId());

        verify(jogoRepository, times(1)).delete(jogo);
    }
}
