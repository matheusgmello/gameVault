package dev.matheus.gameVault.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "jogo")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Jogo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_lancamento")
    private LocalDate dataLancamento;

    @Column(name = "capa_url", length = 1000)
    private String capaUrl;

    private Double nota;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private JogoStatus status = JogoStatus.WISHLIST;

    @Builder.Default
    private Boolean favorito = false;

    @Column(columnDefinition = "TEXT")
    private String review;

    @Column(name = "horas_jogadas")
    @Builder.Default
    private Integer horasJogadas = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToMany
    @JoinTable(
            name = "jogo_genero",
            joinColumns = @JoinColumn(name = "jogo_id"),
            inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    @Builder.Default
    private Set<Genero> generos = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "jogo_plataforma",
            joinColumns = @JoinColumn(name = "jogo_id"),
            inverseJoinColumns = @JoinColumn(name = "plataforma_id")
    )
    @Builder.Default
    private Set<Plataforma> plataformas = new HashSet<>();

}
