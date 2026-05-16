import { createStore } from 'vuex'
import auth from './auth.js'

/**
 * @typedef {import('vuex').Store} Store
 */

describe('Auth Store', () => {
  /** @type {Store} */
  let store

  beforeEach(() => {
    store = createStore({
      modules: { auth },
    })
    sessionStorage.clear()
  })

  describe('Estado Inicial', () => {
    it('debería tener valores por defecto', () => {
      expect(store.state.auth.token).toBeNull()
      expect(store.state.auth.nombre).toBeNull()
      expect(store.state.auth.uuid).toBeNull()
    })
  })

  describe('Login', () => {
    it('debería iniciar sesión correctamente', async () => {
      await store.dispatch('auth/login', {
        token: 'jwt-token-123',
        nombre: 'Juan',
        uuid: 'usuario-uuid-456',
      })

      expect(store.state.auth.token).toBe('jwt-token-123')
      expect(store.state.auth.nombre).toBe('Juan')
      expect(store.state.auth.uuid).toBe('usuario-uuid-456')
      expect(sessionStorage.getItem('token')).toBe('jwt-token-123')
      expect(sessionStorage.getItem('nombre')).toBe('Juan')
      expect(sessionStorage.getItem('uuid')).toBe('usuario-uuid-456')
    })
  })

  describe('Logout', () => {
    it('debería cerrar sesión y limpiar storage', async () => {
      sessionStorage.setItem('token', 'test-token')
      sessionStorage.setItem('nombre', 'TestUser')
      sessionStorage.setItem('uuid', 'test-uuid')

      await store.dispatch('auth/login', {
        token: 'test-token',
        nombre: 'TestUser',
        uuid: 'test-uuid',
      })

      await store.dispatch('auth/logout')

      expect(store.state.auth.token).toBeNull()
      expect(store.state.auth.nombre).toBeNull()
      expect(store.state.auth.uuid).toBeNull()
      expect(sessionStorage.getItem('token')).toBeNull()
      expect(sessionStorage.getItem('nombre')).toBeNull()
      expect(sessionStorage.getItem('uuid')).toBeNull()
    })
  })

  describe('Restaurar Sesión', () => {
    it('debería restaurar sesión desde sessionStorage', async () => {
      sessionStorage.setItem('token', 'restored-token')
      sessionStorage.setItem('nombre', 'RestoredUser')
      sessionStorage.setItem('uuid', 'restored-uuid')

      await store.dispatch('auth/restaurar')

      expect(store.state.auth.token).toBe('restored-token')
      expect(store.state.auth.nombre).toBe('RestoredUser')
      expect(store.state.auth.uuid).toBe('restored-uuid')
    })

    it('debería ignorar si no hay datos en sessionStorage', async () => {
      await store.dispatch('auth/restaurar')

      expect(store.state.auth.token).toBeNull()
      expect(store.state.auth.nombre).toBeNull()
      expect(store.state.auth.uuid).toBeNull()
    })

    it('debería ignorar si falta algún dato', async () => {
      sessionStorage.setItem('token', 'partial-token')
      // nombre y uuid no están

      await store.dispatch('auth/restaurar')

      expect(store.state.auth.token).toBeNull()
    })
  })

  describe('Getters', () => {
    it('debería devolver true cuando hay token', async () => {
      await store.dispatch('auth/login', {
        token: 'token-valido',
        nombre: 'Usuario',
        uuid: 'uuid',
      })

      expect(store.getters['auth/estaAutenticado']).toBe(true)
      expect(store.getters['auth/token']).toBe('token-valido')
      expect(store.getters['auth/nombre']).toBe('Usuario')
      expect(store.getters['auth/uuid']).toBe('uuid')
    })

    it('debería devolver false cuando no hay token', () => {
      expect(store.getters['auth/estaAutenticado']).toBe(false)
      expect(store.getters['auth/token']).toBeNull()
      expect(store.getters['auth/nombre']).toBeNull()
      expect(store.getters['auth/uuid']).toBeNull()
    })
  })

  describe('Mutations', () => {
    it('debería ejecutar SET_AUTH correctamente', () => {
      store.commit('auth/SET_AUTH', {
        token: 'mutation-token',
        nombre: 'Mutacion',
        uuid: 'mutation-uuid',
      })

      expect(store.state.auth.token).toBe('mutation-token')
      expect(store.state.auth.nombre).toBe('Mutacion')
      expect(store.state.auth.uuid).toBe('mutation-uuid')
    })

    it('debería ejecutar CLEAR_AUTH correctamente', () => {
      store.commit('auth/SET_AUTH', {
        token: 'token',
        nombre: 'Usuario',
        uuid: 'uuid',
      })

      store.commit('auth/CLEAR_AUTH')

      expect(store.state.auth.token).toBeNull()
      expect(store.state.auth.nombre).toBeNull()
      expect(store.state.auth.uuid).toBeNull()
    })
  })
})