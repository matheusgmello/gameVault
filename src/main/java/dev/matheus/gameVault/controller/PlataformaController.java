package dev.matheus.gameVault.controller;

import dev.matheus.gameVault.controller.request.PlataformaRequest;
import dev.matheus.gameVault.controller.response.PlataformaResponse;
import dev.matheus.gameVault.entity.Plataforma;
import dev.matheus.gameVault.mapper.PlataformaMapper;
import dev.matheus.gameVault.service.PlataformaService;
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
@RequestMapping("/gamevault/plataforma")
@RequiredArgsConstructor
@Tag(name = "Plataforma", description = "Gerenciamento de plataformas de jogos")
public class PlataformaController {

    private final PlataformaService plataformaService;

    @Operation(summary = "Listar Plataformas", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<List<PlataformaResponse>> buscarTodas() {
        List<PlataformaResponse> plataformas = plataformaService.buscarTodos()
                .stream()
                .map(PlataformaMapper::toPlataformaResponse)
                .toList();
        return ResponseEntity.ok(plataformas);
    }

    @Operation(summary = "Salvar Plataforma", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<PlataformaResponse> salvar(@Valid @RequestBody PlataformaRequest request) {
        Plataforma plataformaSalva = plataformaService.salvar(PlataformaMapper.toPlataforma(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(PlataformaMapper.toPlataformaResponse(plataformaSalva));
    }

    @Operation(summary = "Deletar Plataforma", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        plataformaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}