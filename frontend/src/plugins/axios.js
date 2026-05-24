import axios from 'axios'
import store from '@/store'

const API_BASE = import.meta.env.VITE_API_URL || ''

const axiosInstance = axios.create({
  baseURL: API_BASE,
  headers: {
    'Content-Type': 'application/json',
  },
})

axiosInstance.interceptors.request.use((config) => {
  const token = store.getters['auth/token']
  if (token) {
     config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      store.dispatch('toast/mostrar', {
        mensaje: 'Tu sesión ha expirado. Vuelve a iniciar sesión.',
        tipo: 'error',
      })
    }
    return Promise.reject(error)
  }
)

export default axiosInstance
