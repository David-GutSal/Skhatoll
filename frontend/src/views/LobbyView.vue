<template>
  <div class="contenedor-lobby">
    <h1 class="titulo-lobby">Sala de Juegos</h1>

    <div class="grid-lobby">
      <div class="lado-izquierdo">
        <template v-if="esCreador">
          <div class="caja-panel">
            <div class="overlay">
              <h2 class="titulo-caja">Código Sala</h2>

              <div class="codigo-wrapper">
                <input class="input-codigo" :value="codigoSala" readonly />
                <button
                  class="boton-copiar"
                  @click="copiarCodigo"
                  :title="copiado ? '¡Copiado!' : 'Copiar'"
                >
                  <i :class="copiado ? 'fa-solid fa-check' : 'fa-solid fa-copy'"></i>
                </button>
              </div>

              <p class="subtitulo-caja">Comparte este código con el resto de jugadores</p>

              <button class="boton-iniciar" @click="iniciarPartida">
                <i class="fa-solid fa-play"></i> Iniciar partida
              </button>
            </div>
          </div>

          <div class="caja-redes">
            <p class="texto-redes">También puedes compartirlo en:</p>
            <div class="iconos-redes">
              <a
                :href="'https://wa.me/?text=¡Únete a mi partida! Código: ' + codigoSala"
                target="_blank"
                class="red-social"
                title="WhatsApp"
              >
                <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"
                  />
                </svg>
              </a>

              <a href="#" class="red-social" title="Discord" @click.prevent="copiarParaDiscord">
                <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M20.317 4.37a19.791 19.791 0 00-4.885-1.515.074.074 0 00-.079.037c-.21.375-.444.864-.608 1.25a18.27 18.27 0 00-5.487 0 12.64 12.64 0 00-.617-1.25.077.077 0 00-.079-.037A19.736 19.736 0 003.677 4.37a.07.07 0 00-.032.027C.533 9.046-.32 13.58.099 18.057a.082.082 0 00.031.057 19.9 19.9 0 005.993 3.03.078.078 0 00.084-.028c.462-.63.874-1.295 1.226-1.994a.076.076 0 00-.041-.106 13.107 13.107 0 01-1.872-.892.077.077 0 01-.008-.128 10.2 10.2 0 00.372-.292.074.074 0 01.077-.01c3.928 1.793 8.18 1.793 12.062 0a.074.074 0 01.078.01c.12.098.246.198.373.292a.077.077 0 01-.006.127 12.299 12.299 0 01-1.873.892.077.077 0 00-.041.107c.36.698.772 1.362 1.225 1.993a.076.076 0 00.084.028 19.839 19.839 0 006.002-3.03.077.077 0 00.032-.054c.5-5.177-.838-9.674-3.549-13.66a.061.061 0 00-.031-.03zM8.02 15.33c-1.183 0-2.157-1.085-2.157-2.419 0-1.333.956-2.419 2.157-2.419 1.21 0 2.176 1.096 2.157 2.42 0 1.333-.956 2.418-2.157 2.418zm7.975 0c-1.183 0-2.157-1.085-2.157-2.419 0-1.333.955-2.419 2.157-2.419 1.21 0 2.176 1.096 2.157 2.42 0 1.333-.946 2.418-2.157 2.418z"
                  />
                </svg>
              </a>

              <a
                :href="
                  'https://t.me/share/url?url=https://skhatoll.com&text=¡Únete a mi partida de Hombres Lobo! Código: ' +
                  codigoSala
                "
                target="_blank"
                class="red-social"
                title="Telegram"
              >
                <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M11.944 0A12 12 0 000 12a12 12 0 0012 12 12 12 0 0012-12A12 12 0 0012 0a12 12 0 00-.056 0zm4.962 7.224c.1-.002.321.023.465.14a.506.506 0 01.171.325c.016.093.036.306.02.472-.18 1.898-.962 6.502-1.36 8.627-.168.9-.499 1.201-.82 1.23-.696.065-1.225-.46-1.9-.902-1.056-.693-1.653-1.124-2.678-1.8-1.185-.78-.417-1.21.258-1.91.177-.184 3.247-2.977 3.307-3.23.007-.032.014-.15-.056-.212s-.174-.041-.249-.024c-.106.024-1.793 1.14-5.061 3.345-.48.33-.913.49-1.302.48-.428-.008-1.252-.241-1.865-.44-.752-.245-1.349-.374-1.297-.789.027-.216.325-.437.893-.663 3.498-1.524 5.83-2.529 6.998-3.014 3.332-1.386 4.025-1.627 4.476-1.635z"
                  />
                </svg>
              </a>
            </div>
          </div>
        </template>

        <template v-else>
          <div class="caja-panel">
            <div class="overlay">
              <h2 class="titulo-caja">Introduce aquí el código de sala</h2>

              <div class="input-jugador-wrapper">
                <input
                  class="input-jugador"
                  v-model="inputCodigo"
                  placeholder="######"
                  maxlength="20"
                />
              </div>

              <div class="botones-jugador">
                <button class="boton-unirse" @click="handleUnirse">Unirse a partida</button>
                <button class="boton-salir-jugador" @click="salirSala">Salir de la partida</button>
              </div>

              <p v-if="mensajeUnion" class="mensaje-union">
                ¡Te has unido a la partida! Ten paciencia y espera al resto de jugadores
              </p>
            </div>
          </div>
        </template>
        <!-- Indicador del narrador, pendiente de arreglar que desaparezca en el tablero -->
        <div class="narrador-indicador" v-if="nombreNarrador">
          <i class="fa-solid fa-book-open-reader"></i> Narrador: {{ nombreNarrador }}
        </div>
      </div>
      <ListaJugadores
        :jugadores="jugadores"
        :modoCreador="esCreador"
        :nombreNarrador="nombreNarrador"
        :soyNarrador="soyNarrador"
        @asignar-narrador="asignarNarrador"
      />
    </div>
  </div>
