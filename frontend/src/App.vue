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
  </div>
</template>

<script>
import TopBar from '@/components/layout/TopBar.vue'
import Header from '@/components/layout/Header.vue'
import Navbar from '@/components/layout/Navbar.vue'

export default {
  name: 'App',
  components: { TopBar, Header, Navbar },

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
  z-index: 101;
}

.sticky-navbar {
  position: sticky;
  top: 80px;
  z-index: 102;
  outline: 3px solid transparent;
}

.sticky-navbar.scrolled {
  outline: 2px solid #cc0000;
}

@media (max-width: 600px) {
  .sticky-navbar {
    top: 150px;
  }
}
</style>