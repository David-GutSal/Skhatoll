<template>
  <nav class="navbar">
    <router-link to="/">Inicio</router-link>
    <router-link to="/sala">Sala de juegos</router-link>
    <router-link to="/reglas">Reglas y juegos</router-link>
    <router-link to="/personajes">Personajes</router-link>
  </nav>
</template>

<script>
export default {
  name:"Navbar"
}
</script>

<style scoped>

.navbar{
  display:flex;
  justify-content:center;
  gap:40px;
  padding:18px 40px;
  background:#000000;
  min-height: 60px;
  align-items: center;
}

.navbar a{
  color: white;
  text-decoration: none;
  font-family: 'Cinzel', Arial, sans-serif;
  font-weight: 600;
  font-size: 0.95rem;
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
  background: #cc0000;
  transition: width 0.3s ease;
}

.navbar a:hover::after {
  width: 100%;
}

.navbar a:hover {
  color: #cc0000;
  border-bottom-color: transparent;
}

.navbar a:active {
  color: #cc0000;
  border-bottom-color: #cc0000;
}
 
.navbar a.router-link-exact-active {
  color: #cc0000;
  font-weight: 800;
  border-bottom-color: #cc0000;
}

.navbar a.router-link-exact-active::after {
  width: 100%;
}
 
@media (max-width: 690px) {
  .navbar {
    flex-direction: column;
    gap: 14px;
    padding: 18px 20px;
    align-items: center;
  }
}

</style>