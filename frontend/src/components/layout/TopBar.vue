<template>
  <div class="topbar">

    <div class="logo">
      <img src="@/assets/imgs/logo-provisional.png" alt="logo" />
    </div>

    <div class="auth" v-click-outside="cerrarModales">
      <i class="fa-solid fa-user auth-icon"></i>

      <span v-if="!estaAutenticado" class="auth-links">

        <span class="auth-link-wrapper">
          <a href="#" class="auth-link" :class="{ activo: mostrarLogin }" @click.prevent="toggleLogin">
            INICIAR SESIÓN
          </a>
          <div v-if="mostrarLogin" class="dropdown dropdown-login">
            <h2 class="dropdown-title login-title">¡Hola de nuevo! Inicia Sesión</h2>
            <input
              type="text"
              v-model="loginNombre"
              placeholder="Tu Usuario"
              class="dropdown-input"
            />
            <input
              type="password"
              v-model="loginPassword"
              placeholder="Tu Contraseña"
              class="dropdown-input"
            />
            <button class="dropdown-btn btn-login" @click.prevent="handleLogin">ENTRAR</button>
          </div>
        </span>

        <span class="separator">|</span>

        <span class="auth-link-wrapper">
          <a href="#" class="auth-link" :class="{ activo: mostrarRegistro }" @click.prevent="toggleRegistro">
            REGISTRARSE
          </a>
          <div v-if="mostrarRegistro" class="dropdown dropdown-registro">
            <h2 class="dropdown-title registro-title">¿Eres nuevo? ¡Regístrate!</h2>
            <input
              type="text"
              v-model="registroNombre"
              placeholder="Usuario"
              class="dropdown-input dropdown-input-registro"
            />
            <input
              type="password"
              v-model="registroPassword"
              placeholder="Contraseña"
              class="dropdown-input dropdown-input-registro"
            />
            <button class="dropdown-btn btn-registro" @click.prevent="handleRegistro">REGÍSTRATE</button>
          </div>
        </span>

      </span>

      <span v-else class="auth-links">
        <span class="auth-bienvenido">Bienvenido,  <span id="nombramarillo">{{ nombre }}</span> </span>
        <span class="separator">|</span>
        <a href="#" class="auth-link auth-salir" @click.prevent="handleLogout">
          <i class="fa-solid fa-right-from-bracket"></i> SALIR
        </a>
      </span>
    </div>
  </div>
</template>

<script>
import axiosInstance from '@/plugins/axios'
import { mapActions, mapGetters } from 'vuex'

export default {
  name: 'TopBar',

  directives: {
    'click-outside': {
      mounted(el, binding) {
        el._clickOutside = (e) => {
          if (!el.contains(e.target)) binding.value()
        }
        document.addEventListener('click', el._clickOutside)
      },
      unmounted(el) {
        document.removeEventListener('click', el._clickOutside)
      },
    },
  },

  data() {
    return {
      loginNombre: '',
      loginPassword: '',
      registroNombre: '',
      registroPassword: '',
      mostrarLogin: false,
      mostrarRegistro: false,
    }
  },

  computed: {
    ...mapGetters('auth', ['estaAutenticado', 'nombre']),
  },

  methods: {
    ...mapActions('auth', ['login', 'logout']),

    toggleLogin() {
      this.mostrarLogin = !this.mostrarLogin
      this.mostrarRegistro = false
    },
    toggleRegistro() {
      this.mostrarRegistro = !this.mostrarRegistro
      this.mostrarLogin = false
    },
    cerrarModales() {
      this.mostrarLogin = false
      this.mostrarRegistro = false
    },

    handleLogout() {
      this.logout()
    },

    async handleLogin() {
      try {
        const res = await axiosInstance.post('/auth/login', {
          nombre: this.loginNombre,
          password: this.loginPassword,
        })
        this.login({ token: res.data.token, nombre: res.data.nombre, uuid: res.data.codigoUuid })
        this.cerrarModales()
        this.loginNombre = ''
        this.loginPassword = ''
      } catch (error) {
        console.error('Error login:', error)
        alert(error.response?.status === 401 ? 'Credenciales incorrectas' : 'Error al iniciar sesión')
      }
    },

    async handleRegistro() {
      try {
        const res = await axiosInstance.post('/auth/registro', {
          nombre: this.registroNombre,
          password: this.registroPassword,
        })
        this.login({ token: res.data.token, nombre: res.data.nombre, uuid: res.data.codigoUuid })
        this.cerrarModales()
        this.registroNombre = ''
        this.registroPassword = ''
        alert('Registro exitoso. ¡Bienvenido ' + res.data.nombre + '!')
      } catch (error) {
        console.error('Error registro:', error)
        alert(error.response?.status === 409 ? 'Ese nombre ya está en uso' : 'Error al registrarse')
      }
    },
  },
}
</script>

