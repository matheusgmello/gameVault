package dev.matheus.gameVault.repository;

import dev.matheus.gameVault.entity.Genero;
import dev.matheus.gameVault.entity.Jogo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JogoRepository extends JpaRepository<Jogo, Long> {

    List<Jogo> findByUsuarioId(Long usuarioId);

    Optional<Jogo> findByIdAndUsuarioId(Long id, Long usuarioId);

    // Busca todos os jogos do usuario que contenham o gênero passado por parâmetro
    List<Jogo> findByGenerosContainingAndUsuarioId(Genero genero, Long usuarioId);

}
