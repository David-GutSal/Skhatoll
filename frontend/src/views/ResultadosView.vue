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

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'
import aldeanoscelebrandoImg from '@/assets/imgs/aldeanoscelebrando.jpg'
import loboscelebrandoImg from '@/assets/imgs/loboscelebrando.jpg'
import empatadoscelebrandoImg from '@/assets/imgs/empatadoscelebrando.jpg'
import enamoradoscelebrandoImg from '@/assets/imgs/enamoradoscelebrando.jpg'

const store = useStore()

const bandoGanador = computed(() => store.getters['sala/bandoGanador'])
const miBando = computed(() => store.getters['sala/miBando'])
const nombre = computed(() => store.getters['auth/nombre'])

const esNarrador = computed(() => {
  const miNombre = store.getters['auth/nombre']
  const jugadores = store.getters['sala/jugadores']
  const yo = jugadores?.find(j => j.nombre === miNombre)
  return yo?.esNarrador === true
})

const esEmpate = computed(() => bandoGanador.value?.toLowerCase() === 'empate')

const esEnamorados = computed(() => bandoGanador.value?.toLowerCase() === 'enamorados')

const victoria = computed(() => {
  if (esNarrador.value || esEmpate.value) return false

  if (esEnamorados.value) {
    return miBando.value?.toLowerCase() === 'enamorados'
  }

  if (!bandoGanador.value || !miBando.value) return false
  return bandoGanador.value.toLowerCase() === miBando.value.toLowerCase()
})

const fondoClase = computed(() => {
  if (esEnamorados.value) return 'fondo-enamorados'
  if (esEmpate.value) return 'fondo-empate'
  return victoria.value || esNarrador.value ? 'fondo-victoria' : 'fondo-derrota'
})

const bordeClase = computed(() => {
  if (esEmpate.value) return 'cuadro-empate'
  if (esEnamorados.value) return 'cuadro-enamorados'
  return victoria.value || esNarrador.value ? 'cuadro-victoria' : 'cuadro-derrota'
})

const imagenBordeClase = computed(() => {
  if (esEnamorados.value) return 'imagen-enamorados'
  if (esEmpate.value) return 'imagen-empate'
  return victoria.value || esNarrador.value ? 'imagen-victoria' : 'imagen-derrota'
})

const btnClase = computed(() => {
  if (esEnamorados.value) return 'btn-enamorados'
  if (esEmpate.value) return 'btn-empate'
  return ''
})

const imagenCelebracion = computed(() => {
  if (esEnamorados.value) return enamoradoscelebrandoImg
  if (esEmpate.value) return empatadoscelebrandoImg
  return bandoGanador.value?.toLowerCase() === 'lobo'
    ? loboscelebrandoImg
    : aldeanoscelebrandoImg
})

const nombreBando = (bando) => {
  const nombres = {
    lobo: 'Los Lobos',
    aldea: 'Los Aldeanos',
    enamorados: 'Los Enamorados'
  }
  return nombres[bando?.toLowerCase()] || bando
}

const limpiarSala = () => {
  store.dispatch('sala/salir')
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
.cuadro-derrota { border: 8px solid var(--color-rojo); }
.cuadro-empate { border: 8px solid white; }
.cuadro-enamorados { border: 8px solid #531e8f; }

.titulo, .subtitulo, .imagen-celebracion, .botones {
  position: relative;
  z-index: 1;
}

.titulo {
  font-family: var(--font-cinzel);
  font-weight: 700;
  font-size: clamp(2rem, 6vw, 4rem);
  text-align: center;
  margin: 24px 15px 8px;
}

.titulo-victoria { color: var(--color-dorado); }
.titulo-derrota { color: var(--color-rojo); }
.titulo-empate { color: #4a90d9; }
.titulo-enamorados { color: #ff69b4; }
.titulo-narrador { color: var(--color-dorado); font-size: clamp(2rem, 4vw, 2.5rem); }

.texto-lobos { color: var(--color-rojo); }
.texto-aldeanos { color: #2d9e2d; }
.texto-enamorados { color: #ff69b4; }

.subtitulo {
  font-family: var(--font-raleway);
  font-size: clamp(1rem, 2vw, 1.2rem);
  text-align: center;
  margin: 0 15px 16px;
  font-weight: 700;
}

.subtitulo-victoria, .subtitulo-narrador { color: var(--color-dorado); }
.subtitulo-derrota { color: var(--color-rojo); }
.subtitulo-empate { color: #4a90d9; }
.subtitulo-enamorados { color: white; }
.fa-heart{color:#ff69b4;}

.imagen-celebracion {
  width: 100%;
  display: block;
  object-fit: cover;
  max-height: 380px;
}

.imagen-victoria { border-top: 5px solid var(--color-dorado); border-bottom: 5px solid var(--color-dorado); }
.imagen-derrota { border-top: 5px solid var(--color-rojo); border-bottom: 5px solid var(--color-rojo); }
.imagen-empate { border-top: 5px solid #4a90d9; border-bottom: 5px solid #4a90d9; }
.imagen-enamorados { border-top: 5px solid #531e8f; border-bottom: 5px solid #5b2d8e; }

.botones { display: flex; gap: 16px; justify-content: center; padding: 24px 15px; }

.btn-resultado {
  background: var(--color-rojo);
  color: white;
  padding: 14px 28px;
  border-radius: 10px;
  font-weight: 700;
  text-decoration: none;
  transition: var(--transition-fast);
}

.btn-resultado:hover { background: white; color: var(--color-rojo); }
.btn-empate { background: #4a90d9; }
.btn-empate:hover { color: #4a90d9; background: white; }
.btn-enamorados { background: #531e8f; }
.btn-enamorados:hover { color: #531e8f; background: white; }
</style>
