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
    @DisplayName("Deve retornar 403 ao tentar acessar sem token")
    void deveRetornar403SemToken() throws Exception {
        mockMvc.perform(get("/gamevault/jogo"))
                .andExpect(status().isForbidden());
    }
}
