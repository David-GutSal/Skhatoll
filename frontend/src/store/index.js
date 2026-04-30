import { createStore } from 'vuex'
import auth from './modules/auth'
import sala from './modules/sala'
import toast from './modules/toast'

export default createStore({
  modules: {
    auth,
    sala,
    toast,
  },
})
