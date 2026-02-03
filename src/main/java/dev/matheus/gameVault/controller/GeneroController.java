package dev.matheus.gameVault.controller;

import dev.matheus.gameVault.controller.request.GeneroRequest;
import dev.matheus.gameVault.controller.response.GeneroResponse;
import dev.matheus.gameVault.entity.Genero;
import dev.matheus.gameVault.mapper.GeneroMapper;
import dev.matheus.gameVault.service.GeneroService;
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
@RequestMapping("/gamevault/genero")
@RequiredArgsConstructor
@Tag(name = "Gênero", description = "Gerenciamento dos gêneros de jogos")
public class GeneroController {

    private final GeneroService generoService;

    @Operation(summary = "Listar Gêneros", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<List<GeneroResponse>> buscarTodos() {
        List<GeneroResponse> generos = generoService.buscarTodos()
                .stream()
                .map(GeneroMapper::toGeneroResponse)
                .toList();
        return ResponseEntity.ok(generos);
    }

    @Operation(summary = "Salvar Gênero", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<GeneroResponse> salvar(@Valid @RequestBody GeneroRequest request) {
        Genero generoSalvo = generoService.salvar(GeneroMapper.toGenero(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(GeneroMapper.toGeneroResponse(generoSalvo));
    }

    @Operation(summary = "Buscar Gênero por ID", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    public ResponseEntity<GeneroResponse> buscarPorId(@PathVariable Long id) {
        return generoService.buscarPorId(id)
                .map(genero -> ResponseEntity.ok(GeneroMapper.toGeneroResponse(genero)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Deletar Gênero", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        generoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}