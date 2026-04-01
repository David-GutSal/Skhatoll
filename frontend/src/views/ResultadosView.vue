<template>
  <div
    class="contenedor-resultados"
    :class="victoria ? 'fondo-victoria' : 'fondo-derrota'"
  >
    <div class="cuadro-resultados">
      
      <h1 class="titulo" :class="victoria ? 'oro' : 'rojo'">
        {{ victoria ? 'VICTORIA' : 'DERROTA' }}
        <i
          :class="victoria 
            ? 'fa-regular fa-face-grin-beam' 
            : 'fa-regular fa-face-dizzy'"
        ></i>
      </h1>

      <p class="descripcion" :class="victoria ? 'oro' : 'rojo'">
        {{ mensajeDescripcion }}
      </p>

      <img
        :src="imagenResultado"
        class="imagen-resultado"
        :class="victoria ? 'borde-oro' : 'borde-rojo'"
      />

      <div class="botones">
        <button @click="irInicio">INICIO</button>
        <button @click="irSala">¡OTRA PARTIDA!</button>
      </div>

    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "ResultadosView",

  data() {
    return {
      equipoUsuario: null,
      equipoGanador: null,
      estaVivo: null,
      cargando: true,
    };
  },

  computed: {
    victoria() {
      return this.equipoUsuario === this.equipoGanador;
    },

    mensajeDescripcion() {
      if (!this.equipoGanador || !this.equipoUsuario) return "";

      if (this.victoria) {
        return `¡Enhorabuena! El equipo de los ${this.equipoGanador} se ha alzado con la victoria`;
      } else {
        return `¡Lo sentimos! El equipo de los ${this.equipoUsuario} ha perdido`;
      }
    },

    imagenResultado() {
      if (!this.equipoGanador) return "";

      return this.equipoGanador === "aldeanos"
        ? require("@/assets/imgs/aldeanoscelebrando.jpg")
        : require("@/assets/imgs/loboscelebrando.jpg");
    },
  },

  methods: {
    async obtenerResultados() {
      try {
        const response = await axios.get("/api/resultados"); // 🔥 endpoint

        this.equipoUsuario = response.data.equipoUsuario;
        this.equipoGanador = response.data.equipoGanador;
        this.estaVivo = response.data.estaVivo;

      } catch (error) {
        console.error("Error cargando resultados:", error);
      } finally {
        this.cargando = false;
      }
    },

    irInicio() {
      this.$router.push("/");
    },

    irSala() {
      this.$router.push("/sala");
    },
  },

  mounted() {
    this.obtenerResultados();
  },
};
</script>

<style scoped>
.contenedor-resultados {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-size: cover;
  background-position: center;
  padding: 20px;
}

.fondo-victoria {
  background-image: url('@/assets/imgs/fondovictoria.png');
}

.fondo-derrota {
  background-image: url('@/assets/imgs/fondonoche.png');
}

.cuadro-resultados {
  width: 100%;
  max-width: 600px;
  padding: 15px;
  text-align: center;
  position: relative;

  background-image: url('@/assets/imgs/mesa.jpg');
  background-size: cover;
  background-position: center;

  border: 8px solid;
}

.cuadro-resultados::before {
  content: "";
  position: absolute;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.85);
  z-index: 0;
}

.cuadro-resultados > * {
  position: relative;
  z-index: 1;
}

.oro {
  color: gold;
}

.rojo {
  color: red;
}

.cuadro-resultados {
  border-color: white;
}

.fondo-derrota .cuadro-resultados {
  border-color: red;
}

.titulo {
  font-family: 'Cinzel', serif;
  font-size: 32px;
  margin-bottom: 10px;
}

.descripcion {
  font-size: 18px;
  margin-bottom: 15px;
}

.imagen-resultado {
  width: 100%;
  border-radius: 3px;
}

.borde-oro {
  border: 3px solid gold;
}

.borde-rojo {
  border: 3px solid red;
}

.botones {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}

button {
  background-color: red;
  color: white;
  font-weight: bold;
  padding: 10px 15px;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

button:hover {
  background-color: white;
  color: red;
}

button:active {
  transform: scale(0.95);
}

@media (max-width: 600px) {
  .titulo {
    font-size: 24px;
  }

  .descripcion {
    font-size: 16px;
  }
}
</style>