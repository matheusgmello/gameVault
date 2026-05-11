package dev.matheus.gameVault.repository;

import dev.matheus.gameVault.entity.Genero;
import dev.matheus.gameVault.entity.Jogo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JogoRepository extends JpaRepository<Jogo, Long> {

    List<Jogo> findByUsuarioId(Long usuarioId);

    Optional<Jogo> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByUsuarioIdAndTituloIgnoreCase(Long usuarioId, String titulo);

    boolean existsByUsuarioIdAndTituloIgnoreCaseAndIdNot(Long usuarioId, String titulo, Long id);

    List<Jogo> findByGenerosContainingAndUsuarioId(Genero genero, Long usuarioId);
}
