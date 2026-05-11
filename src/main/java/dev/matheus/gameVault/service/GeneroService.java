package dev.matheus.gameVault.service;

import dev.matheus.gameVault.entity.Genero;
import dev.matheus.gameVault.exception.BusinessRuleException;
import dev.matheus.gameVault.exception.DuplicateResourceException;
import dev.matheus.gameVault.exception.ResourceNotFoundException;
import dev.matheus.gameVault.repository.GeneroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
        if (repository.existsByNomeIgnoreCase(genero.getNome())) {
            throw new DuplicateResourceException("Ja existe um genero com esse nome.");
        }
        return repository.save(genero);
    }

    public Optional<Genero> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void deletar(Long id) {
        Genero genero = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genero nao encontrado."));
        try {
            repository.delete(genero);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("Nao e possivel excluir o genero porque existem jogos vinculados.");
        }
    }
}
