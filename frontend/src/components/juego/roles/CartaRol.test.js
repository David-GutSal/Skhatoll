import { mount } from '@vue/test-utils'
import { describe, it, expect, vi } from 'vitest'
import { createStore } from 'vuex'
import CartaRol from './CartaRol.vue'

const jugadorVivo = {
  nombre: 'Alice',
  idUsuario: 1,
  estaVivo: true,
  muerteConfirmada: false,
  alcalde: false,
  votos: 0,
  esEnamorado: false,
  esMentor: false,
}

const jugadorMuerto = {
  nombre: 'Bob',
  idUsuario: 2,
  estaVivo: false,
  muerteConfirmada: true,
  alcalde: false,
  votos: 0,
  esEnamorado: false,
  esMentor: false,
}

const jugadorSemiMuerto = {
  nombre: 'Charlie',
  idUsuario: 3,
  estaVivo: false,
  muerteConfirmada: false,
  alcalde: false,
  votos: 0,
}

const createMockStore = (miRol = null) =>
  createStore({
    modules: {
      sala: {
        namespaced: true,
        state: () => ({ tipoVotacion: 'DIA', miRol }),
        getters: {
          tipoVotacion: (state) => state.tipoVotacion,
          miRol: (state) => state.miRol,
        },
      },
    },
  })

