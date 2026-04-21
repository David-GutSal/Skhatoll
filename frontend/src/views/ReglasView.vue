<template>
  <main class="reglas">
    <div class="reglas-inner">

      <!-- PARTE 1 -->
      <section class="seccion-intro">
        <h1 class="intro-titulo">REGLAS Y JUEGOS</h1>
        <p class="intro-texto">En esta sección se recopilan las reglas principales del juego así como una guía de seguimiento de la partida y cosas a tener en cuenta.</p>
        <p class="intro-texto">También hay una sección donde pueden verse expansiones y juegos similares a Los Hombres Lobo de Castronegro.</p>
      </section>

      <!-- PARTE 2: Fichas -->
      <section class="fichas-grid mb-5">
        <div class="ficha">
          <img src="@/assets/imgs/medalla-alcalde.jpg" alt="Alcalde" class="ficha-img" />
          <h3 class="ficha-titulo">La figura del Alcalde / Alguacil</h3>
          <p class="ficha-texto">Este cargo se puede asignar a cualquier jugador. Los jugadores presentan sus candidaturas y después serán votados. Ganará el jugador con más votos. Su función principal es la de desempatar en las votaciones. El cargo de alguacil tiene las siguientes características:</p>
          <ul class="ficha-lista">
            <li>No se puede rechazar el honor de ser Alguacil.</li>
            <li>El voto de este jugador contará como doble.</li>
            <li>Si después de una votación hay un empate, el Alcalde decide quién será la víctima a linchar.</li>
            <li>Si el Alguacil es eliminado, antes de morir elige a su sucesor en el cargo.</li>
          </ul>
        </div>
        <div class="ficha">
          <img src="@/assets/imgs/linchamiento.jpg" alt="Linchamiento" class="ficha-img" />
          <h3 class="ficha-titulo">Votación por linchamiento</h3>
          <p class="ficha-texto">Estas votaciones ocurren durante el día y son la manera más eficaz de los aldeanos de eliminar a los lobos. En las votaciones por linchamiento los jugadores son libres de argumentar quién piensan que es un lobo o defenderse a ellos mismos de una acusación. Tras los debates empezarán las votaciones en las que los jugadores votarán a los presuntos lobos (puedes votarte a ti mismo).</p>
          <p class="ficha-texto">En caso de empate será el alcalde el que decida qué persona será linchada. Esa persona linchada normalmente revelará su rol y quedará eliminada de la partida.</p>
        </div>
        <div class="ficha">
          <img src="@/assets/imgs/diaynoche.jpg" alt="Fases del juego" class="ficha-img" />
          <h3 class="ficha-titulo">Las fases del juego</h3>
          <p class="ficha-texto">Cada turno del juego transcurre en 2 fases completas de día y noche siempre guiadas por un narrador omnisciente que sabrá los roles que interpreta cada personaje.</p>
          <p class="ficha-texto"><strong class="icono-ficha"><i class="fa-solid fa-moon"></i> Durante la noche:</strong> los jugadores cierran los ojos y son llamados por el narrador de forma ordenada para que usen sus poderes o sean informados de algo.</p>
          <p class="ficha-texto"><strong class="icono-ficha"><i class="fa-solid fa-sun"></i> Durante el día:</strong> Los aldeanos abrirán los ojos y se revelarán las víctimas de la noche. Después debatirán y harán votaciones para eliminar a los lobos.</p>
        </div>
      </section>

      <!-- PARTE 3: Tabla + Carrusel -->
      <section class="tabla-consejos-wrapper mb-4">

        <div class="seccion-tabla">
          <h2 class="tabla-titulo">Distribución de personajes en partidas</h2>
          <p class="tabla-texto">Esta tabla es una guía básica para equilibrar ambos bandos. Se utilizan los personajes básicos e indispensables del juego.</p>
          <p class="tabla-texto">Si se quieren meter más personajes siempre hay que buscar un equilibrio entre personajes buenos y malos teniendo en cuenta el poder de los personajes ambiguos.</p>
          <div class="tabla-wrapper">
            <table class="tabla-juego">
              <thead>
                <tr class="fila-cabecera">
                  <th>Jugadores (sin narrador)</th>
                  <th><i class="fa-brands fa-wolf-pack-battalion"></i> Lobos</th>
                  <th><i class="fa-solid fa-eye"></i> Vidente</th>
                  <th><i class="fa-solid fa-wheat-awn"></i> Aldeanos</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(fila, i) in tablaJugadores" :key="i" :class="i % 2 === 0 ? 'fila-gris' : 'fila-roja'">
                  <td>{{ fila[0] }}</td>
                  <td>{{ fila[1] }}</td>
                  <td>{{ fila[2] }}</td>
                  <td>{{ fila[3] }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div class="seccion-consejos">
          <div
            class="tarjeta-consejo"
            :class="{ 'tarjeta-portada': indiceTarjeta === 0 }"
            :style="indiceTarjeta !== 0 ? { backgroundColor: tarjetaActual.color } : {}"
          >
            <template v-if="indiceTarjeta === 0">
              <div class="portada-overlay">
                <div class="caja-blanca">
                  <h3 class="portada-titulo" :style="{ color: tarjetaActual.colorTitulo }">
                    {{ tarjetaActual.titulo }}
                  </h3>
                </div>
              </div>
            </template>

            <template v-else>
              <div class="consejo-contenido">
                <div v-if="tarjetaActual.titulo" class="caja-blanca consejo-titulo-caja">
                  <h3 class="consejo-titulo" :style="{ color: tarjetaActual.colorTitulo }">
                    {{ tarjetaActual.titulo }}
                  </h3>
                </div>
                <div
                  v-for="(parrafo, i) in tarjetaActual.textos"
                  :key="i"
                  class="caja-blanca consejo-parrafo-caja"
                >
                  <p class="consejo-parrafo" :style="{ color: tarjetaActual.colorTexto }">
                    <i class="fa-solid fa-file-pen icono-parrafo" :style="{ color: tarjetaActual.colorTexto }"></i>
                    {{ parrafo }}
                  </p>
                </div>
              </div>
            </template>
          </div>

          <div class="consejos-nav">
            <button class="btn-nav" @click="anteriorTarjeta">
              <i class="fa-solid fa-chevron-left"></i>
            </button>
            <span class="consejos-contador">{{ indiceTarjeta + 1 }} / {{ tarjetas.length }}</span>
            <button class="btn-nav" @click="siguienteTarjeta">
              <i class="fa-solid fa-chevron-right"></i>
            </button>
          </div>
        </div>

      </section>

      <!-- PARTE 4: Lista de reglas -->
      <section class="mb-4">
        <ListaReglas />
      </section>

      <!-- PARTE 5: Carrusel de juegos -->
      <section class="seccion-juegos mb-4">
        <div class="juegos-header">
          <h2 class="juegos-titulo">Si te gustó Los Hombres Lobo de Castronegro te gustarán...</h2>
        </div>
        <div class="carrusel-wrapper">
          <button class="btn-carrusel" @click="anteriorJuego">
            <i class="fa-solid fa-chevron-left"></i>
          </button>
          <div class="carrusel-pista">
            <div
              v-for="juego in juegosVisibles"
              :key="juego.titulo"
              class="juego-ficha"
              :style="{ backgroundImage: 'url(' + juego.imagen + ')' }"
            >
              <div class="juego-info">
                <a :href="juego.enlace" target="_blank" class="juego-titulo-link">{{ juego.titulo }}</a>
                <p class="juego-texto">{{ juego.texto }}</p>
              </div>
            </div>
          </div>
          <button class="btn-carrusel" @click="siguienteJuego">
            <i class="fa-solid fa-chevron-right"></i>
          </button>
        </div>
      </section>

    </div>
  </main>
</template>

<script>
import ListaReglas from '@/components/juego/ListaReglas.vue'
import imgBloodclock from '@/assets/imgs/juego-bloodclock.jpg'
import imgSamurai from '@/assets/imgs/juego-samurai.jpg'
import imgAsamblea from '@/assets/imgs/juego-asambleadelmal.jpg'
import imgAldea from '@/assets/imgs/juego-laaldea.jpg'
import imgUltimate from '@/assets/imgs/juego-ultimatewerewolf.jpg'
import imgLuna from '@/assets/imgs/juego-lunanueva.png'
import imgBang from '@/assets/imgs/juego-bang.jpg'
import slideImg from '@/assets/imgs/slide.jpeg'

export default {
  name: 'ReglasView',
  components: { ListaReglas },

  data() {
    return {
      indiceTarjeta: 0,
      indiceJuego: 0,
      fichasPorPantalla: 3,
      slideImg,

      tablaJugadores: [
        [8,2,1,5],[9,2,1,6],[10,2,1,7],[11,2,1,8],
        [12,3,1,8],[13,3,1,9],[14,3,1,10],[15,3,1,11],
        [16,3,1,12],[17,3,1,13],[18,4,1,13],
      ],

      tarjetas: [
        { color: '#3a3a3a', colorTitulo: '#cc0000', colorTexto: '#cc0000', titulo: 'PREPARACIÓN Y CONSEJOS', textos: [] },
        { color: '#3a3a3a', colorTitulo: '#cc0000', colorTexto: '#111', titulo: 'PREPARACIÓN', textos: ['Los jugadores eligen un narrador que no juega pero guiará la partida. Este narrador repartirá las cartas e iniciará la partida.'] },
        { color: '#cc0000', colorTitulo: '#000', colorTexto: '#000', titulo: 'Consejos al narrador', textos: ['Tu papel es fundamental: de tu talento depende el ambiente del juego. Guiados por ti, los jugadores se van a divertir. No dudes en crear una atmósfera de angustia. Crea suspense cuando reveles las víctimas de los Hombres Lobo. Y aviva los debates si decaen.'] },
        { color: '#cc0000', colorTitulo: '#000', colorTexto: '#000', titulo: '', textos: ['Las frases de llamada nocturna de los personajes solo son ejemplos. Cuando domines bien la técnica, déjate llevar por tu propia inspiración.', 'Con jugadores experimentados, aunque no seáis muchos, no dudes en utilizar los personajes con poderes especiales, porque añade interés a las partidas.'] },
        { color: '#cc0000', colorTitulo: '#000', colorTexto: '#000', titulo: '', textos: ['Con jugadores novatos, introduce poco a poco las otras cartas de personajes. Las partidas son relativamente cortas, jugad varias seguidas y variad los personajes.', 'Cuando hables, ten cuidado de no dar pistas de la identidad de los personajes.', 'Por la noche, cuando te dirijas a los personajes, no dirijas la voz hacia ellos; el resto de jugadores podría deducir su posición en la mesa.'] },
        { color: '#cc0000', colorTitulo: '#000', colorTexto: '#000', titulo: '', textos: ['Si los hombres lobo no consiguen ponerse de acuerdo o no se deciden, mala suerte para ellos, porque esa noche no habrá víctima.', 'Cuando la Vidente espía, haz como que le das la vuelta a las cartas de todos los jugadores.', 'Puedes esperar varios turnos antes de hacer elegir por votación al Alguacil.'] },
        { color: '#cc0000', colorTitulo: '#000', colorTexto: '#000', titulo: 'Consejos para jugadores', textos: ['Los Hombres Lobo: una estrategia eficaz para librarse de una acusación es votar contra tu propio compañero.', 'La Vidente: cuidado, si has descubierto un Hombre Lobo, a lo mejor vale la pena descubrir tu identidad para acusar a un jugador, pero no demasiado pronto.'] },
        { color: '#cc0000', colorTitulo: '#000', colorTexto: '#000', titulo: '', textos: ['La Niña Pequeña: es un personaje muy poderoso, pero muy angustioso de jugar. No dudes en espiar: da miedo pero hay que aprovecharlo rápidamente antes de ser eliminado.', 'El Cazador: siempre es bueno, en caso de acusación, hacerse pasar por el Cazador.', 'Cupido: si te designas a ti mismo como enamorado, no escojas un compañero sentimental bocazas.'] },
        { color: '#cc0000', colorTitulo: '#000', colorTexto: '#000', titulo: '', textos: ['El Alguacil: no dudes en presentarte al cargo de Alguacil, haz campaña. Pero si eres Hombre Lobo, no busques el puesto demasiado abiertamente.', 'La Bruja: tu personaje va cobrando importancia a medida que se acerca el final de la partida. No desperdicies tus hechizos.'] },
      ],

      juegos: [
        { titulo: 'Blood on the Clocktower', enlace: 'https://bloodontheclocktower.com/', imagen: imgBloodclock, texto: 'Un juego de deducción social para 5-20 jugadores ambientado en un pueblo victoriano. Ofrece roles únicos y la posibilidad de participar incluso después de morir.' },
        { titulo: 'Samurai Sword', enlace: 'https://www.youtube.com/watch?v=udCXta6aW0E', imagen: imgSamurai, texto: 'Un juego de cartas para 3-7 jugadores ambientado en el Japón feudal donde samurái, shōgun, ninjas y rōnin luchan por el poder con roles ocultos y alianzas cambiantes.' },
        { titulo: 'La Asamblea del Mal', enlace: 'https://www.youtube.com/watch?v=aTOuLHM4LiU', imagen: imgAsamblea, texto: 'Un juego de roles ocultos para 5-10 jugadores donde los villanos intentan infiltrarse en el consejo de héroes. Combina deducción con habilidades especiales.' },
        { titulo: 'Los Hombres Lobo de Castronegro: La Aldea', enlace: 'https://es.scribd.com/document/444169570/La-Aldea-de-Castronegro', imagen: imgAldea, texto: 'Expansión oficial con nuevos personajes y escenarios de juego que amplían las posibilidades estratégicas de cada partida.' },
        { titulo: 'One Night Ultimate Werewolf', enlace: 'https://beziergames.com/collections/one-night-ultimate-werewolf', imagen: imgUltimate, texto: 'Una versión rápida e intensa donde toda la partida ocurre en una sola noche. Ideal para grupos de 3-10 jugadores que quieren disfrutar de múltiples partidas.' },
        { titulo: 'Los Hombres Lobo de Castronegro: Luna Nueva', enlace: 'https://es.scribd.com/document/389540969/Hombres-Lobo-de-Castronegro-Luna-Nueva-Reglamento-Cartas', imagen: imgLuna, texto: 'Expansión del juego que incorpora nuevos personajes con poderes únicos, añadiendo capas estratégicas y sorpresas a cada partida.' },
        { titulo: 'BANG!', enlace: 'https://www.youtube.com/watch?v=1gRPOZKz5Y4', imagen: imgBang, texto: 'Un clásico juego de cartas del Oeste para 2-7 jugadores con roles ocultos. El Sheriff, los Forajidos, el Renegado y los Ayudantes luchan en duelos llenos de acción.' },
      ],
    }
  },

  computed: {
    tarjetaActual() {
      return this.tarjetas[this.indiceTarjeta]
    },
    juegosVisibles() {
      const total = this.juegos.length
      const result = []
      for (let i = 0; i < this.fichasPorPantalla; i++) {
        result.push(this.juegos[(this.indiceJuego + i) % total])
      }
      return result
    },
  },

  mounted() {
    this.actualizarFichasPorPantalla()
    window.addEventListener('resize', this.actualizarFichasPorPantalla)
  },

  beforeUnmount() {
    window.removeEventListener('resize', this.actualizarFichasPorPantalla)
  },

  methods: {
    anteriorTarjeta() {
      this.indiceTarjeta = (this.indiceTarjeta - 1 + this.tarjetas.length) % this.tarjetas.length
    },
    siguienteTarjeta() {
      this.indiceTarjeta = (this.indiceTarjeta + 1) % this.tarjetas.length
    },
    anteriorJuego() {
      this.indiceJuego = (this.indiceJuego - 1 + this.juegos.length) % this.juegos.length
    },
    siguienteJuego() {
      this.indiceJuego = (this.indiceJuego + 1) % this.juegos.length
    },
    actualizarFichasPorPantalla() {
      if (window.innerWidth < 600) {
        this.fichasPorPantalla = 1
      } else if (window.innerWidth < 992) {
        this.fichasPorPantalla = 2
      } else {
        this.fichasPorPantalla = 3
      }
    },
  },
}
</script>

<style scoped>
.reglas {
  width: 100%;
  padding: 20px 0 40px;
}

.reglas-inner {
  width: 85%;
  margin: 25px auto;
}

/* PARTE 1 */
.seccion-intro {
  background: #1f1f1f;
  border-radius: 15px;
  padding: 50px;
  text-align: center;
  margin-bottom: 35px;
}

.intro-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 2.5rem;
  font-weight: 700;
  color: white;
  margin-bottom: 20px;
}

