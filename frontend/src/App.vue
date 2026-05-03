<template>
  <div id="app">
    <template v-if="!$route.meta.sinLayout">
      <div class="sticky-topbar" :class="{ scrolled: hayScroll }">
        <TopBar />
      </div>
      <Header />
      <div class="sticky-navbar" :class="{ scrolled: hayScroll }">
        <Navbar />
      </div>
    </template>

    <router-view />
    <ToastNotificacion /> 
     <Footer v-if="!$route.meta.sinLayout" />
  </div>
</template>

<script>
import TopBar from '@/components/layout/TopBar.vue'
import Header from '@/components/layout/Header.vue'
import Navbar from '@/components/layout/Navbar.vue'
import Footer from '@/components/layout/Footer.vue'
import ToastNotificacion from '@/components/layout/ToastNotificacion.vue'
export default {
  name: 'App',
  components: { TopBar, Header, Navbar, Footer, ToastNotificacion},

  data() {
    return {
      hayScroll: false,
    }
  },

  mounted() {
    window.addEventListener('scroll', this.detectarScroll)
  },

  unmounted() {
    window.removeEventListener('scroll', this.detectarScroll)
  },

  methods: {
    detectarScroll() {
      this.hayScroll = window.scrollY > 0
    },
  },
}
</script>

<style>
body {
  font-family: 'Raleway', Arial, sans-serif;
  margin: 0;
}

h1, h2, h3, h4, h5, h6 {
  font-family: 'Cinzel', Arial, sans-serif;
}

#app {
  min-height: 100vh;
}

.sticky-topbar {
  position: sticky;
  top: 0;
  z-index: 200;
}

.sticky-navbar {
  position: sticky;
  top: 112px;
  z-index: 100;
  background-color: #ffffff;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.75);
}

.sticky-navbar.scrolled {
  outline: 2px solid #e4ba03;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.95);
}

@media (max-width: 700px) {
  .sticky-navbar {
    top: 100px;
  }
}

@media (max-width: 690px) {
  .sticky-navbar {
    top: 150px;
  }
}
</style>