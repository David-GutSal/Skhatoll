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
    meta: { sinLayout: true },
  },

  {
    path: '/espera-narrador',
    name: 'esperaNarrador',
    component: EsperaNarradorView,
    meta: { sinLayout: true },
  },

  {
    path: '/narrador',
    name: 'narrador',
    component: NarradorView,
    meta: { sinLayout: true },
  },

  {
    path: '/jugador',
    name: 'jugador',
    component: JugadorView,
    meta: { sinLayout: true },
  },
  
  {
    path: '/resultados',
    name: 'resultados',
    component: ResultadosView,
    meta: { sinLayout: true },
  },

  // Añade esto temporalmente en el router para previsualizar las pantallas de carga en CargaRolView
/*{ path: '/preview-carga', component: CargaRolView, meta: { sinLayout: true } },
  { path: '/preview-espera', component: EsperaNarradorView, meta: { sinLayout: true } }
*/
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
