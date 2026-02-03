package dev.matheus.gameVault.repository;

import dev.matheus.gameVault.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Usado pelo Service de autenticação para validar as credenciais
    Optional<UserDetails> findUsuarioByEmail(String email);
}
