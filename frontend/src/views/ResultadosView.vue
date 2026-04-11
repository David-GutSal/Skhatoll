<template>
  <div class="contenedor-resultados" :class="fondoClase">

    <div class="cuadro" :class="bordeClase">

      <template v-if="esEnamorados">
        <h1 class="titulo titulo-enamorados">
          ¡TRIUNFÓ EL AMOR! <i class="fa-solid fa-heart"></i>
        </h1>
        <p class="subtitulo subtitulo-enamorados">
          Los enamorados se salieron con la suya y ahora huirán a un nuevo pueblo 
          a crear su nidito de amor <i class="fa-solid fa-heart"></i> <i class="fa-solid fa-heart"> </i> <i class="fa-solid fa-heart"></i> 
        </p>
      </template>

      <template v-else-if="esEmpate">
        <h1 class="titulo titulo-empate">
          EMPATE <i class="fa-regular fa-face-meh-blank"></i>
        </h1>
        <p class="subtitulo subtitulo-empate">
          Upps... Hubo empate... ¡Habrá que echar otra partida para ver quién gana!
        </p>
      </template>

      <template v-else-if="esNarrador">
        <h1 class="titulo titulo-narrador">
          Los ganadores son: 
          <span :class="bandoGanador === 'lobo' ? 'texto-lobos' : 
                         bandoGanador === 'enamorados' ? 'texto-enamorados' : 'texto-aldeanos'">
            {{ nombreBando(bandoGanador) }}
          </span>
        </h1>
        <p class="subtitulo subtitulo-narrador">¡Enhorabuena a los ganadores!</p>
      </template>

      <template v-else>
        <h1 class="titulo" :class="victoria ? 'titulo-victoria' : 'titulo-derrota'">
          <span v-if="victoria">VICTORIA <i class="fa-regular fa-face-grin-beam"></i></span>
          <span v-else>DERROTA <i class="fa-regular fa-face-dizzy"></i></span>
        </h1>

        <p class="subtitulo" :class="victoria ? 'subtitulo-victoria' : 'subtitulo-derrota'">
          <span v-if="victoria">
            ¡Enhorabuena! ¡¡El equipo de {{ nombreBando(bandoGanador) }} se ha alzado con la victoria!!
          </span>
          <span v-else>
            ¡Lo sentimos! El equipo de {{ nombreBando(bandoGanador) }} ha ganado la partida...
          </span>
        </p>
      </template>

      <img
        :src="imagenCelebracion"
        :alt="bandoGanador"
        class="imagen-celebracion"
        :class="imagenBordeClase"
      />

      <div class="botones">
        <router-link to="/" class="btn-resultado" :class="btnClase" @click="limpiarSala">
          INICIO
        </router-link>
        <router-link to="/sala" class="btn-resultado" :class="btnClase" @click="limpiarSala">
          ¡OTRA PARTIDA!
        </router-link>
      </div>

    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import aldeanoscelebrandoImg from '@/assets/imgs/aldeanoscelebrando.jpg'
import loboscelebrandoImg from '@/assets/imgs/loboscelebrando.jpg'
import empatadoscelebrandoImg from '@/assets/imgs/empatadoscelebrando.jpg'
import enamoradoscelebrandoImg from '@/assets/imgs/enamoradoscelebrando.jpg'

export default {
  name: 'ResultadosView',

  computed: {
    ...mapGetters('sala', ['bandoGanador', 'miBando']),
    ...mapGetters('auth', ['nombre']),

    esNarrador() {
      return this.$store.getters['sala/esCreador']
    },

    esEmpate() {
      return this.bandoGanador?.toLowerCase() === 'empate'
    },

    esEnamorados() {
      return this.bandoGanador?.toLowerCase() === 'enamorados'
    },

    victoria() {
      if (this.esNarrador || this.esEmpate) return false

      if (this.esEnamorados) {
        return this.miBando?.toLowerCase() === 'enamorados'
      }

      if (!this.bandoGanador || !this.miBando) return false
      return this.bandoGanador.toLowerCase() === this.miBando.toLowerCase()
    },

    fondoClase() {
      if (this.esEnamorados) return 'fondo-enamorados'
      if (this.esEmpate) return 'fondo-empate'
      return this.victoria || this.esNarrador ? 'fondo-victoria' : 'fondo-derrota'
    },

    bordeClase() {
      if (this.esEmpate) return 'cuadro-empate'
      if (this.esEnamorados) return 'cuadro-enamorados'
      return this.victoria || this.esNarrador ? 'cuadro-victoria' : 'cuadro-derrota'
    },

    imagenBordeClase() {
      if (this.esEnamorados) return 'imagen-enamorados'
      if (this.esEmpate) return 'imagen-empate'
      return this.victoria || this.esNarrador ? 'imagen-victoria' : 'imagen-derrota'
    },

    btnClase() {
      if (this.esEnamorados) return 'btn-enamorados'
      if (this.esEmpate) return 'btn-empate'
      return ''
    },

    imagenCelebracion() {
      if (this.esEnamorados) return enamoradoscelebrandoImg
      if (this.esEmpate) return empatadoscelebrandoImg
      return this.bandoGanador?.toLowerCase() === 'lobo' 
        ? loboscelebrandoImg 
        : aldeanoscelebrandoImg
    }
  },

  methods: {
    nombreBando(bando) {
      const nombres = {
        lobo: 'Los Lobos',
        aldea: 'Los Aldeanos',
        enamorados: 'Los Enamorados'
      }
      return nombres[bando?.toLowerCase()] || bando
    },

    limpiarSala() {
      this.$store.dispatch('sala/salir')
    }
  },

// ====================== PREVISUALIZACIÓN ======================
//Descomentar para ver
  /*created() {
    // ==================== ACTIVAR / DESACTIVAR PREVISUALIZACIÓN ====================
    const PREVIEW_ACTIVADA = true   // ← Cambia a `true` para activar previsualización

    if (!PREVIEW_ACTIVADA) return

    // Configuración de prueba - Cambia estos valores según lo que quieras ver
    const config = {
      bandoGanador: 'enamorados',   // 'lobo' | 'aldea' | 'empate' | 'enamorados'
      miBando: 'aldea',        // 'lobo' | 'aldea' | 'enamorados'
      esCreador: false              // true = ver como Narrador
    }

    this.$store.commit('sala/SET_RESULTADO', { 
      bandoGanador: config.bandoGanador, 
      mensajeFin: '' 
    })
    this.$store.commit('sala/SET_MI_BANDO', config.miBando)
    this.$store.commit('sala/SET_SALA', { codigoSala: 'PREVIEW', esCreador: config.esCreador })
  }
*/
}
</script>

