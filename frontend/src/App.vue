<template>
 <div id="app">
    <div class="sticky-topbar" :class="{ scrolled: hayScroll }">
      <TopBar />
    </div>
    <Header />
    <div class="sticky-navbar" :class="{ scrolled: hayScroll }">
      <Navbar />
    </div>  
    <router-view/>
  </div>
</template>

<script>
import TopBar from "@/components/layout/TopBar.vue"
import Header from "@/components/layout/Header.vue"
import Navbar from "@/components/layout/Navbar.vue"

export default {
  name: "App",
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
  margin: 0;
  font-family: 'Raleway', Arial, sans-serif;
}

h1, h2, h3, h4, h5, h6 {
  font-family: 'Cinzel', Arial, sans-serif;
}

#app {
  min-height: 100vh;
}

#app > router-view {
  padding: 20px;
}

.sticky-topbar {
  position: sticky;
  top: 0;
  z-index: 101;
}

.sticky-navbar {
  position: sticky;
  top: 95px;
  z-index: 102;
}

.sticky-navbar.scrolled {
  outline: 1px solid #cc0000;
}

@media (max-width: 690px) {
  .sticky-navbar {
    top: 150px;
  }
}
</style>