</template>

<script>
import axiosInstance from '@/plugins/axios'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { mapGetters, mapActions } from 'vuex'
import ListaJugadores from '@/components/lobby/ListaJugadores.vue'
import bienvenidaImg from '@/assets/imgs/bienvenida.jpg'

export default {
  name: 'LobbyView',
  components: { ListaJugadores },

  data() {
    return {
      inputCodigo: '',
      mensajeUnion: false,
      stompClient: null,
      copiado: false,
      bienvenidaImg,
    }
  },

  computed: {
    ...mapGetters('sala', ['codigoSala', 'esCreador', 'jugadores']),
    ...mapGetters('auth', ['nombre']),

    nombreNarrador() {
      const narrador = this.jugadores?.find((j) => j.esNarrador)
      return narrador ? narrador.nombre : null
    },

    soyNarrador() {
      return this.nombreNarrador === this.nombre
    },
  },
  watch: {
    codigoSala(nuevo) {
      if (nuevo) {
        this.cargarJugadores()
        this.conectarWebSocket()
      }
    },
  },

  created() {
    if (this.codigoSala) {
      this.cargarJugadores()
      this.conectarWebSocket()
    }
  },

  beforeUnmount() {
    if (this.stompClient) {
      this.stompClient.deactivate()
      this.stompClient = null
    }
  },

  methods: {
    ...mapActions('sala', ['unirse', 'setJugadores', 'salir']),

    async copiarCodigo() {
      await navigator.clipboard.writeText(this.codigoSala)
      this.copiado = true
      setTimeout(() => (this.copiado = false), 2000)
      this.$store.dispatch('toast/mostrar', {
        mensaje: '¡Código copiado al portapapeles!',
        tipo: 'info',
      })
    },

    async cargarJugadores() {
      try {
        const res = await axiosInstance.get(`/salas/${this.codigoSala}/jugadores`)
        this.setJugadores(res.data)
      } catch (error) {
        this.$store.dispatch('toast/mostrar', {
          mensaje: 'Error al cargar jugadores',
          tipo: 'error',
        })
      }
    },

    async iniciarPartida() {
      try {
        await axiosInstance.post(`/salas/${this.codigoSala}/iniciar`)
        this.$store.dispatch('toast/mostrar', {
          mensaje: '¡La partida va a comenzar!',
          tipo: 'exito',
        })
      } catch (error) {
        this.$store.dispatch('toast/mostrar', {
          mensaje: error.response?.data || 'Error al iniciar',
          tipo: 'error',
        })
      }
    },

    async asignarNarrador(idUsuario) {
      try {
        await axiosInstance.put(`/salas/${this.codigoSala}/narrador`, { idUsuario })
      } catch (error) {
        this.$store.dispatch('toast/mostrar', {
          mensaje: 'Error al asignar narrador',
          tipo: 'error',
        })
      }
    },

    async handleUnirse() {
      try {
        await axiosInstance.post('/salas/unirse', { codigoSala: this.inputCodigo })
        this.unirse(this.inputCodigo)
        this.mensajeUnion = true
        await this.cargarJugadores()
        this.conectarWebSocket()
      } catch (error) {
        this.$store.dispatch('toast/mostrar', {
          mensaje:
            error.response?.status === 409
              ? 'Ya estás en esta sala o está llena'
              : 'Sala no encontrada',
          tipo: 'error',
        })
      }
    },

    async copiarParaDiscord() {
      await navigator.clipboard.writeText(
        `¡Únete a mi partida de Hombres Lobo! Código: ${this.codigoSala}`,
      )
      this.$store.dispatch('toast/mostrar', {
        mensaje: '¡Código copiado! Pégalo en Discord',
        tipo: 'exito',
      })
    },

    async salirSala() {
      try {
        if (this.stompClient) {
          this.stompClient.deactivate()
          this.stompClient = null
        }

        await axiosInstance.post(`/salas/${this.codigoSala}/salir`)
        this.salir()
        this.$router.push({ name: 'sala' })
      } catch (error) {
        this.$store.dispatch('toast/mostrar', {
          mensaje: 'Error al salir de la sala',
          tipo: 'error',
        })
      }
    },

    conectarWebSocket() {
      const token = this.$store.getters['auth/token']
      const cliente = new Client({
        webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
        connectHeaders: { Authorization: `Bearer ${token}` },
      })
      cliente.onConnect = () => {
        let rolRecibido = false
        let inicioRecibido = false

        const intentarNavegar = () => {
          if (rolRecibido && inicioRecibido) {
            this.$router.push({ name: 'cargaRol' })
          }
        }

        cliente.subscribe(`/topic/sala/${this.codigoSala}/inicio`, () => {
          setTimeout(() => {
            const esCreador = this.$store.getters['sala/esCreador']
            if (esCreador) {
              this.$router.push({ name: 'esperaNarrador' })
            } else {
              this.$router.push({ name: 'cargaRol' })
            }
          }, 500)
        })

        cliente.subscribe(`/user/queue/rol`, (msg) => {
          const payload = JSON.parse(msg.body)
          if (payload.tipo === 'ROL_ASIGNADO') {
            this.$store.dispatch('sala/setRol', {
              nombreRol: payload.nombreRol,
              descripcionRol: payload.descripcionRol,
              bando: payload.bando,
            })
            rolRecibido = true
            intentarNavegar()
          }
        })

        cliente.subscribe(`/topic/sala/${this.codigoSala}`, (msg) => {
          const payload = JSON.parse(msg.body)
          if (payload.tipo === 'JUGADOR_UNIDO') {
            this.setJugadores(payload.jugadores)
            const ultimo = payload.jugadores[payload.jugadores.length - 1]
            if (ultimo && ultimo.nombre !== this.nombre) {
              this.$store.dispatch('toast/mostrar', {
                mensaje: `${ultimo.nombre} se ha unido a la partida`,
                tipo: 'info',
              })
            }
          }
          if (payload.tipo === 'JUGADOR_SALIO') {
            this.setJugadores(payload.jugadores)
          }
        })
      }
      cliente.activate()
      this.stompClient = cliente
    },
  },
}
</script>

