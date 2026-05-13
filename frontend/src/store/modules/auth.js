export default {
  namespaced: true,

  state: () => ({
    token: null,
    nombre: null,
    uuid: null,
  }),

  mutations: {
    SET_AUTH(state, { token, nombre, uuid }) {
      state.token = token
      state.nombre = nombre
      state.uuid = uuid
    },

    CLEAR_AUTH(state) {
      state.token = null
      state.nombre = null
      state.uuid = null
    },
  },

  actions: {
    login({ commit }, { token, nombre, uuid }) {
      sessionStorage.setItem('token', token)
      sessionStorage.setItem('nombre', nombre)
      sessionStorage.setItem('uuid', uuid)
      commit('SET_AUTH', { token, nombre, uuid })
    },
    logout({ commit }) {
      sessionStorage.removeItem('token')
      sessionStorage.removeItem('nombre')
      sessionStorage.removeItem('uuid')
      commit('CLEAR_AUTH')
    },
    restaurar({ commit }) {
      const token = sessionStorage.getItem('token')
      const nombre = sessionStorage.getItem('nombre')
      const uuid = sessionStorage.getItem('uuid')
      if (token && nombre && uuid) {
        commit('SET_AUTH', { token, nombre, uuid })
      }
    },
  },

  getters: {
    estaAutenticado: (state) => !!state.token,
    token: (state) => state.token,
    nombre: (state) => state.nombre,
    uuid: (state) => state.uuid,
  },
}