.intro-texto {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1.2rem;
  color: white;
  line-height: 1.6;
  margin: 0;
}

/* PARTE 2 */
.fichas-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
  margin-bottom: 2rem;
}

@media (max-width: 1240px) and (min-width: 768px) {
  .fichas-grid {
    grid-template-columns: repeat(2, 1fr);
    justify-items: center;
  }
  .fichas-grid .ficha:last-child:nth-child(odd) {
    grid-column: 1 / -1;
    max-width: calc(50% - 20px);
    width: 100%;
  }
}

@media (max-width: 767px) {
  .fichas-grid {
    grid-template-columns: 1fr;
  }
  .fichas-grid .ficha:last-child:nth-child(odd) {
    max-width: 100%;
    grid-column: auto;
  }
}

.ficha {
  background: #000;
  border-radius: 15px;
  padding: 35px;
  display: flex;
  flex-direction: column;
  width: 100%;
}

.ficha-img {
  width: 70%;
  margin: 0 auto 18px;
  aspect-ratio: 1 / 1;
  object-fit: cover;
  border: 4px solid #cc0000;
  border-radius: 15px;
  display: block;
}

.ficha-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.9rem;
  font-weight: bold;
  color: #cc0000;
  text-align: center;
  margin-bottom: 15px;
}

