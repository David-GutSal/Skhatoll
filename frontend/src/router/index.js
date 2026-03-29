import { createRouter, createWebHistory } from 'vue-router'

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

const routes = [
  {
    path: '/',
    name: 'inicio',
    component: InicioView,
  },
  {
    path: '/sala',
    name: 'sala',
    component: SalaView,
  },
  {
    path: '/reglas',
    name: 'reglas',
    component: ReglasView,
  },
  {
    path: '/personajes',
    name: 'personajes',
    component: PersonajesView,
  },

  {
    path: '/lobby',
    name: 'lobby',
    component: LobbyView,
  },

  {
    path: '/carga-rol',
    name: 'cargaRol',
    component: CargaRolView,
  },

  {
    path: '/espera-narrador',
    name: 'esperaNarrador',
    component: EsperaNarradorView,
  },

  {
    path: '/narrador',
    name: 'narrador',
    component: NarradorView,
  },

  {
    path: '/jugador',
    name: 'jugador',
    component: JugadorView,
  },
  
  {
    path: '/resultados',
    name: 'resultados',
    component: ResultadosView,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
