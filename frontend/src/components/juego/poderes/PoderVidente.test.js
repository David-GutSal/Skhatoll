import { mount } from '@vue/test-utils'
import { describe, it, expect, vi } from 'vitest'
import { createStore } from 'vuex'
import axios from 'axios'
import PoderVidente from './PoderVidente.vue'

vi.mock('axios')

const createMockStore = (codigoSala = 'TEST123') =>
  createStore({
    modules: {
      sala: {
        namespaced: true,
        state: () => ({ codigoSala }),
        getters: {
          codigoSala: (state) => state.codigoSala,
        },
      },
      toast: {
        namespaced: true,
        state: () => ({}),
        actions: {
          mostrar: vi.fn(),
        },
      },
    },
  })

describe('PoderVidente', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('debería renderizar correctamente', () => {
    const wrapper = mount(PoderVidente, {
      global: {
        provide: { store: createMockStore() },
      },
    })

    expect(wrapper.find('.poder-vidente').exists()).toBe(true)
    expect(wrapper.find('.poder-titulo').text()).toContain('Premonición')
  })

  it('debería mostrar descripción del poder', () => {
    const wrapper = mount(PoderVidente, {
      global: {
        provide: { store: createMockStore() },
      },
    })

    expect(wrapper.find('.poder-descripcion').text()).toContain('revela su identidad')
  })

  it('debería tener botón deshabilitado cuando no hay jugador seleccionado', () => {
    const wrapper = mount(PoderVidente, {
      global: {
        provide: { store: createMockStore() },
      },
      props: {
        jugadorSeleccionado: null,
      },
    })

    expect(wrapper.find('.btn-poder').attributes('disabled')).toBeDefined()
  })

  it('debería tener botón habilitado cuando hay jugador seleccionado y poder no usado', () => {
    const wrapper = mount(PoderVidente, {
      global: {
        provide: { store: createMockStore() },
      },
      props: {
        jugadorSeleccionado: { nombre: 'Alice', idUsuario: 1 },
      },
    })

    expect(wrapper.find('.btn-poder').attributes('disabled')).toBeUndefined()
  })

  it('debería aplicar clase según la fase (día)', () => {
    const wrapperDia = mount(PoderVidente, {
      global: {
        provide: { store: createMockStore() },
      },
      props: { esDia: true },
    })

    expect(wrapperDia.find('.poder-vidente').classes()).toContain('poder-dia')
  })

  it('debería aplicar clase según la fase (noche)', () => {
    const wrapperNoche = mount(PoderVidente, {
      global: {
        provide: { store: createMockStore() },
      },
      props: { esDia: false },
    })

    expect(wrapperNoche.find('.poder-vidente').classes()).toContain('poder-noche')
  })

  it('debería usar valores por defecto en props', () => {
    const wrapper = mount(PoderVidente, {
      global: {
        provide: { store: createMockStore() },
      },
    })

    expect(wrapper.props('esDia')).toBe(false)
    expect(wrapper.props('jugadorSeleccionado')).toBeNull()
  })

  it('debería mostrar texto correcto del botón en estado normal', () => {
    const wrapper = mount(PoderVidente, {
      global: {
        provide: { store: createMockStore() },
      },
      props: {
        jugadorSeleccionado: { nombre: 'Alice', idUsuario: 1 },
      },
    })

    expect(wrapper.find('.btn-poder').text()).toContain('Revelar identidad')
  })

  it('debería mostrar icono de ojo en botón', () => {
    const wrapper = mount(PoderVidente, {
      global: {
        provide: { store: createMockStore() },
      },
    })

    expect(wrapper.find('.btn-poder i.fa-eye').exists()).toBe(true)
  })
})