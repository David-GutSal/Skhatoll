<template>
  <div class="lista-jugadores">
    <div class="overlay">

      <h2 class="titulo-lista">Jugadores en Sala</h2>
      <p class="subtitulo-lista">Nuevos visitantes llegan a la aldea de Castronegro...</p>

      <ul class="lista" :class="modoCreador ? 'lista-creador' : 'lista-jugador'">
        <li v-for="jugador in jugadores" :key="jugador.idUsuario" class="jugador-item">

          <i v-if="esNarrador(jugador)" class="fa-solid fa-book-open-reader icono-narrador"></i>
          <i v-else class="fa-solid fa-person icono-jugador"></i>

          <span class="jugador-nombre" :class="{ 'es-narrador': esNarrador(jugador) }">
            {{ jugador.nombre }}
          </span>

          <input
            v-if="modoCreador"
            type="radio"
            name="narrador"
            class="radio-narrador"
            :checked="esNarrador(jugador)"
            @change="$emit('asignar-narrador', jugador.idUsuario)"
          />

        </li>
      </ul>

      <p v-if="modoCreador" class="mensaje-pie mensaje-creador">
        <i class="fa-solid fa-book-open-reader"></i>
        Puedes elegir si ser tú mismo el narrador o que lo sea otro jugador
      </p>

      <p v-else class="mensaje-pie mensaje-jugador">
        <i class="fa-solid fa-book-open-reader"></i>
        <span v-if="soyNarrador">
          ¡Has sido escogido para ser el narrador de la próxima partida! ¡Deja volar tu imaginación!
        </span>
        <span v-else-if="nombreNarrador">
          Al parecer <strong>{{ nombreNarrador }}</strong> es el narrador
        </span>
        <span v-else>
          Esperando a que el creador elija un narrador...
        </span>
      </p>

    </div>
  </div>
</template>

<script>
import lobbyImg from '@/assets/imgs/bienvenida.jpg'

export default {
  name: 'ListaJugadores',

  props: {
    jugadores: Array,
    modoCreador: Boolean,
    nombreNarrador: String,
    soyNarrador: Boolean,
  },

  emits: ['asignar-narrador'],

  data() {
    return { lobbyImg }
  },

  methods: {
    esNarrador(jugador) {
      return jugador.nombre === this.nombreNarrador
    },
  },
}
</script>

<style scoped>
.lista-jugadores {
  background-image: v-bind("'url(' + lobbyImg + ')'");
  background-size: cover;
  background-position: center;
  border-radius: 15px;
  overflow: hidden;
  height: fit-content;
}

.overlay {
  background: rgba(31, 31, 31, 0.88);
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.titulo-lista {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.5rem;
  font-weight: 700;
  color: white;
  text-align: center;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin: 0;
}

.subtitulo-lista {
  font-family: 'Raleway', Arial, sans-serif;
  font-style: italic;
  color: white;
  font-size: 1rem;
  text-align: center;
  margin: 0 0 6px 0;
}

.lista {
  list-style: none;
  padding: 0;
  margin: 0;
  border-radius: 10px;
  overflow: hidden;
}

.lista-creador {
  border: 3px solid #8b0000;
  background: #000;
}

.lista-jugador {
  border: 3px solid #000;
  background: white;
}

.jugador-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-bottom: 1px solid rgba(0,0,0,0.1);
}

.lista-creador .jugador-item {
  border-bottom-color: rgba(255,255,255,0.08);
}

.jugador-item:last-child {
  border-bottom: none;
}

.icono-jugador {
  font-size: 1.3rem;
  flex-shrink: 0;
}

.lista-creador .icono-jugador { color: #8b0000; }
.lista-jugador .icono-jugador { color: #8b0000; }

.icono-narrador {
  font-size: 1.3rem;
  flex-shrink: 0;
}

.lista-creador .icono-narrador { color: white; }
.lista-jugador .icono-narrador { color: #8b0000; }

.jugador-nombre {
  flex: 1;
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1.05rem;
  font-weight: 700;
}

.lista-creador .jugador-nombre { color: white; }
.lista-jugador .jugador-nombre { color: #8b0000; }

.jugador-nombre.es-narrador {
  color: #cc0000;
}

.lista-jugador .jugador-nombre.es-narrador {
  color: #8b0000;
}

.radio-narrador {
  margin-left: auto;
  accent-color: #8b0000;
  width: 20px;
  height: 20px;
  cursor: pointer;
  flex-shrink: 0;
}

.mensaje-pie {
  font-family: 'Raleway', Arial, sans-serif;
  font-style: italic;
  font-size: 1.2rem;
  margin: 4px 0 0 0;
  display: flex;
  align-items: flex-start;
  gap: 8px;
  line-height: 1.2;
}

.mensaje-pie i {
  flex-shrink: 0;
  margin-top: 2px;
}

.mensaje-creador { color: #cc0000; }
.mensaje-jugador { color: white; }
</style>