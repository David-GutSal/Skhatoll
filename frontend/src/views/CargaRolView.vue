<template>
  <div class="contenedor-carga">

    <div class="tarjeta-rol">

<!-- Plantilla por defecto para luego cambiarla -->
      <img
        class="imagen-personaje"
        :src="imagenUrl"
        alt="Bruja"
        />
    
      <div class="info-rol">
        <h2>Tu rol es: BRUJA</h2>
        <p class="descripcion">
          La Bruja puede salvar a un jugador por la noche o envenenar a otro. Usa tu habilidad sabiamente.
        </p>
        <p class="bando">Bando: BUENO</p>
      </div>

    </div>

    <p class="mensaje-espera">
      Espera a que el narrador inicie la partida...
    </p>

    <div class="slider-frases">
      <p class="frase" v-for="(frase, index) in frases" :key="index" v-show="index === fraseActual">
        "{{ frase }}"
      </p>
    </div>


  </div>
</template>

<script>
// Plantilla
import BrujaImg from '@/assets/imgs/bruja.jpg'

export default {
  name: "CargaRolView",
  data() {
    return {
      nombreRol: "BRUJA",
      descripcionRol: "La Bruja puede salvar a un jugador por la noche o envenenar a otro. Usa tu habilidad sabiamente.",
      bando: "BUENO",
      imagenUrl: BrujaImg,
      frases: [
        "Cuando las barbas de tu vecino veas cortar pon las tuyas a remojar ",
        "El lobo ya no come la carne que quiere, sino la que puede",
        "Cuidado con el lobo con piel de cordero",
        "Las apariencias engañan, la criatura más dulce puede ser la más diabólica"
      ],
      fraseActual: 0
    };
  },
  created() {
    setInterval(() => {
      this.fraseActual = (this.fraseActual + 1) % this.frases.length;
    }, 8000);
  }
};
</script>


<style scoped>
.contenedor-carga {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: #8b0000;
  color: white;
  text-align: center;
}

.tarjeta-rol {
  background-color: #1f1f1f;
  border-radius: 15px;
  padding: 30px;
  max-width: 400px;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.imagen-personaje {
  width: 150px;
  height: 150px;
  object-fit: contain;
  margin-bottom: 20px;
}

.info-rol h2 {
  margin-bottom: 15px;
  font-size: 22px;
}

.descripcion {
  margin-bottom: 15px;
  font-size: 16px;
  line-height: 1.4;
}

.bando {
  font-weight: bold;
  font-size: 16px;
  color: #dddddd;
}

.mensaje-espera {
  margin-top: 30px;
  font-size: 16px;
  color: #ffffff;
}

.slider-frases {
  margin-top: 40px;
  height: 30px;
  overflow: hidden;
}

.frase {
  font-style: italic;
  color: white;
  transition: opacity 0.5s;
  font-size: 16px;
}

@media(max-width: 480px) {
  .tarjeta-rol {
    padding: 20px;
  }

  .imagen-personaje {
    width: 200px;
    height: 200px;
  }

  .info-rol h2 {
    font-size: 20px;
  }

  .descripcion,
  .bando,
  .mensaje-espera,
  .frase {
    font-size: 14px;
  }
}
</style>