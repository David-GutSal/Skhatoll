<template>
  <div class="lista-reglas">
    <h2 class="reglas-titulo">Objetivos y turnos</h2>

    <div class="desplegable-grupo">
      <button class="desplegable-header dorado" @click="toggleDesplegable('objetivos')">
        <span class="desplegable-nombre">Objetivos del juego</span>
        <i :class="abiertos.objetivos ? 'fa-solid fa-chevron-up' : 'fa-solid fa-chevron-down'"></i>
      </button>
      <transition name="desplegable">
        <div v-if="abiertos.objetivos" class="desplegable-contenido">
          <p class="reglas-texto">Los jugadores se dividen en dos bandos principales:</p>
          <p class="reglas-texto"><i class="fa-solid fa-user-group icono-rojo"></i> <strong>Los Aldeanos (mayoría):</strong> Su objetivo es descubrir y eliminar a todos los Hombres Lobo.</p>
          <p class="reglas-texto"><i class="fa-brands fa-wolf-pack-battalion icono-rojo"></i> <strong>Los Hombres Lobo (minoría):</strong> Su objetivo es eliminar a todos los Aldeanos sin ser descubiertos.</p>
          <p class="reglas-texto">Los Hombres Lobo se conocen entre sí y actúan juntos por la noche. La mayoría de los Aldeanos no saben quiénes son los lobos, por lo que deben usar la deducción, el debate y la sospecha para identificarlos. La partida termina cuando:</p>
          <p class="reglas-texto"><i class="fa-solid fa-trophy icono-rojo"></i> Todos los Hombres Lobo han sido eliminados → Victoria de los Aldeanos.</p>
          <p class="reglas-texto"><i class="fa-solid fa-trophy icono-rojo"></i> Todos los Aldeanos han sido eliminados → Victoria de los Hombres Lobo.</p>
          <p class="reglas-texto"><i class="fa-solid fa-trophy icono-rojo"></i> Se cumple la condición especial de algún personaje con poderes.</p>
        </div>
      </transition>
    </div>

    <div class="desplegable-grupo">
      <button class="desplegable-header rojo" @click="toggleDesplegable('resumen')">
        <span class="desplegable-nombre">Resumen de turnos</span>
        <i :class="abiertos.resumen ? 'fa-solid fa-chevron-up' : 'fa-solid fa-chevron-down'"></i>
      </button>
      <transition name="desplegable">
        <div v-if="abiertos.resumen" class="desplegable-contenido">
          <img src="@/assets/imgs/resumenturnos.png" alt="Resumen de turnos" class="img-resumen" />
        </div>
      </transition>
    </div>

    <div class="desplegable-grupo">
      <button class="desplegable-header rojo" @click="toggleDesplegable('preparacion')">
        <span class="desplegable-nombre">Turno de Preparación</span>
        <i :class="abiertos.preparacion ? 'fa-solid fa-chevron-up' : 'fa-solid fa-chevron-down'"></i>
      </button>
      <transition name="desplegable">
        <div v-if="abiertos.preparacion" class="desplegable-contenido">
          <p class="reglas-texto intro-cursiva">Es el turno previo a empezar la partida, se reparten los roles, se presentan a los personajes y algunos de los personajes especiales utilizan sus poderes.</p>
          <p class="reglas-texto">1. Tras repartir las cartas, el narrador duerme a la aldea e irá llamando a cada personaje para ver el rol que le ha tocado a cada uno.</p>
          <p class="reglas-texto">2. El narrador llama al Ladrón (En caso de existir).</p>
          <p class="reglas-texto narrador-voz">"El narrador dice: 'El ladrón se despierta'. El jugador que tiene la carta Ladrón abre los ojos y mira discretamente las dos cartas que hay en el medio de la mesa, y cambia si quiere de personaje. El narrador dice 'El Ladrón vuelve a dormir'. El Ladrón vuelve a cerrar los ojos."</p>
          <p class="reglas-texto">3. El narrador llama a Cupido (En caso de existir).</p>
          <p class="reglas-texto narrador-voz">"El narrador dice 'Cupido se despierta'. Cupido abre los ojos y designa a dos jugadores (de los cuales uno puede ser él mismo). El narrador da una vuelta a la mesa y toca discretamente el hombro de los dos enamorados. El narrador dice 'Cupido vuelve a dormir'. Cupido vuelve a cerrar los ojos."</p>
          <p class="reglas-texto">4. El narrador llama a los Enamorados (Si existe Cupido).</p>
          <p class="reglas-texto narrador-voz">"El narrador dice 'Los enamorados se despiertan, se reconocen y se vuelven a dormir'. No se muestran sus cartas, de manera que cada jugador ignora la verdadera personalidad del ser amado. Tras esto, el narrador sigue con el turno normal."</p>
        </div>
      </transition>
    </div>

    <div class="desplegable-grupo">
      <button class="desplegable-header rojo" @click="toggleDesplegable('normal')">
        <span class="desplegable-nombre">Turno Normal</span>
        <i :class="abiertos.normal ? 'fa-solid fa-chevron-up' : 'fa-solid fa-chevron-down'"></i>
      </button>
      <transition name="desplegable">
        <div v-if="abiertos.normal" class="desplegable-contenido">
          <p class="reglas-texto intro-cursiva">Varía según la elección de personajes en juego y su supervivencia en la partida, pero los personajes son llamados siempre en este orden:</p>
          <p class="reglas-texto">1. El narrador llama a la Vidente.</p>
          <p class="reglas-texto narrador-voz">"El narrador dice: '¡La vidente se despierta, y señala a un jugador del que quiere conocer la verdadera personalidad!' El narrador muestra a la Vidente la carta del jugador designado. El narrador dice 'la vidente se vuelve a dormir'. La Vidente vuelve a cerrar los ojos."</p>
          <p class="reglas-texto">2. El narrador llama a los Hombres Lobo.</p>
          <p class="reglas-texto narrador-voz">"El narrador dice: '¡Los Hombres lobo se despiertan, se reconocen y designan una nueva víctima!' Los Hombres Lobo (y solamente ellos) levantan la cabeza, abren los ojos, se ponen de acuerdo en silencio y designan una nueva víctima. Durante este turno, la Niña Pequeña puede espiar a los Hombres Lobo."</p>
          <p class="reglas-texto narrador-voz">"Si la sorprenden, los Hombres Lobo tienen derecho a cambiar de opinión y la Niña Pequeña muere en lugar de la víctima previamente escogida. El narrador dice: 'los Hombres Lobo saciados vuelven a dormirse y sueñan con próximas y sabrosas víctimas'. Los Hombres Lobo vuelven a cerrar los ojos."</p>
          <p class="reglas-texto">3. El Narrador llama a la Bruja.</p>
          <p class="reglas-texto narrador-voz">"El narrador dice: 'La Bruja se despierta, le muestro la víctima de los Hombres Lobo. ¿Usará su poción curativa o su poción venenosa?'. El narrador señala a la Bruja la víctima de los Hombres Lobo. La bruja no está obligada a usar su poder en un turno específico. Si usa una poción, debe designar al narrador su destinatario, con el pulgar hacia arriba para la curación, con el pulgar hacia abajo para el envenenamiento. El narrador revelará el eventual efecto de la poción la mañana siguiente."</p>
          <p class="reglas-texto">4. El narrador despierta a toda la aldea.</p>
          <p class="reglas-texto narrador-voz">"El narrador dice: 'Amanece en la aldea, todo el mundo despierta y abre los ojos, todo el mundo salvo...' El narrador anuncia entonces el o los jugadores que han sido víctimas de los hombres lobo o de la bruja durante la noche."</p>
          <p class="reglas-texto">Este o estos jugadores revelan su carta porque han sido eliminados del juego. No podrán comunicarse con el resto de jugadores de ninguna forma.</p>
          <ul class="reglas-lista">
            <li>Si uno de estos jugadores es el Cazador, tiene derecho a replicar, eliminando inmediatamente a otro jugador a su elección.</li>
            <li>Si uno de los jugadores es uno de los dos Enamorados, el otro enamorado muere de pena inmediatamente.</li>
            <li>Si uno de los jugadores es el Alguacil, designa a su sucesor.</li>
          </ul>
          <p class="reglas-texto">5. La Aldea entera debate y busca sospechosos.</p>
          <p class="reglas-texto">El narrador debe animar y avivar los debates. Un ruido sospechoso durante la noche, un comportamiento extraño con respecto a otro jugador o una manera de votar siempre idéntica de ciertos aldeanos: he aquí posibles indicios que pueden llevar a sospechar que ciertos jugadores son Hombres Lobo.</p>
          <p class="reglas-texto">En el transcurso de esta fase no hay que perder de vista que los objetivos de cada uno son diferentes:</p>
          <ul class="reglas-lista">
            <li>Cada Aldeano intenta desenmascarar un Hombre Lobo y hacer que todos voten contra él.</li>
            <li>Los Hombres Lobo deben hacerse pasar por Aldeanos.</li>
            <li>La Vidente y la Niña Pequeña tienen que ayudar a los otros aldeanos, pero sin poner demasiado pronto su vida en peligro al descubrir su identidad.</li>
            <li>Los enamorados deben protegerse mutuamente.</li>
          </ul>
          <p class="reglas-texto">Cualquiera se puede hacer pasar por alguien que no es. Aquí comienza el juego de verdad. Haced notar vuestro talento de oradores, tiraos faroles o decid la verdad, pero sed siempre creíbles.</p>
          <p class="reglas-texto">6. La aldea vota.</p>
          <p class="reglas-texto">Los jugadores deben eliminar a un jugador sospechoso de ser Hombre Lobo. A la señal del narrador, cada jugador vota al jugador que quiera eliminar señalándolo con el dedo. El jugador que reciba más votos es eliminado. No olvidéis que el voto del Alguacil cuenta doble.</p>
          <p class="reglas-texto">En caso de empate, si está presente, el voto del alguacil decide. Si no es así, se vota otra vez. Si sigue habiendo empate, ningún jugador es eliminado. Si el jugador linchado es el Cazador, eliminará a su vez a otro jugador inmediatamente.</p>
          <p class="reglas-texto">7. La aldea vuelve a dormir.</p>
          <p class="reglas-texto narrador-voz">"El narrador dice: 'Se hace de noche, los supervivientes se vuelven a dormir'; los jugadores vuelven a cerrar los ojos (los jugadores eliminados se callan, sobre todo al descubrir quiénes son los hombres lobo)."</p>
          <p class="reglas-texto">El juego se reanuda al principio del turno normal, etapa 1.</p>
        </div>
      </transition>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ListaReglas',
  data() {
    return {
      abiertos: {
        objetivos: false,
        resumen: false,
        preparacion: false,
        normal: false,
      },
    }
  },
  methods: {
    toggleDesplegable(clave) {
      this.abiertos[clave] = !this.abiertos[clave]
    },
  },
}
</script>

