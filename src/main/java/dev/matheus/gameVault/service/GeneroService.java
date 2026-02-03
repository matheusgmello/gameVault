package dev.matheus.gameVault.service;

import dev.matheus.gameVault.entity.Genero;
import dev.matheus.gameVault.repository.GeneroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeneroService {
    private final GeneroRepository repository;

    public List<Genero> buscarTodos() {
        return repository.findAll();
    }

    public Genero salvar(Genero genero) {
        return repository.save(genero);
    }

    public Optional<Genero> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}