describe('CartaRol', () => {
  describe('Modo Carga / Mi Rol', () => {
    it('debería renderizar carta completa en modo carga', () => {
      const wrapper = mount(CartaRol, {
        global: {
          provide: { store: createMockStore() },
        },
        props: {
          modoVista: 'carga',
          nombreRol: 'Vidente',
          descripcion: 'Puede ver los roles',
          bando: 'aldea',
          imagen: '/img/roles/vidente.png',
        },
      })

      expect(wrapper.find('.carta-completa').exists()).toBe(true)
      expect(wrapper.find('.carta-nombre').text()).toBe('Vidente')
    })

    it('debería mostrar bando en mayúsculas', () => {
      const wrapper = mount(CartaRol, {
        global: {
          provide: { store: createMockStore() },
        },
        props: {
          modoVista: 'carga',
          nombreRol: 'Lobo',
          bando: 'lobo',
        },
      })

      expect(wrapper.find('.carta-bando-badge').text()).toBe('LOBO')
    })
  })

  describe('Modo Narrador', () => {
    it('debería renderizar carta de mesa en modo narrador', () => {
      const wrapper = mount(CartaRol, {
        global: {
          provide: { store: createMockStore() },
        },
        props: {
          modoVista: 'narrador',
          jugador: { ...jugadorVivo, nombreRol: 'Vidente' },
          esDia: true,
          modoEventos: false,
          jugadorSeleccionado: null,
        },
      })

      expect(wrapper.find('.carta-narrador').exists()).toBe(true)
      expect(wrapper.find('.carta-mesa-nombre').text()).toBe('Alice')
    })

    it('debería aplicar clase muerto cuando no está vivo', () => {
      const wrapper = mount(CartaRol, {
        global: {
          provide: { store: createMockStore() },
        },
        props: {
          modoVista: 'narrador',
          jugador: jugadorMuerto,
          esDia: true,
          modoEventos: false,
          jugadorSeleccionado: null,
        },
      })

      expect(wrapper.find('.carta-narrador').classes()).toContain('muerto')
    })

    it('debería aplicar clase alcalde cuando es alcalde', () => {
      const wrapper = mount(CartaRol, {
        global: {
          provide: { store: createMockStore() },
        },
        props: {
          modoVista: 'narrador',
          jugador: { ...jugadorVivo, alcalde: true },
          esDia: true,
          modoEventos: false,
          jugadorSeleccionado: null,
        },
      })

      expect(wrapper.find('.carta-narrador').classes()).toContain('alcalde')
    })

    it('debería mostrar overlay de muerto confirmado', () => {
      const wrapper = mount(CartaRol, {
        global: {
          provide: { store: createMockStore() },
        },
        props: {
          modoVista: 'narrador',
          jugador: jugadorMuerto,
          esDia: true,
          modoEventos: false,
          jugadorSeleccionado: null,
        },
      })

      expect(wrapper.find('.overlay-muerto').exists()).toBe(true)
    })

    it('debería mostrar overlay de semi-muerto', () => {
      const wrapper = mount(CartaRol, {
        global: {
          provide: { store: createMockStore() },
        },
        props: {
          modoVista: 'narrador',
          jugador: jugadorSemiMuerto,
          esDia: true,
          modoEventos: false,
          jugadorSeleccionado: null,
        },
      })

      expect(wrapper.find('.overlay-semimuerto').exists()).toBe(true)
    })

    it('debería mostrar icono de enamorado', () => {
      const wrapper = mount(CartaRol, {
        global: {
          provide: { store: createMockStore() },
        },
        props: {
          modoVista: 'narrador',
          jugador: { ...jugadorVivo, esEnamorado: true },
          esDia: true,
          modoEventos: false,
          jugadorSeleccionado: null,
        },
      })

      expect(wrapper.find('.badge-enamorado').exists()).toBe(true)
    })

    it('debería mostrar icono de mentor para el Narrador', () => {
      const wrapper = mount(CartaRol, {
        global: {
          provide: { store: createMockStore('Narrador') },
        },
        props: {
          modoVista: 'narrador',
          jugador: { ...jugadorVivo, esMentor: true },
          esDia: true,
          modoEventos: false,
          jugadorSeleccionado: null,
        },
      })

      expect(wrapper.find('.badge-mentor').exists()).toBe(true)
    })

    it('no debería mostrar icono de mentor para otros roles', () => {
      const wrapper = mount(CartaRol, {
        global: {
          provide: { store: createMockStore('Aldeano') },
        },
        props: {
          modoVista: 'narrador',
          jugador: { ...jugadorVivo, esMentor: true },
          esDia: true,
          modoEventos: false,
          jugadorSeleccionado: null,
        },
      })

      expect(wrapper.find('.badge-mentor').exists()).toBe(false)
    })

    it('debería mostrar votos cuando tiene votos', () => {
      const wrapper = mount(CartaRol, {
        global: {
          provide: { store: createMockStore() },
        },
        props: {
          modoVista: 'narrador',
          jugador: { ...jugadorVivo, votos: 3 },
          esDia: true,
          modoEventos: false,
          jugadorSeleccionado: null,
        },
      })

      expect(wrapper.find('.badge-votos').exists()).toBe(true)
      expect(wrapper.find('.badge-votos').text()).toContain('3')
    })

    it('debería emitir evento seleccionar al hacer click', async () => {
      const wrapper = mount(CartaRol, {
        global: {
          provide: { store: createMockStore() },
        },
        props: {
          modoVista: 'narrador',
          jugador: jugadorVivo,
          esDia: true,
          modoEventos: false,
          jugadorSeleccionado: null,
        },
      })

      await wrapper.find('.carta-narrador').trigger('click')

      expect(wrapper.emitted('seleccionar')).toBeTruthy()
      expect(wrapper.emitted('seleccionar')[0][0]).toEqual(jugadorVivo)
    })

    it('debería mostrar ??? cuando no tiene rol', () => {
      const wrapper = mount(CartaRol, {
        global: {
          provide: { store: createMockStore() },
        },
        props: {
          modoVista: 'narrador',
          jugador: { ...jugadorVivo, nombreRol: null },
          esDia: true,
          modoEventos: false,
          jugadorSeleccionado: null,
        },
      })

      expect(wrapper.find('.carta-mesa-rol').text()).toBe('???')
    })
  })

  describe('Modo Jugador', () => {
    it('debería renderizar en modo jugador', () => {
      const wrapper = mount(CartaRol, {
        global: {
          provide: { store: createMockStore() },
        },
        props: {
          modoVista: 'jugador',
          jugador: jugadorVivo,
          esDia: true,
          modoEventos: false,
          jugadorSeleccionado: null,
        },
      })

      expect(wrapper.find('.carta-jugador').exists()).toBe(true)
    })
  })

  describe('Sin store - modo narrador básico', () => {
    it('debería renderizar sin errores sin provide de store', () => {
      const wrapper = mount(CartaRol, {
        props: {
          modoVista: 'narrador',
          jugador: jugadorVivo,
        },
      })

      expect(wrapper.exists()).toBe(true)
    })
  })
})