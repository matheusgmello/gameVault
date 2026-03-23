package dev.matheus.gameVault.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.matheus.gameVault.config.TokenService;
import dev.matheus.gameVault.controller.request.JogoRequest;
import dev.matheus.gameVault.entity.Genero;
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

        Usuario usuario = Usuario.builder()
                .nome("Admin")
                .email("admin@test.com")
                .senha("senha")
                .build();
        usuarioRepository.save(usuario);

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
                9.8,
                java.util.List.of(genero.getId()),
                java.util.List.of(plataforma.getId())
        );

        mockMvc.perform(post("/gamevault/jogo")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Final Fantasy VII Rebirth"))
                .andExpect(jsonPath("$.nota").value(9.8));
    }

    @Test
    @DisplayName("Deve listar todos os jogos via API")
    void deveListarJogos() throws Exception {
        mockMvc.perform(get("/gamevault/jogo")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar acessar sem token")
    void deveRetornar403SemToken() throws Exception {
        mockMvc.perform(get("/gamevault/jogo"))
                .andExpect(status().isForbidden());
    }
}
