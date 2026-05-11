package dev.matheus.gameVault.service;

import dev.matheus.gameVault.entity.Plataforma;
import dev.matheus.gameVault.exception.BusinessRuleException;
import dev.matheus.gameVault.exception.DuplicateResourceException;
import dev.matheus.gameVault.exception.ResourceNotFoundException;
import dev.matheus.gameVault.repository.PlataformaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlataformaService {
    private final PlataformaRepository repository;

    public List<Plataforma> buscarTodos() {
        return repository.findAll();
    }

    public Plataforma salvar(Plataforma plataforma) {
        if (repository.existsByNomeIgnoreCase(plataforma.getNome())) {
            throw new DuplicateResourceException("Ja existe uma plataforma com esse nome.");
        }
        return repository.save(plataforma);
    }

    public Optional<Plataforma> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void deletar(Long id) {
        Plataforma plataforma = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plataforma nao encontrada."));
        try {
            repository.delete(plataforma);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("Nao e possivel excluir a plataforma porque existem jogos vinculados.");
        }
    }
}
