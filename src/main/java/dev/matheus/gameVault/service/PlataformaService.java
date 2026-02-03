package dev.matheus.gameVault.service;

import dev.matheus.gameVault.entity.Plataforma;
import dev.matheus.gameVault.repository.PlataformaRepository;
import lombok.RequiredArgsConstructor;
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
        return repository.save(plataforma);
    }

    public Optional<Plataforma> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}