.ficha-texto {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1rem;
  color: white;
  text-align: left;
  line-height: 1.6;
  margin-bottom: 12px;
}

.ficha-lista {
  list-style: square;
  padding-left: 18px;
  margin: 0;
}

.ficha-lista li {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 0.9rem;
  color: white;
  line-height: 1.6;
  margin-bottom: 4px;
}

.ficha-lista li::marker { color: #e4ba03; }
.icono-ficha { color: #cc0000; }

/* PARTE 3: Tabla + Consejos en fila, debajo de 1420px */
.tabla-consejos-wrapper {
  display: flex;
  flex-direction: row;
  gap: 20px;
  align-items: flex-start;
}

.seccion-tabla {
  background: #000;
  border-radius: 15px;
  padding: 45px;
  flex: 3;
  min-width: 0;
}

.seccion-consejos {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

@media (max-width: 1420px) {
  .tabla-consejos-wrapper {
    flex-direction: column;
  }
  .seccion-tabla,
  .seccion-consejos {
    flex: none;
    width: 100%;
  }
  .seccion-tabla { padding: 30px; }
}

@media (max-width: 767px) {
  .seccion-tabla { padding: 20px; }
}

.tabla-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 2rem;
  font-weight: 700;
  color: #cc0000;
  text-align: center;
  margin-bottom: 16px;
}

.tabla-texto {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1.2rem;
  color: white;
  margin-bottom: 15px;
  line-height: 1.3;
}

.tabla-wrapper {
  background: #3a3a3a;
  border-radius: 15px;
  border: 6px solid #cc0000;
  overflow: hidden;
  margin-top: 20px;
  width: 100%;
}

.tabla-juego {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.fila-cabecera th {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.7rem;
  color: white;
  font-weight: bold;
  background: #e4ba03;
  padding: 16px 6px;
  text-align: center;
  word-break: break-word;
  hyphens: auto;
}

.tabla-juego td {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1.9rem;
  font-weight: 700;
  color: white;
  padding: 14px 6px;
  text-align: center;
}

.fila-gris { background: #3a3a3a; }
.fila-roja { background: #cc0000; }

@media (max-width: 1120px) {
  .fila-cabecera th { font-size: 1.1rem; padding: 12px 4px; }
}

@media (max-width: 600px) {
  .fila-cabecera th { font-size: 0.85rem; padding: 8px 3px; }
  .tabla-juego td { font-size: 1.1rem; padding: 10px 4px; }
  .tabla-texto { font-size: 1rem; }
}

/* Carrusel consejos */
.tarjeta-consejo {
  flex: 1;
  border-radius: 15px;
  border: 5px solid #e4ba03;
  min-height: 300px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
}

.tarjeta-portada {
  background-image: v-bind("'url(' + slideImg + ')'");
  background-size: cover;
  background-position: center;
}

/* Caja blanca reutilizable */
.caja-blanca {
  background: rgba(255, 255, 255, 0.7);
  padding: 12px 15px;
  border-radius: 8px;
}

.portada-overlay {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.portada-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 2rem;
  font-weight: bold;
  margin: 0;
  text-align: center;
}

/* Contenido tarjetas normales */
.consejo-contenido {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 20px;
  flex: 1;
}

.consejo-titulo-caja {
  margin-bottom: 4px;
}

.consejo-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.8rem;
  font-weight: bold;
  margin: 0;
}

.consejo-parrafo-caja {
  flex-shrink: 0;
}

.consejo-parrafo {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1.2rem;
  font-weight: 600;
  line-height: 1.6;
  margin: 0;
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.icono-parrafo {
  margin-top: 6px;
  flex-shrink: 0;
}

.consejos-nav {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  padding: 20px 0 0;
}

.btn-nav {
  background: none;
  border: none;
  color: #e4ba03;
  font-size: 2.2rem;
  cursor: pointer;
  transition: transform 0.25s ease;
}

.btn-nav:hover { transform: scale(1.2); }

.consejos-contador {
  font-family: 'Raleway', Arial, sans-serif;
  color: white;
  font-size: 1.4rem;
}

/* PARTE 5 */
.seccion-juegos {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.juegos-header {
  background: #000;
  border-radius: 15px;
  padding: 35px;
  text-align: center;
}

.juegos-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 2rem;
  font-weight: bold;
  color: #e4ba03;
  margin: 0;
}

.carrusel-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-carrusel {
  background: none;
  border: none;
  color: #e4ba03;
  font-size: 2rem;
  cursor: pointer;
  flex-shrink: 0;
  transition: transform 0.30s ease;
}

.btn-carrusel:hover {
  transform: scale(1.2);

}

.carrusel-pista {
  display: flex;
  gap: 16px;
  flex: 1;
}

.juego-ficha {
  flex: 1;
  height: 280px;
  background-size: cover;
  background-position: center;
  border-radius: 12px;
  display: flex;
  align-items: flex-end;
  overflow: hidden;
}

.juego-info {
  background: rgba(0, 0, 0, 0.85);
  border-radius: 10px;
  padding: 20px;
  width: 100%;
}

.juego-titulo-link {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1rem;
  font-weight: bold;
  color: #e4ba03;
  text-decoration: none;
  display: block;
  margin-bottom: 6px;
  transition: color 0.2s ease;
  cursor: pointer;
}

.juego-titulo-link:hover {color: #cc0000; }

.juego-texto {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 0.8rem;
  color: white;
  margin: 0;
  line-height: 1.6;
}

@media (max-width: 992px) {
  .reglas-inner { width: 90%; }
  .juegos-titulo { font-size: 1.4rem; }
}

@media (max-width: 600px) {
  .reglas-inner { width: 95%; }
  .seccion-intro { padding: 20px; }
  .intro-titulo { font-size: 1.6rem; }
  .intro-texto { font-size: 1rem; }
  .tarjeta-consejo { min-height: 200px; }
  .consejo-parrafo { font-size: 1rem; }
  .consejo-titulo { font-size: 1.2rem; }
  .portada-titulo { font-size: 1.2rem; }
  .juegos-titulo { font-size: 1.3rem; }
  .juego-ficha { height: 220px; }
}
</style>