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

      // 🟢 GUARDAR EN LOCALSTORAGE
      localStorage.setItem('token', token)
      localStorage.setItem('nombre', nombre)
      localStorage.setItem('uuid', uuid)
    },

    CLEAR_AUTH(state) {
      state.token = null
      state.nombre = null
      state.uuid = null

      // 🔴 BORRAR LOCALSTORAGE
      localStorage.removeItem('token')
      localStorage.removeItem('nombre')
      localStorage.removeItem('uuid')
    },
  },

  actions: {
    login({ commit }, payload) {
      commit('SET_AUTH', payload)
    },
    logout({ commit }) {
      commit('CLEAR_AUTH')
    },
  },

  getters: {
    estaAutenticado: (state) => !!state.token,
    token: (state) => state.token,
    nombre: (state) => state.nombre,
    uuid: (state) => state.uuid,
  },
}
