<template>
  <div class="contenedor-carga">
    <div class="tarjeta-rol">
      <!-- Plantilla por defecto para luego cambiarla -->
      <img class="imagen-personaje" :src="imagenUrl" alt="Bruja" />

      <div class="info-rol">
        <h2>Tu rol es: {{ nombreRol }}</h2>
        <p class="descripcion">{{ descripcionRol }}</p>
        <p class="bando">Bando: {{ bando }}</p>
      </div>
    </div>

    <p class="mensaje-espera">Espera a que el narrador inicie la partida...</p>

    <div class="slider-frases">
      <p class="frase" v-for="(frase, index) in frases" :key="index" v-show="index === fraseActual">
        "{{ frase }}"
      </p>
    </div>
  </div>
</template>

<script>
// Para ver esta página antes de prepararla para backend hayq ue usar este enlace: http://localhost:5173/carga-rol

// =============================
// IMPORTS PARA BACKEND (WEBSOCKET)
// =============================
// DESCOMENTAR CUANDO SE CONECTE CON SPRING BOOT

// import { Client } from "@stomp/stompjs"
// import SockJS from 'sockjs-client'

// Plantilla
import BrujaImg from '@/assets/imgs/bruja.jpg'

export default {
  name: 'CargaRolView',
  data() {
    return {
      nombreRol: 'BRUJA',
      descripcionRol:
        'La Bruja puede salvar a un jugador por la noche o envenenar a otro. Usa tu habilidad sabiamente.',
      bando: 'BUENO',
      imagenUrl: BrujaImg,
      frases: [
        'Cuando las barbas de tu vecino veas cortar pon las tuyas a remojar ',
        'El lobo ya no come la carne que quiere, sino la que puede',
        'Cuidado con el lobo con piel de cordero',
        'Las apariencias engañan, la criatura más dulce puede ser la más diabólica',
      ],
      fraseActual: 0,

      // =============================
      // VARIABLES WEBSOCKET (BACKEND)
      // =============================

      // stompClient: null,
      // codigoSala: '', // Se puede tomar de localStorage o de ruta
      // token: '', // Token de autenticación
    }
  },

  created() {

    // =============================
    // ROTACIÓN DE FRASES (FRONTEND)
    // =============================

    setInterval(() => {
      this.fraseActual = (this.fraseActual + 1) % this.frases.length
    }, 8000)

    // =============================
    // DATOS LOCALES PARA PRUEBAS
    // =============================

    // this.codigoSala = localStorage.getItem('codigoSala') || ''
    // this.token = localStorage.getItem('token') || ''

    // =============================
    // CONEXIÓN WEBSOCKET (BACKEND)
    // =============================

    // this.conectarWebSocket()
  },

  methods: {

    // ====================================
    // MÉTODO PARA BACKEND CON WEBSOCKET
    // ====================================
    // DESCOMENTAR CUANDO EL BACKEND ESTÉ LISTO

    /*
    conectarWebSocket() {

      const socket = new SockJS('http://localhost:8080/ws') // URL del backend

      this.stompClient = new Client({

        webSocketFactory: () => socket,
        connectHeaders: { Authorization: `Bearer ${this.token}` },
        debug: str => console.log(str),
        reconnectDelay: 5000

      });

      this.stompClient.onConnect = frame => {

        this.stompClient.subscribe(`/user/queue/rol`, message => {

          const payload = JSON.parse(message.body);

          if (payload.tipo === 'ROL_ASIGNADO') {

            this.nombreRol = payload.nombreRol;
            this.descripcionRol = payload.descripcionRol;
            this.bando = payload.bando;

            // Imagen según el rol (por ahora ejemplo)
            // Puedes mapear nombreRol -> ruta imagen local
            this.imagenUrl = this.getImagenPorRol(payload.nombreRol)

            // Redirigir automáticamente según el rol
            if (payload.nombreRol.toUpperCase() === 'NARRADOR') {
              this.$router.push('/narrador')
            } else {
              this.$router.push('/jugador')
            }

          }

        });

      };

      this.stompClient.onStompError = frame => {
        console.error("Error STOMP:", frame.headers["message"]);
      };

      this.stompClient.activate();

    },
    */

    getImagenPorRol(nombreRol) {
      // Mapeo de ejemplo, luego usar imágenes locales
      const map = {
        BRUJA: BrujaImg,
        LOBO: BrujaImg,
        ALCALDE: BrujaImg,
      }
      return map[nombreRol] || BrujaImg
    },
  },
}
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

@media (max-width: 480px) {
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