<style scoped>

.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 40px;
  background: #000000;
  color: white;
  min-height: 80px;
  position: relative;
  z-index: 100;
}

.logo img {
  height: 70px;
  object-fit: contain;
}

.auth {
  display: flex;
  align-items: center;
  gap: 15px;
  position: relative;
  font-family: 'Cinzel', Arial, sans-serif;
}

.auth-icon {
  color: white;
  font-size: 1.5rem;
}

.auth-links {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.05rem;
  letter-spacing: 0.05em;
}

.separator {
  color: #9e0000;
  font-size: 2.5rem;
  line-height: 1;
}

.auth-link-wrapper {
  position: relative;
}

.auth-link {
  color: white;
  text-decoration: none;
  font-weight: 600;
  cursor: pointer;
  transition: color 0.2s ease;

}

.auth-link:hover,
.auth-link:active,
.auth-link.activo {
  color: #cc0000;
}

.dropdown {
  text-align: center;
  position: absolute;
  top: calc(100% + 18px);
  right: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  padding: 28px 26px;
  border-radius: 5px;
  width: 300px;
  z-index: 999;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
}

.dropdown-login {
  background: #ffffff;
  border: 3px solid #cc0000;
}

.login-title {
  color: #cc0000;
  font-weight: 700;
  font-size: 1.5rem;
  margin: 0 0 4px 0;
}

.dropdown-registro {
  background: #111111;
  border: 3px solid #cc0000;
}

.registro-title {
  color: #ffffff;
  font-weight: 700;
  font-size: 1.5rem;
  margin: 0 0 4px 0;
  text-align: center;
}

.dropdown-input {
  width: 100%;
  padding: 10px 14px;
  border: 1.5px solid #ccc;
  border-radius: 3px;
  font-size: 0.9rem;
  box-sizing: border-box;
  background: #fff;
  color: #222;
}

.dropdown-input:focus {
  outline: none;
  border-color: #cc0000;
}

.dropdown-input-registro {
  background: #1e1e1e;
  border-color: #444;
  color: #fff;
}

.dropdown-input-registro::placeholder {
  color: #888;
}

.dropdown-input-registro:focus {
  border-color: #cc0000;
}

.dropdown-btn {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 3px;
  font-size: 0.9rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: background 0.2s ease;
  box-sizing: border-box;
  margin-top: 2px;
}

.btn-login {
  background: #cc0000;
  color: white;
}

.btn-login:hover {
  background: #990000;
}

.btn-registro {
  background: white;
  color: #111;
}

.btn-registro:hover {
  background: #e0e0e0;
}

.auth-bienvenido {
  color: white;
  font-size: 1.35rem;
  font-weight: 600;

}

#nombramarillo{
  color: #ebc310;
}

.auth-salir {
  display: flex;
  align-items: center;
  gap: 5px;
}

@media (max-width: 690px) {
  .topbar {
    padding: 14px 20px;
    flex-direction: column;
    align-items: center;
    gap: 12px;
  }
  .auth-links {
    font-size: 1rem;
    justify-content: center;
  }
  .auth {
    justify-content: center;
  }

  .auth-icon{
    display: none;
  }

  .dropdown {
    width: 240px;
    right: 50%;
    transform: translateX(50%);
  }
}
</style>