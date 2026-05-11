package dev.matheus.gameVault.service;

import dev.matheus.gameVault.entity.Usuario;
import dev.matheus.gameVault.exception.DuplicateResourceException;
import dev.matheus.gameVault.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public Usuario salvar(Usuario usuario) {
        if (repository.existsByEmailIgnoreCase(usuario.getEmail())) {
            throw new DuplicateResourceException("Ja existe um usuario com esse e-mail.");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return repository.save(usuario);
    }
}
