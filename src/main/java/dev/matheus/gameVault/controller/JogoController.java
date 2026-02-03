package dev.matheus.gameVault.controller;

import dev.matheus.gameVault.controller.request.JogoRequest;
import dev.matheus.gameVault.controller.response.JogoResponse;
import dev.matheus.gameVault.entity.Jogo;
import dev.matheus.gameVault.mapper.JogoMapper;
import dev.matheus.gameVault.service.JogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gamevault/jogo")
@RequiredArgsConstructor
@Tag(name = "Jogo", description = "Gerenciamento do catálogo de jogos")
public class JogoController {

    private final JogoService jogoService;

    @Operation(summary = "Salvar Jogo", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<JogoResponse> salvar(@Valid @RequestBody JogoRequest request) {
        Jogo jogoSalvo = jogoService.salvar(JogoMapper.toJogo(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(JogoMapper.toJogoResponse(jogoSalvo));
    }

    @Operation(summary = "Listar todos os Jogos", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<List<JogoResponse>> buscarTodos() {
        return ResponseEntity.ok(jogoService.buscarTodos().stream()
                .map(JogoMapper::toJogoResponse).toList());
    }

    @Operation(summary = "Atualizar Jogo", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public ResponseEntity<JogoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody JogoRequest request) {
        return jogoService.atualizar(id, JogoMapper.toJogo(request))
                .map(jogo -> ResponseEntity.ok(JogoMapper.toJogoResponse(jogo)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar Jogos por Gênero", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/busca")
    public ResponseEntity<List<JogoResponse>> buscarPorGenero(@RequestParam Long generoId) {
        return ResponseEntity.ok(jogoService.buscarPorGenero(generoId).stream()
                .map(JogoMapper::toJogoResponse).toList());
    }

    @Operation(summary = "Deletar Jogo", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return jogoService.buscarPorId(id)
                .map(j -> {
                    jogoService.deletar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}