package dev.matheus.gameVault.repository;

import dev.matheus.gameVault.entity.Genero;
import dev.matheus.gameVault.entity.Jogo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JogoRepository extends JpaRepository<Jogo, Long> {

    // Busca todos os jogos que contenham o gênero passado por parâmetro
    List<Jogo> findByGenerosContaining(Genero genero);

}
