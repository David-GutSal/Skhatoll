<template>
  <nav class="navbar" id="mi-navbar">
    <router-link to="/">Inicio</router-link>
    <router-link to="/sala">Sala de juegos</router-link>
    <router-link to="/reglas">Reglas y juegos</router-link>
    <router-link to="/personajes">Personajes</router-link>
  </nav>
</template>

<script setup>
</script>

<style scoped>

.navbar{
  display:flex;
  justify-content:center;
  gap:40px;
  padding:28px 40px;
  background:#000000;
  min-height: 60px;
  align-items: center;
  z-index: 15;
}

.navbar a{
  color: white;
  text-decoration: none;
  font-family: var(--font-cinzel);
  font-weight: 600;
  font-size: 1.4rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  position: relative;
}

.navbar a::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0%;
  height: 2px;
  background: var(--color-rojo);
  transition: width 0.3s ease;
}

.navbar a:hover::after {
  width: 100%;
}

.navbar a:hover {
  color: var(--color-rojo);
  border-bottom-color: transparent;
}

.navbar a:active {
  color: var(--color-rojo);
  border-bottom-color: var(--color-rojo);
}
 
.navbar a.router-link-exact-active {
  color: var(--color-rojo);
  font-weight: 800;
  border-bottom-color: var(--color-rojo);
}

.navbar a.router-link-exact-active::after {
  width: 100%;
}
 
@media (max-width: 970px) {
  .navbar {
    flex-direction: column;
    gap: 14px;
    padding: 18px 20px;
    align-items: center;
  }

  .navbar a {font-size: 1.2rem;}
}

</style>