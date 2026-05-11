package dev.matheus.gameVault.service;

import dev.matheus.gameVault.controller.response.EstatisticaItemResponse;
import dev.matheus.gameVault.controller.response.JogoEstatisticasResponse;
import dev.matheus.gameVault.entity.Genero;
import dev.matheus.gameVault.entity.Jogo;
import dev.matheus.gameVault.entity.JogoStatus;
import dev.matheus.gameVault.entity.Plataforma;
import dev.matheus.gameVault.entity.Usuario;
import dev.matheus.gameVault.exception.DuplicateResourceException;
import dev.matheus.gameVault.exception.ResourceNotFoundException;
import dev.matheus.gameVault.repository.JogoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class JogoService {

    private final JogoRepository jogoRepository;
    private final GeneroService generoService;
    private final PlataformaService plataformaService;

    public Jogo salvar(Jogo jogo, Long usuarioId) {
        validarTituloDuplicado(usuarioId, jogo.getTitulo());
        jogo.setUsuario(Usuario.builder().id(usuarioId).build());
        jogo.setGeneros(this.validarGeneros(jogo.getGeneros()));
        jogo.setPlataformas(this.validarPlataformas(jogo.getPlataformas()));
        return jogoRepository.save(jogo);
    }

    public List<Jogo> buscarTodos(Long usuarioId) {
        return jogoRepository.findByUsuarioId(usuarioId);
    }

    public List<Jogo> buscarComFiltros(Long usuarioId, String busca, JogoStatus status, Long generoId,
                                       Long plataformaId, Boolean favorito, String ordenarPor) {
        Stream<Jogo> jogos = jogoRepository.findByUsuarioId(usuarioId).stream();

        if (busca != null && !busca.isBlank()) {
            String termo = busca.trim().toLowerCase();
            jogos = jogos.filter(jogo -> jogo.getTitulo() != null
                    && jogo.getTitulo().toLowerCase().contains(termo));
        }

        if (status != null) {
            jogos = jogos.filter(jogo -> status.equals(jogo.getStatus()));
        }

        if (generoId != null) {
            jogos = jogos.filter(jogo -> jogo.getGeneros().stream()
                    .anyMatch(genero -> generoId.equals(genero.getId())));
        }

        if (plataformaId != null) {
            jogos = jogos.filter(jogo -> jogo.getPlataformas().stream()
                    .anyMatch(plataforma -> plataformaId.equals(plataforma.getId())));
        }

        if (favorito != null) {
            jogos = jogos.filter(jogo -> favorito.equals(jogo.getFavorito()));
        }

        return jogos.sorted(comparador(ordenarPor)).toList();
    }

    public Optional<Jogo> buscarPorId(Long id, Long usuarioId) {
        return jogoRepository.findByIdAndUsuarioId(id, usuarioId);
    }

    public List<Jogo> buscarPorGenero(Long generoId, Long usuarioId) {
        Genero genero = Genero.builder().id(generoId).build();
        return jogoRepository.findByGenerosContainingAndUsuarioId(genero, usuarioId);
    }

    public JogoEstatisticasResponse buscarEstatisticas(Long usuarioId) {
        List<Jogo> jogos = jogoRepository.findByUsuarioId(usuarioId);
        int totalJogos = jogos.size();
        int totalHoras = jogos.stream()
                .map(Jogo::getHorasJogadas)
                .mapToInt(horas -> horas == null ? 0 : horas)
                .sum();
        int totalFavoritos = (int) jogos.stream()
                .filter(jogo -> Boolean.TRUE.equals(jogo.getFavorito()))
                .count();
        double mediaNota = jogos.stream()
                .map(Jogo::getNota)
                .filter(nota -> nota != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        return JogoEstatisticasResponse.builder()
                .totalJogos(totalJogos)
                .totalGeneros(generoService.buscarTodos().size())
                .totalPlataformas(plataformaService.buscarTodos().size())
                .mediaNota(mediaNota)
                .totalFavoritos(totalFavoritos)
                .totalHoras(totalHoras)
                .jogosPorPlataforma(agruparPorPlataforma(jogos))
                .jogosPorStatus(agruparPorStatus(jogos))
                .topJogos(topJogos(jogos))
                .build();
    }

    public Optional<Jogo> atualizar(Long id, Long usuarioId, Jogo jogoAtualizado) {
        return jogoRepository.findByIdAndUsuarioId(id, usuarioId).map(jogo -> {
            validarTituloDuplicado(usuarioId, jogoAtualizado.getTitulo(), id);
            jogo.setTitulo(jogoAtualizado.getTitulo());
            jogo.setDescricao(jogoAtualizado.getDescricao());
            jogo.setDataLancamento(jogoAtualizado.getDataLancamento());
            jogo.setCapaUrl(jogoAtualizado.getCapaUrl());
            jogo.setNota(jogoAtualizado.getNota());
            jogo.setStatus(jogoAtualizado.getStatus());
            jogo.setFavorito(jogoAtualizado.getFavorito());
            jogo.setReview(jogoAtualizado.getReview());
            jogo.setHorasJogadas(jogoAtualizado.getHorasJogadas());

            jogo.getGeneros().clear();
            jogo.getGeneros().addAll(this.validarGeneros(jogoAtualizado.getGeneros()));

            jogo.getPlataformas().clear();
            jogo.getPlataformas().addAll(this.validarPlataformas(jogoAtualizado.getPlataformas()));

            return jogoRepository.save(jogo);
        });
    }

    public void deletar(Long id, Long usuarioId) {
        jogoRepository.findByIdAndUsuarioId(id, usuarioId)
                .ifPresent(jogoRepository::delete);
    }

    private Comparator<Jogo> comparador(String ordenarPor) {
        return switch (ordenarPor == null ? "titulo" : ordenarPor) {
            case "nota" -> Comparator.comparing(
                    Jogo::getNota,
                    Comparator.nullsLast(Comparator.reverseOrder())
            );
            case "lancamento" -> Comparator.comparing(
                    Jogo::getDataLancamento,
                    Comparator.nullsLast(Comparator.reverseOrder())
            );
            case "horas" -> Comparator.comparing(
                    Jogo::getHorasJogadas,
                    Comparator.nullsLast(Comparator.reverseOrder())
            );
            default -> Comparator.comparing(
                    Jogo::getTitulo,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
            );
        };
    }

    private List<EstatisticaItemResponse> agruparPorPlataforma(List<Jogo> jogos) {
        Map<String, Long> contagem = jogos.stream()
                .flatMap(jogo -> jogo.getPlataformas().stream())
                .collect(Collectors.groupingBy(Plataforma::getNome, Collectors.counting()));

        return contagem.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(item -> new EstatisticaItemResponse(item.getKey(), item.getValue()))
                .toList();
    }

    private List<EstatisticaItemResponse> agruparPorStatus(List<Jogo> jogos) {
        Map<JogoStatus, Long> contagem = jogos.stream()
                .collect(Collectors.groupingBy(Jogo::getStatus, Collectors.counting()));

        return contagem.entrySet().stream()
                .sorted(Map.Entry.<JogoStatus, Long>comparingByValue().reversed())
                .map(item -> new EstatisticaItemResponse(item.getKey().name(), item.getValue()))
                .toList();
    }

    private List<EstatisticaItemResponse> topJogos(List<Jogo> jogos) {
        return jogos.stream()
                .filter(jogo -> jogo.getNota() != null)
                .sorted(Comparator.comparing(Jogo::getNota, Comparator.reverseOrder()))
                .limit(5)
                .map(jogo -> new EstatisticaItemResponse(jogo.getTitulo(), jogo.getNota()))
                .toList();
    }

    private Set<Genero> validarGeneros(Set<Genero> generos) {
        return generos.stream()
                .map(g -> generoService.buscarPorId(g.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Genero informado nao foi encontrado.")))
                .collect(Collectors.toSet());
    }

    private Set<Plataforma> validarPlataformas(Set<Plataforma> plataformas) {
        return plataformas.stream()
                .map(p -> plataformaService.buscarPorId(p.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Plataforma informada nao foi encontrada.")))
                .collect(Collectors.toSet());
    }

    private void validarTituloDuplicado(Long usuarioId, String titulo) {
        if (jogoRepository.existsByUsuarioIdAndTituloIgnoreCase(usuarioId, titulo)) {
            throw new DuplicateResourceException("Voce ja possui um jogo com esse titulo na sua biblioteca.");
        }
    }

    private void validarTituloDuplicado(Long usuarioId, String titulo, Long jogoId) {
        if (jogoRepository.existsByUsuarioIdAndTituloIgnoreCaseAndIdNot(usuarioId, titulo, jogoId)) {
            throw new DuplicateResourceException("Voce ja possui um jogo com esse titulo na sua biblioteca.");
        }
    }
}