<style scoped>
.lista-reglas {
  background: #2a2a2a;
  border-radius: 15px;
  padding: 50px;
}

.reglas-titulo {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 2rem;
  font-weight: 700;
  color: white;
  text-align: center;
  margin-bottom: 30px;
}

.desplegable-grupo {
  margin-bottom: 12px;
}

.desplegable-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  background: none;
  border: none;
  border-bottom: 2px solid currentColor;
  padding: 10px 0;
  cursor: pointer;
  text-align: left;
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.5rem;
  font-weight: 700;
}

.dorado { color: #e4ba03; }
.rojo { color: #cc0000; }

.desplegable-nombre {
  text-decoration: underline;
}

.desplegable-contenido {
  padding: 16px 0 8px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.reglas-texto {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1rem;
  color: white;
  line-height: 1.6;
  margin: 0;
  text-align: left;
}

.icono-rojo {
  color: #cc0000;
}

.intro-cursiva {
  font-style: italic;
  text-align: center;
}

.narrador-voz {
  color: #cc0000;
  font-style: italic;
  padding-left: 16px;
  border-left: 3px solid #cc0000;
}

.reglas-lista {
  list-style: square;
  padding-left: 24px;
  margin: 0;
  color: #cc0000;
}

.reglas-lista li {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 1rem;
  color: white;
  line-height: 1.6;
  margin-bottom: 4px;
}

.img-resumen {
  display: block;
  max-width: 100%;
  margin: 0 auto;
  border: 4px solid #e4ba03;
  border-radius: 10px;
}

.desplegable-enter-active,
.desplegable-leave-active {
  transition: opacity 0.3s ease;
}

.desplegable-enter-from,
.desplegable-leave-to {
  opacity: 0;
}

@media (max-width: 600px) {
  .lista-reglas {
    padding: 20px;
  }
  .desplegable-header {
    font-size: 1.1rem;
  }
}
</style>