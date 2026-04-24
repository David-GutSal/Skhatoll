package com.skhatoll.backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "habilidades_sala",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_sala_usuario", "nombre"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabilidadSala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habilidad")
    private Integer idHabilidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sala_usuario", nullable = false)
    private SalaUsuario idSalaUsuario;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "usada", nullable = false)
    @Builder.Default
    private Boolean usada = false;
}
