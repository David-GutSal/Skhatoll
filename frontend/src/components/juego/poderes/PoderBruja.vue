<template>
  <div class="poder-bruja">

    <div class="titulo-caja">
      <p class="titulo-pregunta">¿Quieres usar las pociones?</p>
    </div>

    <!-- Pociones -->
    <div class="pociones-fila">

      <!-- ── POCIÓN DE LA VIDA ── -->
      <div class="pocion-bloque">
        <div class="pocion-marco" :class="pocionVidaUsada ? 'marco-gris' : 'marco-vida'">
          <img
            :src="imgVida"
            alt="Poción de la Vida"
            class="pocion-imagen"
            :class="{ 'imagen-gris': pocionVidaUsada }"
          />
        </div>

        <div v-if="jugadorSemimuerto && !pocionVidaUsada" class="chip-victima chip-vida">
          <i class="fa-solid fa-user-injured"></i>
          {{ jugadorSemimuerto.nombre }}
        </div>
        <div v-else-if="!pocionVidaUsada" class="chip-victima chip-vacia">
          <i class="fa-solid fa-bed"></i>
          Sin víctima esta noche
        </div>
        <div v-else class="chip-victima chip-usada">
          <i class="fa-solid fa-check"></i>
          Poción utilizada
        </div>

        <button
          class="btn-pocion btn-vida"
          :disabled="pocionVidaUsada || !jugadorSemimuerto"
          @click="usarPocionVida"
        >
          <i class="fa-solid fa-heart-pulse"></i>
          {{ pocionVidaUsada ? 'Ya usada' : 'Usar Poción de Vida' }}
        </button>
      </div>

      <!-- ── POCIÓN DE LA MUERTE ── -->
      <div class="pocion-bloque">
        <div class="pocion-marco" :class="pocionMuerteUsada ? 'marco-gris' : 'marco-muerte'">
          <img
            :src="imgMuerte"
            alt="Poción de la Muerte"
            class="pocion-imagen"
            :class="{ 'imagen-gris': pocionMuerteUsada }"
          />
        </div>

        <div v-if="jugadorSeleccionado && !pocionMuerteUsada" class="chip-victima chip-muerte">
          <i class="fa-solid fa-crosshairs"></i>
          {{ jugadorSeleccionado.nombre }}
        </div>
        <div v-else-if="!pocionMuerteUsada" class="chip-victima chip-vacia">
          <i class="fa-solid fa-crosshairs"></i>
          Selecciona una víctima en el tablero
        </div>
        <div v-else class="chip-victima chip-usada">
          <i class="fa-solid fa-check"></i>
          Poción utilizada
        </div>

        <button
          class="btn-pocion btn-muerte"
          :disabled="pocionMuerteUsada || !puedeUsarMuerte"
          @click="usarPocionMuerte"
        >
          <i class="fa-solid fa-skull-crossbones"></i>
          {{ pocionMuerteUsada ? 'Ya usada' : 'Usar Poción de Muerte' }}
        </button>
      </div>
    </div>

    <!-- Pociones restantes -->
    <p class="pociones-restantes" :class="pocionesRestantes > 0 ? 'texto-verde' : 'texto-rojo'">
      Pociones restantes: {{ pocionesRestantes }}/2
    </p>

    <!-- Descripciones -->
    <div class="descripciones">
      <p class="desc-item">
        <i class="fa-solid fa-flask"></i>
        <span>
          <b>Poción de la vida:</b> usa esta poción para resucitar a una persona devorada por los
          lobos o a ti misma. Recuerda que solo puedes usarla una vez por partida.
        </span>
      </p>
      <p class="desc-item">
        <i class="fa-solid fa-flask desc-muerte"></i>
        <span>
          <b>Poción de la muerte:</b> usa esta poción para eliminar a otro jugador. Recuerda que
          solo puedes usarla una vez por partida.
        </span>
      </p>
    </div>

    <!-- Finalizar turno -->
    <button
      class="btn-finalizar"
      :class="{ 'btn-finalizado': turnoFinalizado }"
      @click="finalizarTurno"
    >
      <i class="fa-solid fa-door-open"></i>
      {{ turnoFinalizado ? 'Turno finalizado' : 'Finalizo mi turno' }}
    </button>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex'
import axiosInstance from '@/plugins/axios'
import imgVida from '@/assets/imgs/pocion-vida.png'
import imgMuerte from '@/assets/imgs/pocion-muerte.png'

