package com.skhatoll.backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enamorados")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enamorados {

    @Id
    @Column(name = "id_sala")
    private Integer idSala;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sala", insertable = false, updatable = false)
    private Sala sala;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_1", nullable = false)
    private Usuario usuario1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_2", nullable = false)
    private Usuario usuario2;
}