package dev.matheus.gameVault.repository;

import dev.matheus.gameVault.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<UserDetails> findUsuarioByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
}
