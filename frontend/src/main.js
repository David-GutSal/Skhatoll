import { createApp } from 'vue'
import App from './App.vue'
import router from './router/index'
import store from './store/index'
import axiosInstance from './plugins/axios'
import 'bootstrap/dist/css/bootstrap.min.css'

const app = createApp(App)

app.config.globalProperties.$axios = axiosInstance

app.use(router)
app.use(store)

app.mount('#app')