<style scoped>

.contenedor-resultados {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  box-sizing: border-box;
  background-size: cover;
  background-position: center;
}

.fondo-victoria { background-image: url('@/assets/imgs/fondovictoria.png'); }
.fondo-derrota { background-image: url('@/assets/imgs/fondonoche.png'); }
.fondo-empate { background-image: url('@/assets/imgs/fondodia.png'); }
.fondo-enamorados { background-image: url('@/assets/imgs/fondoenamorado.png'); }

.cuadro {
  width: 95%;
  max-width: 800px;
  background-image: url('@/assets/imgs/mesa.jpg');
  background-size: cover;
  background-position: center;
  border-radius: 15px;
  overflow: hidden;
  position: relative;
}

.cuadro::before {
  content: '';
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.85);
  pointer-events: none;
}

.cuadro-victoria { border: 8px solid white; }
.cuadro-derrota { border: 8px solid #cc0000; }
.cuadro-empate { border: 8px solid white; }
.cuadro-enamorados { border: 8px solid #531e8f; }

.titulo, .subtitulo, .imagen-celebracion, .botones {
  position: relative;
  z-index: 1;
}

.titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-weight: 700;
  font-size: clamp(2rem, 6vw, 4rem);
  text-align: center;
  margin: 24px 15px 8px;
}

.titulo-victoria { color: #e4ba03; }
.titulo-derrota { color: #cc0000; }
.titulo-empate { color: #4a90d9; }
.titulo-enamorados { color: #ff69b4; }
.titulo-narrador { color: #e4ba03; font-size: clamp(2rem, 4vw, 2.5rem); }

.texto-lobos { color: #cc0000; }
.texto-aldeanos { color: #2d9e2d; }
.texto-enamorados { color: #ff69b4; }

.subtitulo {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: clamp(1rem, 2vw, 1.2rem);
  text-align: center;
  margin: 0 15px 16px;
  font-weight: 700;
}

.subtitulo-victoria, .subtitulo-narrador { color: #e4ba03; }
.subtitulo-derrota { color: #cc0000; }
.subtitulo-empate { color: #4a90d9; }
.subtitulo-enamorados { color: white; }
.fa-heart{color:#ff69b4;}

.imagen-celebracion {
  width: 100%;
  display: block;
  object-fit: cover;
  max-height: 380px;
}

.imagen-victoria { border-top: 5px solid #e4ba03; border-bottom: 5px solid #e4ba03; }
.imagen-derrota { border-top: 5px solid #cc0000; border-bottom: 5px solid #cc0000; }
.imagen-empate { border-top: 5px solid #4a90d9; border-bottom: 5px solid #4a90d9; }
.imagen-enamorados { border-top: 5px solid #531e8f; border-bottom: 5px solid #5b2d8e; }

.botones { display: flex; gap: 16px; justify-content: center; padding: 24px 15px; }

.btn-resultado {
  background: #cc0000;
  color: white;
  padding: 14px 28px;
  border-radius: 10px;
  font-weight: 700;
  text-decoration: none;
  transition: all 0.2s ease;
}

.btn-resultado:hover { background: white; color: #cc0000; }
.btn-empate { background: #4a90d9; }
.btn-empate:hover { color: #4a90d9; background: white; }
.btn-enamorados { background: #531e8f; }
.btn-enamorados:hover { color: #531e8f; background: white; }
</style>
