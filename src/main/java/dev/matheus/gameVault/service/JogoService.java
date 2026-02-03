package dev.matheus.gameVault.service;

import dev.matheus.gameVault.entity.Genero;
import dev.matheus.gameVault.entity.Jogo;
import dev.matheus.gameVault.entity.Plataforma;
import dev.matheus.gameVault.repository.JogoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JogoService {

    private final JogoRepository jogoRepository;
    private final GeneroService generoService;
    private final PlataformaService plataformaService;

    public Jogo salvar(Jogo jogo) {
        jogo.setGeneros(this.validarGeneros(jogo.getGeneros()));
        jogo.setPlataformas(this.validarPlataformas(jogo.getPlataformas()));
        return jogoRepository.save(jogo);
    }

    public List<Jogo> buscarTodos() {
        return jogoRepository.findAll();
    }

    public Optional<Jogo> buscarPorId(Long id) {
        return jogoRepository.findById(id);
    }

    public List<Jogo> buscarPorGenero(Long generoId) {
        Genero genero = Genero.builder().id(generoId).build();
        return jogoRepository.findByGenerosContaining(genero);
    }

    public Optional<Jogo> atualizar(Long id, Jogo jogoAtualizado) {
        return jogoRepository.findById(id).map(jogo -> {
            jogo.setTitulo(jogoAtualizado.getTitulo());
            jogo.setDescricao(jogoAtualizado.getDescricao());
            jogo.setDataLancamento(jogoAtualizado.getDataLancamento());
            jogo.setNota(jogoAtualizado.getNota());

            jogo.getGeneros().clear();
            jogo.getGeneros().addAll(this.validarGeneros(jogoAtualizado.getGeneros()));

            jogo.getPlataformas().clear();
            jogo.getPlataformas().addAll(this.validarPlataformas(jogoAtualizado.getPlataformas()));

            return jogoRepository.save(jogo);
        });
    }

    public void deletar(Long id) {
        jogoRepository.deleteById(id);
    }

    private Set<Genero> validarGeneros(Set<Genero> generos) {
        return generos.stream()
                .map(g -> generoService.buscarPorId(g.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private Set<Plataforma> validarPlataformas(Set<Plataforma> plataformas) {
        return plataformas.stream()
                .map(p -> plataformaService.buscarPorId(p.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }
}