<style scoped>
.contenedor-lobby {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
  box-sizing: border-box;
}

.titulo-lobby {
  font-family: 'Cinzel', Arial, sans-serif;
  font-weight: 700;
  font-size: 2.5rem;
  color: white;
  text-align: center;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin: 0 0 30px 0;
}

.grid-lobby {
  display: grid;
  grid-template-columns: 65% 25%;
  gap: 0 5%;
  width: 100%;
  max-width: 1200px;
  align-items: start;
}

.lado-izquierdo {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.caja-panel {
  background-image: v-bind("'url(' + bienvenidaImg + ')'");
  background-size: cover;
  background-position: center;
  border-radius: 15px;
  overflow: hidden;
}

.overlay {
  background: rgba(31, 31, 31, 0.85);
  padding: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.titulo-caja {
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 2.5rem;
  font-weight: 700;
  color: white;
  text-align: center;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin: 0;
}

.codigo-wrapper {
  display: flex;
  width: 100%;
  border-radius: 50px;
  overflow: hidden;
  border: 5px solid #e4ba03;
  background: white;
}

.input-codigo {
  flex: 1;
  padding: 14px 20px;
  background: transparent;
  border: none;
  font-family: Arial, sans-serif;
  font-size: 2rem;
  font-weight: bold;
  color: #8b0000;
  letter-spacing: 0.4em;
  text-align: center;
  outline: none;
}

.boton-copiar {
  background: #8b0000;
  border: none;
  padding: 14px;
  cursor: pointer;
  color: white;
  font-size: 1.4rem;
  border-radius: 0 50px 50px 0;
  transition: background 0.2s ease;
  flex-shrink: 0;
}

.boton-copiar:hover {
  background: #580000;
}

.subtitulo-caja {
  font-family: 'Raleway', Arial, sans-serif;
  font-style: italic;
  color: #ccc;
  font-size: 1.2rem;
  margin: 0;
  text-align: center;
}

.boton-iniciar {
  background: #8b0000;
  color: white;
  border: none;
  padding: 14px 36px;
  border-radius: 10px;
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1.1rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  cursor: pointer;
  transition:
    background 0.2s ease,
    transform 0.15s ease;
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 6px;
}

.boton-iniciar:hover {
  background: #e4ba03;
  color: #000;
}

.boton-iniciar:active {
  transform: scale(0.96);
}

.caja-redes {
  background: #1f1f1f;
  border-radius: 12px;
  padding: 18px 28px;
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.texto-redes {
  font-family: 'Raleway', Arial, sans-serif;
  color: white;
  font-weight: 700;
  font-size: 1.1rem;
  margin: 0;
  white-space: nowrap;
}

.iconos-redes {
  display: flex;
  gap: 16px;
  align-items: center;
}

.red-social {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: white;
  transition: background 0.2s ease;
  flex-shrink: 0;
  text-decoration: none;
}

.red-social svg {
  width: 32px;
  height: 32px;
  fill: #333;
  transition: fill 0.2s ease;
}

.red-social:hover {
  background: #8b0000;
}

.red-social:hover svg {
  fill: white;
}

.input-jugador-wrapper {
  width: 100%;
}

.input-jugador {
  margin: 15px auto;
  width: 100%;
  box-sizing: border-box;
  background: white;
  border: 5px solid #a30000;
  border-radius: 50px;
  font-size: 1.8rem;
  font-family: Arial, sans-serif;
  font-weight: bolder;
  color: #111;
  padding: 14px 24px;
  text-align: center;
  outline: none;
  letter-spacing: 0.5em;
}

.input-jugador::placeholder {
  color: #aaa;
}

.input-jugador:focus {
  border-color: #8b0000;
}

.botones-jugador {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
  width: 100%;
}

.boton-unirse {
  background: #a30000;
  color: white;
  border: none;
  padding: 13px 28px;
  border-radius: 10px;
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  letter-spacing: 0.05em;
  transition:
    background 0.2s ease,
    transform 0.15s ease;
}

.boton-unirse:hover {
  background: #640000;
}
.boton-unirse:active {
  transform: scale(0.95);
}

.boton-salir-jugador {
  background: #c9a800;
  color: white;
  border: none;
  padding: 13px 28px;
  border-radius: 10px;
  font-family: 'Cinzel', Arial, sans-serif;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  letter-spacing: 0.05em;
  transition:
    background 0.2s ease,
    transform 0.15s ease;
}

.boton-salir-jugador:hover {
  background: #000;
}
.boton-salir-jugador:active {
  transform: scale(0.95);
}

.narrador-indicador {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #cc0000;
  font-family: 'Cinzel', Arial, sans-serif;
  font-weight: 700;
  font-size: 0.95rem;
  padding: 10px 14px;
  background: #1f1f1f;
  border-radius: 8px;
  border-left: 4px solid #8b0000;
}

.mensaje-union {
  font-family: 'Raleway', Arial, sans-serif;
  font-style: italic;
  color: white;
  font-size: 0.95rem;
  text-align: center;
  margin: 0;
  line-height: 1.5;
}

@media (max-width: 900px) {
  .grid-lobby {
    grid-template-columns: 1fr;
    gap: 20px 0;
  }
  .titulo-caja {
    font-size: 1.8rem;
  }
  .input-codigo {
    font-size: 1.8rem;
  }
}

@media (max-width: 1234px) and (min-width: 904px) {
  .input-codigo {
    font-size: 1.5rem;
  }
  .titulo-caja {
    font-size: 1.5rem;
  }
}

@media (max-width: 600px) {
  .titulo-lobby {
    font-size: 1.6rem;
  }
  .titulo-caja {
    font-size: 1.3rem;
  }
  .input-codigo {
    font-size: 1.2rem;
  }
  .caja-redes {
    flex-direction: column;
    align-items: flex-start;
  }
  .botones-jugador {
    flex-direction: column;
    align-items: center;
  }
}
</style>
