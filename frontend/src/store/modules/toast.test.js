import { createStore } from 'vuex'
import toast from './toast.js'
import { vi } from 'vitest'

/**
 * @typedef {import('vuex').Store} Store
 */

describe('Toast Store', () => {
  /** @type {Store} */
  let store

  beforeEach(() => {
    store = createStore({
      modules: { toast },
    })
    vi.useFakeTimers()
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  describe('Estado Inicial', () => {
    it('debería tener valores por defecto', () => {
      expect(store.state.toast.mensaje).toBeNull()
      expect(store.state.toast.tipo).toBe('info')
      expect(store.state.toast.visible).toBe(false)
      expect(store.state.toast.cola).toEqual([])
    })
  })

  describe('Mostrar Toast', () => {
    it('debería mostrar toast con tipo por defecto', async () => {
      await store.dispatch('toast/mostrar', { mensaje: 'Hola mundo' })

      expect(store.state.toast.mensaje).toBe('Hola mundo')
      expect(store.state.toast.tipo).toBe('info')
      expect(store.state.toast.visible).toBe(true)
    })

    it('debería mostrar toast con tipo personalizado', async () => {
      await store.dispatch('toast/mostrar', { mensaje: 'Error', tipo: 'error' })

      expect(store.state.toast.mensaje).toBe('Error')
      expect(store.state.toast.tipo).toBe('error')
      expect(store.state.toast.visible).toBe(true)
    })

    it('debería soportar tipos success y warning', async () => {
      await store.dispatch('toast/mostrar', { mensaje: 'Éxito', tipo: 'success' })
      expect(store.state.toast.tipo).toBe('success')

      await store.dispatch('toast/ocultar')
      vi.advanceTimersByTime(300)

      await store.dispatch('toast/mostrar', { mensaje: 'Advertencia', tipo: 'warning' })
      expect(store.state.toast.tipo).toBe('warning')
    })
  })

  describe('Ocultar Toast', () => {
    it('debería ocultar toast y limpiar mensaje', async () => {
      await store.dispatch('toast/mostrar', { mensaje: 'Visible' })

      await store.dispatch('toast/ocultar')

      expect(store.state.toast.visible).toBe(false)
      expect(store.state.toast.mensaje).toBeNull()
    })
  })

  describe('Cola de Toasts', () => {
    it('debería encolar toast cuando ya hay uno visible', async () => {
      await store.dispatch('toast/mostrar', { mensaje: 'Primero' })
      expect(store.state.toast.visible).toBe(true)

      await store.dispatch('toast/mostrar', { mensaje: 'Segundo', tipo: 'error' })

      expect(store.state.toast.cola).toHaveLength(1)
      expect(store.state.toast.cola[0].mensaje).toBe('Segundo')
      expect(store.state.toast.cola[0].tipo).toBe('error')
    })

    it('debería procesar siguiente toast de la cola', async () => {
      await store.dispatch('toast/mostrar', { mensaje: 'Primero' })
      await store.dispatch('toast/mostrar', { mensaje: 'Segundo' })
      await store.dispatch('toast/mostrar', { mensaje: 'Tercero' })

      expect(store.state.toast.cola).toHaveLength(2)

      await store.dispatch('toast/ocultar')

      vi.advanceTimersByTime(300)

      expect(store.state.toast.mensaje).toBe('Segundo')
      expect(store.state.toast.cola).toHaveLength(1)
    })

    it('debería vaciar la cola cuando se ocultan todos', async () => {
      await store.dispatch('toast/mostrar', { mensaje: 'Primero' })
      await store.dispatch('toast/mostrar', { mensaje: 'Segundo' })
      await store.dispatch('toast/mostrar', { mensaje: 'Tercero' })

      await store.dispatch('toast/ocultar')
      vi.advanceTimersByTime(300)

      await store.dispatch('toast/ocultar')
      vi.advanceTimersByTime(300)

      expect(store.state.toast.mensaje).toBe('Tercero')

      await store.dispatch('toast/ocultar')
      vi.advanceTimersByTime(300)

      expect(store.state.toast.visible).toBe(false)
      expect(store.state.toast.cola).toHaveLength(0)
    })
  })

  describe('Getters', () => {
    it('debería devolver correctamente los getters', async () => {
      await store.dispatch('toast/mostrar', { mensaje: 'Test', tipo: 'warning' })

      expect(store.getters['toast/mensaje']).toBe('Test')
      expect(store.getters['toast/tipo']).toBe('warning')
      expect(store.getters['toast/visible']).toBe(true)
      expect(store.getters['toast/colaPendiente']).toBe(0)
    })

    it('debería devolver cantidad de mensajes en cola', async () => {
      await store.dispatch('toast/mostrar', { mensaje: 'Primero' })
      await store.dispatch('toast/mostrar', { mensaje: 'Segundo' })

      expect(store.getters['toast/colaPendiente']).toBe(1)
    })
  })

  describe('Mutations', () => {
    it('debería ejecutar MOSTRAR correctamente', () => {
      store.commit('toast/MOSTRAR', { mensaje: 'Mutation', tipo: 'error' })

      expect(store.state.toast.mensaje).toBe('Mutation')
      expect(store.state.toast.tipo).toBe('error')
      expect(store.state.toast.visible).toBe(true)
    })

    it('debería ejecutar OCULTAR correctamente', () => {
      store.commit('toast/MOSTRAR', { mensaje: 'Visible' })
      store.commit('toast/OCULTAR')

      expect(store.state.toast.visible).toBe(false)
      expect(store.state.toast.mensaje).toBeNull()
    })

    it('debería ejecutar ENCOLAR correctamente', () => {
      store.commit('toast/ENCOLAR', { mensaje: 'En cola', tipo: 'info' })

      expect(store.state.toast.cola).toHaveLength(1)
      expect(store.state.toast.cola[0].mensaje).toBe('En cola')
    })

    it('debería ejecutar DESENCOLAR correctamente', () => {
      store.commit('toast/ENCOLAR', { mensaje: 'Primero' })
      store.commit('toast/ENCOLAR', { mensaje: 'Segundo' })
      store.commit('toast/DESENCOLAR')

      expect(store.state.toast.cola).toHaveLength(1)
      expect(store.state.toast.cola[0].mensaje).toBe('Segundo')
    })
  })
})