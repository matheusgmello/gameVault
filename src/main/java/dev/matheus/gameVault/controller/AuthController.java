package dev.matheus.gameVault.controller;

import dev.matheus.gameVault.config.TokenService;
import dev.matheus.gameVault.controller.request.LoginRequest;
import dev.matheus.gameVault.controller.request.UsuarioRequest;
import dev.matheus.gameVault.controller.response.LoginResponse;
import dev.matheus.gameVault.controller.response.UsuarioResponse;
import dev.matheus.gameVault.entity.Usuario;
import dev.matheus.gameVault.exception.UsernameOrPasswordInvalidException;
import dev.matheus.gameVault.mapper.UsuarioMapper;
import dev.matheus.gameVault.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gamevault/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de login e registro de usuários")
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Operation(summary = "Registrar novo usuário", description = "Cria uma nova conta de jogador no sistema")
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody UsuarioRequest request) {
        Usuario usuarioSalvo = usuarioService.salvar(UsuarioMapper.toUsuario(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toUsuarioResponse(usuarioSalvo));
    }

    @Operation(summary = "Efetuar Login", description = "Autentica o jogador e retorna um token JWT")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            var autenticacaoToken = new UsernamePasswordAuthenticationToken(request.email(), request.senha());
            var autenticacao = authenticationManager.authenticate(autenticacaoToken);

            Usuario usuario = (Usuario) autenticacao.getPrincipal();
            String token = tokenService.gerarToken(usuario);

            return ResponseEntity.ok(new LoginResponse(token));

        } catch (BadCredentialsException e) {
            throw new UsernameOrPasswordInvalidException("Usuário ou senha inválidos");
        }
    }
}