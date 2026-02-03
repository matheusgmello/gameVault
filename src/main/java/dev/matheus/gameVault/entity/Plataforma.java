package dev.matheus.gameVault.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plataforma")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Plataforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;
}
