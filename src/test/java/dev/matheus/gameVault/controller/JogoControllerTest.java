package dev.matheus.gameVault.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.matheus.gameVault.config.TokenService;
import dev.matheus.gameVault.controller.request.JogoRequest;
import dev.matheus.gameVault.entity.Genero;
import dev.matheus.gameVault.entity.Jogo;
import dev.matheus.gameVault.entity.JogoStatus;
import dev.matheus.gameVault.entity.Plataforma;
import dev.matheus.gameVault.entity.Usuario;
import dev.matheus.gameVault.repository.GeneroRepository;
import dev.matheus.gameVault.repository.JogoRepository;
import dev.matheus.gameVault.repository.PlataformaRepository;
import dev.matheus.gameVault.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.boot.test.autoconfigure.json.AutoConfigureJson
@ActiveProfiles("test")
class JogoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JogoRepository jogoRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private PlataformaRepository plataformaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    private ObjectMapper objectMapper;

    private String token;
    private Usuario usuario;
    private Usuario outroUsuario;
    private Genero genero;
    private Plataforma plataforma;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        
        jogoRepository.deleteAll();
        generoRepository.deleteAll();
        plataformaRepository.deleteAll();
        usuarioRepository.deleteAll();

        usuario = Usuario.builder()
                .nome("Admin")
                .email("admin@test.com")
                .senha("senha")
                .build();
        usuario = usuarioRepository.save(usuario);

        outroUsuario = Usuario.builder()
                .nome("Outro Jogador")
                .email("outro@test.com")
                .senha("senha")
                .build();
        outroUsuario = usuarioRepository.save(outroUsuario);

        token = tokenService.gerarToken(usuario);

        genero = Genero.builder().nome("RPG").build();
        genero = generoRepository.save(genero);

        plataforma = Plataforma.builder().nome("PS5").build();
        plataforma = plataformaRepository.save(plataforma);
    }

    @Test
    @DisplayName("Deve salvar um novo jogo via API")
    void deveSalvarNovoJogo() throws Exception {
        JogoRequest request = new JogoRequest(
                "Final Fantasy VII Rebirth",
                "Incrível RPG",
                LocalDate.now(),
                "https://example.com/ff7.jpg",
                9.8,
                JogoStatus.JOGANDO,
                true,
                "Combate excelente e mundo muito rico.",
                42,
                java.util.List.of(genero.getId()),
                java.util.List.of(plataforma.getId())
        );

        mockMvc.perform(post("/gamevault/jogo")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Final Fantasy VII Rebirth"))
                .andExpect(jsonPath("$.capaUrl").value("https://example.com/ff7.jpg"))
                .andExpect(jsonPath("$.nota").value(9.8))
                .andExpect(jsonPath("$.status").value("JOGANDO"))
                .andExpect(jsonPath("$.favorito").value(true))
                .andExpect(jsonPath("$.horasJogadas").value(42));
    }

    @Test
    @DisplayName("Deve listar todos os jogos via API")
    void deveListarJogos() throws Exception {
        mockMvc.perform(get("/gamevault/jogo")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve listar apenas jogos do usuario autenticado")
    void deveListarApenasJogosDoUsuarioAutenticado() throws Exception {
        Jogo jogoOutroUsuario = Jogo.builder()
                .titulo("Jogo Privado")
                .descricao("Nao deve aparecer para outro usuario")
                .dataLancamento(LocalDate.now())
                .nota(8.5)
                .status(JogoStatus.WISHLIST)
                .favorito(false)
                .horasJogadas(0)
                .usuario(outroUsuario)
                .generos(Set.of(genero))
                .plataformas(Set.of(plataforma))
                .build();
        jogoRepository.save(jogoOutroUsuario);

        mockMvc.perform(get("/gamevault/jogo")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Deve filtrar jogos do usuario autenticado")
    void deveFiltrarJogosDoUsuarioAutenticado() throws Exception {
        Jogo jogoFavorito = Jogo.builder()
                .titulo("Hades")
                .descricao("Roguelike de acao")
                .dataLancamento(LocalDate.of(2020, 9, 17))
                .nota(9.4)
                .status(JogoStatus.ZERADO)
                .favorito(true)
                .horasJogadas(48)
                .usuario(usuario)
                .generos(Set.of(genero))
                .plataformas(Set.of(plataforma))
                .build();
        jogoRepository.save(jogoFavorito);

        Jogo jogoOutroStatus = Jogo.builder()
                .titulo("Stardew Valley")
                .descricao("Fazenda e rotina")
                .dataLancamento(LocalDate.of(2016, 2, 26))
                .nota(8.9)
                .status(JogoStatus.JOGANDO)
                .favorito(false)
                .horasJogadas(88)
                .usuario(usuario)
                .generos(Set.of(genero))
                .plataformas(Set.of(plataforma))
                .build();
        jogoRepository.save(jogoOutroStatus);

        mockMvc.perform(get("/gamevault/jogo")
                .header("Authorization", "Bearer " + token)
                .param("status", "ZERADO")
                .param("favorito", "true")
                .param("busca", "had")
                .param("ordenarPor", "nota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Hades"))
                .andExpect(jsonPath("$[0].status").value("ZERADO"))
                .andExpect(jsonPath("$[0].favorito").value(true));
    }

    @Test
    @DisplayName("Deve buscar detalhe de jogo apenas do usuario autenticado")
    void deveBuscarDetalheDeJogoApenasDoUsuarioAutenticado() throws Exception {
        Jogo jogo = Jogo.builder()
                .titulo("Elden Ring")
                .descricao("RPG de mundo aberto")
                .dataLancamento(LocalDate.of(2022, 2, 25))
                .nota(10.0)
                .status(JogoStatus.ZERADO)
                .favorito(true)
                .horasJogadas(126)
                .usuario(usuario)
                .generos(Set.of(genero))
                .plataformas(Set.of(plataforma))
                .build();
        jogo = jogoRepository.save(jogo);

        Jogo jogoOutroUsuario = Jogo.builder()
                .titulo("Jogo Privado")
                .descricao("Nao deve abrir para outro usuario")
                .dataLancamento(LocalDate.now())
                .nota(7.0)
                .status(JogoStatus.WISHLIST)
                .favorito(false)
                .horasJogadas(0)
                .usuario(outroUsuario)
                .generos(Set.of(genero))
                .plataformas(Set.of(plataforma))
                .build();
        jogoOutroUsuario = jogoRepository.save(jogoOutroUsuario);

        mockMvc.perform(get("/gamevault/jogo/{id}", jogo.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Elden Ring"))
                .andExpect(jsonPath("$.descricao").value("RPG de mundo aberto"));

        mockMvc.perform(get("/gamevault/jogo/{id}", jogoOutroUsuario.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar estatisticas apenas do usuario autenticado")
    void deveRetornarEstatisticasApenasDoUsuarioAutenticado() throws Exception {
        Jogo jogo = Jogo.builder()
                .titulo("Elden Ring")
                .descricao("RPG de mundo aberto")
                .dataLancamento(LocalDate.of(2022, 2, 25))
                .nota(10.0)
                .status(JogoStatus.ZERADO)
                .favorito(true)
                .horasJogadas(126)
                .usuario(usuario)
                .generos(Set.of(genero))
                .plataformas(Set.of(plataforma))
                .build();
        jogoRepository.save(jogo);

        Jogo jogoOutroUsuario = Jogo.builder()
                .titulo("Jogo Privado")
                .descricao("Nao deve contar para outro usuario")
                .dataLancamento(LocalDate.now())
                .nota(5.0)
                .status(JogoStatus.JOGANDO)
                .favorito(false)
                .horasJogadas(20)
                .usuario(outroUsuario)
                .generos(Set.of(genero))
                .plataformas(Set.of(plataforma))
                .build();
        jogoRepository.save(jogoOutroUsuario);

        mockMvc.perform(get("/gamevault/jogo/estatisticas")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalJogos").value(1))
                .andExpect(jsonPath("$.totalFavoritos").value(1))
                .andExpect(jsonPath("$.totalHoras").value(126))
                .andExpect(jsonPath("$.mediaNota").value(10.0))
                .andExpect(jsonPath("$.jogosPorPlataforma[0].name").value("PS5"))
                .andExpect(jsonPath("$.jogosPorStatus[0].name").value("ZERADO"))
                .andExpect(jsonPath("$.topJogos[0].name").value("Elden Ring"));
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar acessar sem token")
    void deveRetornar403SemToken() throws Exception {
        mockMvc.perform(get("/gamevault/jogo"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar erros de validacao padronizados ao salvar jogo invalido")
    void deveRetornarErrosDeValidacaoPadronizadosAoSalvarJogoInvalido() throws Exception {
        JogoRequest request = new JogoRequest(
                "",
                "Descricao qualquer",
                LocalDate.now(),
                null,
                12.0,
                JogoStatus.JOGANDO,
                false,
                "Review",
                -2,
                java.util.List.of(),
                java.util.List.of()
        );

        mockMvc.perform(post("/gamevault/jogo")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Existem campos invalidos na requisicao."))
                .andExpect(jsonPath("$.fieldErrors.titulo").exists())
                .andExpect(jsonPath("$.fieldErrors.nota").exists())
                .andExpect(jsonPath("$.fieldErrors.horasJogadas").exists())
                .andExpect(jsonPath("$.fieldErrors.generos").exists())
                .andExpect(jsonPath("$.fieldErrors.plataformas").exists());
    }
}
