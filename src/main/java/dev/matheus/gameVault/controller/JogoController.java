package dev.matheus.gameVault.controller;

import dev.matheus.gameVault.config.JWTUserData;
import dev.matheus.gameVault.controller.request.JogoRequest;
import dev.matheus.gameVault.controller.response.JogoResponse;
import dev.matheus.gameVault.entity.Jogo;
import dev.matheus.gameVault.entity.JogoStatus;
import dev.matheus.gameVault.mapper.JogoMapper;
import dev.matheus.gameVault.service.JogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<JogoResponse> salvar(@AuthenticationPrincipal JWTUserData usuario,
                                               @Valid @RequestBody JogoRequest request) {
        Jogo jogoSalvo = jogoService.salvar(JogoMapper.toJogo(request), usuario.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(JogoMapper.toJogoResponse(jogoSalvo));
    }

    @Operation(summary = "Listar todos os Jogos", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<List<JogoResponse>> buscarTodos(@AuthenticationPrincipal JWTUserData usuario,
                                                          @RequestParam(required = false) String busca,
                                                          @RequestParam(required = false) JogoStatus status,
                                                          @RequestParam(required = false) Long generoId,
                                                          @RequestParam(required = false) Long plataformaId,
                                                          @RequestParam(required = false) Boolean favorito,
                                                          @RequestParam(required = false) String ordenarPor) {
        return ResponseEntity.ok(jogoService.buscarComFiltros(
                        usuario.id(),
                        busca,
                        status,
                        generoId,
                        plataformaId,
                        favorito,
                        ordenarPor
                ).stream()
                .map(JogoMapper::toJogoResponse).toList());
    }

    @Operation(summary = "Buscar Jogo por ID", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    public ResponseEntity<JogoResponse> buscarPorId(@AuthenticationPrincipal JWTUserData usuario,
                                                    @PathVariable Long id) {
        return jogoService.buscarPorId(id, usuario.id())
                .map(jogo -> ResponseEntity.ok(JogoMapper.toJogoResponse(jogo)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualizar Jogo", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public ResponseEntity<JogoResponse> atualizar(@AuthenticationPrincipal JWTUserData usuario,
                                                  @PathVariable Long id,
                                                  @Valid @RequestBody JogoRequest request) {
        return jogoService.atualizar(id, usuario.id(), JogoMapper.toJogo(request))
                .map(jogo -> ResponseEntity.ok(JogoMapper.toJogoResponse(jogo)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar Jogos por Gênero", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/busca")
    public ResponseEntity<List<JogoResponse>> buscarPorGenero(@AuthenticationPrincipal JWTUserData usuario,
                                                              @RequestParam Long generoId) {
        return ResponseEntity.ok(jogoService.buscarPorGenero(generoId, usuario.id()).stream()
                .map(JogoMapper::toJogoResponse).toList());
    }

    @Operation(summary = "Deletar Jogo", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@AuthenticationPrincipal JWTUserData usuario,
                                        @PathVariable Long id) {
        return jogoService.buscarPorId(id, usuario.id())
                .map(j -> {
                    jogoService.deletar(id, usuario.id());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
