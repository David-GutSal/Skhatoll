import { createApp } from 'vue'
import App from './App.vue'
import router from './router/index'
import store from './store/index'
import './assets/styles/main.css'
import './assets/styles/_shared.scss'

const app = createApp(App)

const token = sessionStorage.getItem('token')
const nombre = sessionStorage.getItem('nombre')
const uuid = sessionStorage.getItem('uuid')

if (token && nombre) {
  store.dispatch('auth/login', { token, nombre, uuid })
}

app.config.errorHandler = (err, instance, info) => {
  console.error('[Error global Vue]', err, info)
  store.dispatch('toast/mostrar', {
    mensaje: err?.message || 'Error inesperado en la aplicación',
    tipo: 'error',
  })
}

app.use(router)
app.use(store)

app.mount('#app')


