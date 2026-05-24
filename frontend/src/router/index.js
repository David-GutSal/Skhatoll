import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'

import InicioView from '../views/InicioView.vue'
import SalaView from '../views/SalaView.vue'
import ReglasView from '../views/ReglasView.vue'
import PersonajesView from '../views/PersonajesView.vue'

import LobbyView from '../views/LobbyView.vue'
import CargaRolView from '../views/CargaRolView.vue'
import NarradorView from '../views/NarradorView.vue'
import JugadorView from '../views/JugadorView.vue'
import ResultadosView from '../views/ResultadosView.vue'
import EsperaNarradorView from '../views/EsperaNarradorView.vue'
import EliminadoView from '../views/EliminadoView.vue'
import NotFoundView from '../views/NotFoundView.vue'

const routes = [
  {
    path: '/',
    name: 'inicio',
    component: InicioView,
    meta: { requiereAuth: false, requiereSala: false },
  },
  {
    path: '/sala',
    name: 'sala',
    component: SalaView,
    meta: { requiereAuth: false, requiereSala: false },
  },
  {
    path: '/reglas',
    name: 'reglas',
    component: ReglasView,
    meta: { requiereAuth: false, requiereSala: false },
  },
  {
    path: '/personajes',
    name: 'personajes',
    component: PersonajesView,
    meta: { requiereAuth: false, requiereSala: false },
  },

  {
    path: '/lobby',
    name: 'lobby',
    component: LobbyView,
    meta: { requiereAuth: true, requiereSala: false },
  },

  {
    path: '/carga-rol',
    name: 'cargaRol',
    component: CargaRolView,
    meta: { sinLayout: true, requiereAuth: true, requiereSala: true },
  },

  {
    path: '/espera-narrador',
    name: 'esperaNarrador',
    component: EsperaNarradorView,
    meta: { sinLayout: true, requiereAuth: true, requiereSala: true },
  },

  {
    path: '/narrador',
    name: 'narrador',
    component: NarradorView,
    meta: { sinLayout: true, requiereAuth: true, requiereSala: true },
  },

  {
    path: '/jugador',
    name: 'jugador',
    component: JugadorView,
    meta: { sinLayout: true, requiereAuth: true, requiereSala: true },
  },

  {
    path: '/resultados',
    name: 'resultados',
    component: ResultadosView,
    meta: { sinLayout: true, requiereAuth: true, requiereSala: true },
  },

  {
    path: '/eliminado',
    name: 'eliminado',
    component: EliminadoView,
    meta: { sinLayout: true, requiereAuth: true, requiereSala: true },
  },

  {
    path: '/:pathMatch(.*)*',
    name: 'notFound',
    component: NotFoundView,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
  if (savedPosition) {
    return savedPosition
  }

  return new Promise((resolve) => {
    setTimeout(() => {
      if (to.path === '/') {
        resolve({ top: 0, behavior: 'smooth' })
      } else {
        const ancho = window.innerWidth
        let posicionFinal = 0

        if (ancho > 1200) {
          posicionFinal = 410
        }
        else if (ancho <= 1200 && ancho > 847) {
          posicionFinal = 180
        }
        else if (ancho <= 847 && ancho > 480) {
          posicionFinal = 170
        }
        else {
          posicionFinal = 140
        }

        resolve({
          top: posicionFinal,
          behavior: 'smooth'
        })
      }
    }, 500)
    })
  },
})

router.beforeEach((to, from) => {
  if (to.meta.requiereAuth !== false) {
    const estaAutenticado = store.getters['auth/estaAutenticado']
    if (!estaAutenticado) {
      return { name: 'inicio' }
    }
  }

  if (to.meta.requiereSala) {
    const codigoSala = store.getters['sala/codigoSala']
    if (!codigoSala) {
      return { name: 'sala' }
    }
  }
})

export default router