export default {
  name: 'PoderBruja',

  props: {
    jugadorSeleccionado: { type: Object, default: null },
  },

  emits: ['finalizarTurno', 'envenenar', 'vidaUsada'],

  data() {
    return {
      imgVida,
      imgMuerte,
      pocionVidaUsada: false,
      pocionMuerteUsada: false,
      turnoFinalizado: false,
    }
  },

  computed: {
    ...mapGetters('sala', ['codigoSala']),
    ...mapGetters('auth', ['nombre']),
    ...mapState('sala', ['semiMuertos', 'jugadores']),

    // Jugador devorado por los lobos esta noche (primer semimuerto)
    jugadorSemimuerto() {
      if (!this.semiMuertos || this.semiMuertos.length === 0) return null
      const nombre = this.semiMuertos[0]
      return this.jugadores.find((j) => j.nombre === nombre) || null
    },

    // La bruja no puede envenenarse a sí misma ni a jugadores muertos
    puedeUsarMuerte() {
      if (!this.jugadorSeleccionado) return false
      if (this.jugadorSeleccionado.nombre === this.nombre) return false
      if (!this.jugadorSeleccionado.estaVivo) return false
      return true
    },

    pocionesRestantes() {
      let r = 2
      if (this.pocionVidaUsada) r--
      if (this.pocionMuerteUsada) r--
      return r
    },
  },

  methods: {
    async usarPocionVida() {
      if (!this.jugadorSemimuerto || this.pocionVidaUsada) return
      try {
        await axiosInstance.post(`/partida/${this.codigoSala}/habilidad`, {
          nombreHabilidad: 'pocion_vida',
          objetivos: [this.jugadorSemimuerto.idUsuario],
        })
        // Revertir la muerte en el store local
        this.$store.dispatch('sala/quitarSemimuerto', this.jugadorSemimuerto.nombre)
        this.pocionVidaUsada = true
        // Notificar al narrador vía STOMP (lo publica JugadorView)
        this.$emit('vidaUsada', this.jugadorSemimuerto.nombre)
      } catch {
        alert('Error al usar la poción de vida')
      }
    },

    async usarPocionMuerte() {
      if (!this.puedeUsarMuerte || this.pocionMuerteUsada) return
      try {
        await axiosInstance.post(`/partida/${this.codigoSala}/habilidad`, {
          nombreHabilidad: 'pocion_muerte',
          objetivos: [this.jugadorSeleccionado.idUsuario],
        })
        // El backend ya emite WS /muerte → todos los clientes lo reciben
        this.pocionMuerteUsada = true
        this.$emit('envenenar', this.jugadorSeleccionado)
      } catch {
        alert('Error al usar la poción de muerte')
      }
    },

    finalizarTurno() {
      this.turnoFinalizado = true
      this.$emit('finalizarTurno')
    },

    resetear() {
      this.pocionVidaUsada = false
      this.pocionMuerteUsada = false
      this.turnoFinalizado = false
    },
  },
}
</script>

<style scoped>
.poder-bruja {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 24px;
  border-radius: 15px;
  width: 100%;
  max-width: 620px;
  background: rgba(0, 0, 0, 0.85);
  border: 3px solid #9b59b6;
}

/* ── Título ── */
.titulo-caja {
  width: 100%;
}

.titulo-pregunta {
  font-family: 'Cinzel', Arial, sans-serif;
  font-weight: 700;
  font-size: 1.3rem;
  color: #000;
  background: white;
  text-align: center;
  padding: 12px 20px;
  border-radius: 10px;
  margin: 0;
}

/* ── Layout pociones ── */
.pociones-fila {
  display: flex;
  gap: 20px;
  width: 100%;
  justify-content: center;
}

.pocion-bloque {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  flex: 1;
  max-width: 240px;
}

/* ── Imagen ── */
.pocion-marco {
  width: 100%;
  aspect-ratio: 1 / 1;
  border-radius: 10px;
  border: 4px solid;
  overflow: hidden;
  transition: border-color 0.3s ease;
}

.marco-vida   { border-color: #2d9e2d; }
.marco-muerte { border-color: #9b59b6; }
.marco-gris   { border-color: #444; }

.pocion-imagen {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: filter 0.4s ease;
}

.imagen-gris {
  filter: grayscale(100%) brightness(0.55);
}

/* ── Chip de víctima ── */
.chip-victima {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 20px;
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 0.85rem;
  font-weight: 700;
  width: 100%;
  justify-content: center;
}

.chip-vida {
  background: rgba(45, 158, 45, 0.15);
  border: 1px solid #2d9e2d;
  color: #5fd45f;
}

.chip-muerte {
  background: rgba(155, 89, 182, 0.15);
  border: 1px solid #9b59b6;
  color: #cc88ff;
}

.chip-vacia {
  background: rgba(255, 255, 255, 0.04);
  border: 1px dashed #555;
  color: #555;
}

.chip-usada {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid #333;
  color: #555;
}

/* ── Botones de poción ── */
.btn-pocion {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 10px;
  border: 2px solid;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
  width: 100%;
  transition:
    background 0.2s ease,
    color 0.2s ease;
}

.btn-vida {
  background: #2d9e2d;
  border-color: #2d9e2d;
  color: white;
}

.btn-vida:hover:not(:disabled) {
  background: white;
  color: #2d9e2d;
}

.btn-muerte {
  background: #9b59b6;
  border-color: #9b59b6;
  color: white;
}

.btn-muerte:hover:not(:disabled) {
  background: white;
  color: #9b59b6;
}

.btn-pocion:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

/* ── Pociones restantes ── */
.pociones-restantes {
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 1rem;
  text-align: center;
  margin: 0;
}

.texto-verde { color: #5fd45f; }
.texto-rojo  { color: #cc0000; }

/* ── Descripciones ── */
.descripciones {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
  padding: 14px 16px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid #333;
}

.desc-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 0.88rem;
  color: #ccc;
  margin: 0;
  line-height: 1.5;
}

.desc-item i {
  color: #2d9e2d;
  font-size: 1rem;
  margin-top: 2px;
  flex-shrink: 0;
}

.desc-muerte {
  color: #9b59b6 !important;
}

/* ── Botón finalizar ── */
.btn-finalizar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 10px;
  border: 3px solid #cc0000;
  background: #cc0000;
  color: white;
  font-family: 'Raleway', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.95rem;
  cursor: pointer;
  transition:
    background 0.3s ease,
    border-color 0.3s ease,
    color 0.3s ease;
}

.btn-finalizar:hover:not(.btn-finalizado) {
  background: #a00000;
  border-color: #a00000;
}

.btn-finalizado {
  background: #111;
  border-color: #333;
  color: #555;
  cursor: not-allowed;
}

@media (max-width: 500px) {
  .pociones-fila {
    flex-direction: column;
    align-items: center;
  }
  .pocion-bloque {
    max-width: 100%;
    width: 100%;
  }
}
</style>