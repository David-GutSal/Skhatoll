<template>
  <!--Para ver la página antes de convertirla para back hay que poner este enlace: http://localhost:5173/lobby?modo=creador--
  Si ahora se pulsa en esta vista y se le da a Iniciar partida, tras esperar 5 segundos te redirige a la pantalla de carga-->
  <!--Para ver la página antes de convertirla para back hay que poner este enlace: http://localhost:5173/lobby?modo=jugador-->
  <div class="contenedor-lobby">

    <div class="grid-lobby">

      <div class="panel-izquierdo">

        <h2 v-if="esCreador">Código sala</h2>
        <h2 v-else>Introduce aquí el código de la sala</h2>

        <input
          class="input-codigo"
          v-model="codigoSala"
          :readonly="esCreador"
        />

        <div class="botones">
          <button v-if="esCreador" class="boton" @click="iniciarPartida">
            Iniciar partida
          </button>

          <button v-if="!esCreador" class="boton" @click="unirseSala">
            Unirse
          </button>

          <button v-if="!esCreador" class="boton-salir" @click="salirSala">
            Salir de la partida
          </button>
        </div>

        <p v-if="mensajeUnion" class="mensaje">
          ¡Te has unido a la partida!<br>
          Ten paciencia y espera al resto de jugadores
        </p>

      </div>

      <div class="panel-jugadores">

        <h2>Jugadores en sala</h2>
        <ul>
          <li v-for="jugador in jugadores" :key="jugador.id">
            <label v-if="esCreador">
              <input type="radio" name="narrador" :checked="jugador.esNarrador">
              {{ jugador.nombre }}
            </label>
            <span v-else>
              {{ jugador.nombre }}
            </span>
          </li>
        </ul>

        <p v-if="esCreador" class="mensaje">
          Puedes seleccionar a otro jugador como narrador o serlo tú mismo
        </p>

      </div>

    </div>
  </div>
</template>

<script>
export default {
  name: "LobbyView",
  data() {
    return {
      esCreador: false,          // cambia según URL
      codigoSala: "PR0F3 4PRU3B4N0S",  // para simular diseño, luego API reemplaza
      mensajeUnion: false,
      jugadores: [
        { id: 1, nombre: "Jugador1", esNarrador: true },
        { id: 2, nombre: "Jugador2", esNarrador: false },
        { id: 3, nombre: "Jugador3", esNarrador: false }
      ]
    };
  },
  created() {

    const modo = this.$route.query.modo;
    this.esCreador = modo === "creador";
  },
  methods: {
    iniciarPartida() {
      setTimeout(() => {
  this.$router.push({ name: 'cargaRol' })
}, 3000)
    },
    unirseSala() {
      this.mensajeUnion = true;
      console.log("Te has unido a la sala");
    },
    salirSala() {
      console.log("Salir de la sala");
    }
  }
};
</script>

<style scoped>
.contenedor-lobby {
  display: flex;
  justify-content: center;
  padding: 40px;
}

.grid-lobby {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  width: 100%;
  max-width: 900px;
}

.panel-izquierdo,
.panel-jugadores {
  background: #1f1f1f;
  padding: 30px;
  border-radius: 10px;
  color: white;
}

.input-codigo {
  width: 100%;
  padding: 12px;
  border-radius: 8px;
  border: none;
  margin-top: 10px;
  margin-bottom: 20px;
}

.botones {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.boton {
  background: #8b0000;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 8px;
  cursor: pointer;
}

.boton:hover {
  background: #a30000;
}

.boton-salir {
  background: #333;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 8px;
  cursor: pointer;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  padding: 8px 0;
}

.mensaje {
  margin-top: 15px;
  font-size: 14px;
}

@media(max-width:768px){
  .grid-lobby{
    grid-template-columns:1fr;
  }
}
</style>