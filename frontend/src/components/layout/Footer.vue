<template>
  <footer class="footer">
    <div class="footer-wrapper">
      <img :src="footerImg" alt="" class="footer-buildings" />
      
      <div class="footer-contenido">
        <div class="footer-grid">
          <div class="footer-col footer-col-izq">
            <span class="footer-copy">© 2026 Skhatoll. Proyecto Final 2º DAW.</span>
          </div>

          <div class="footer-col footer-col-centro">
            <img src="@/assets/imgs/logo-provisional.png" alt="Skhatoll" class="footer-logo" />
          </div>

          <div class="footer-col footer-col-der">
            <ul class="footer-nav">
              <li><router-link to="/">Inicio</router-link></li>
              <li><router-link to="/sala">Sala de juegos</router-link></li>
              <li><router-link to="/reglas">Reglas</router-link></li>
              <li><router-link to="/personajes">Personajes</router-link></li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script>
import footerImg from '@/assets/imgs/footer-castronegro.svg'

export default {
  name: 'Footer',
  data() {
    return { footerImg }
  },
}
</script>

<style scoped>
.footer {
  color: white;
  position: relative;

}

.footer-wrapper {
  position: relative;
  width: 100%;
  min-height: 250px;
}

.footer-buildings {
  width: 100%;
  height: auto;
  display: block;
  position: relative;
  z-index: 1;
}

.footer-contenido {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 2;
  background: linear-gradient(transparent 0%, black 30%);
  padding: 40px 5% 30px;
}

.footer-grid {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 20px;
  background-color: rgba(0, 0, 0, 0.95);
  padding: 20px 0;
  border-radius: 10px 10px 0 0;
}

.footer-col {
  display: flex;
  flex-direction: column;
}

.footer-col-izq {
  align-items: flex-start;
}

.footer-col-centro {
  align-items: center;
}

.footer-col-der {
  align-items: flex-end;
}

.footer-logo {
  height: 60px;
  object-fit: contain;
}

.footer-copy {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 0.85rem;
  font-weight: 600;
  color: white;
}

.footer-nav {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: row;
  gap: 24px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.footer-nav a {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: 0.9rem;
  font-weight: 600;
  color: white;
  text-decoration: none;
  transition: color 0.2s ease;
}

.footer-nav a:hover {
  color: #cc0000;
}

@media (max-width: 875px) {
  .footer-buildings {
    display: none !important;
  }
  
  .footer-wrapper {
    min-height: auto;
    background-color: black;
  }
  
  .footer-contenido {
    position: static;
    background: black;
    padding: 30px 5% 30px;
  }
  
  .footer-grid {
    grid-template-columns: 1fr;
    text-align: center;
    gap: 20px;
    padding: 0;
  }

  .footer-col-izq,
  .footer-col-der {
    align-items: center;
  }

  .footer-nav {
    flex-direction: column;
    align-items: center;
    gap: 12px;
  }

  .footer-nav a {
    font-size: 0.85rem;
  }
}

@media (max-width: 480px) {
  .footer-logo {
    height: 45px;
  }
  
  .footer-copy {
    font-size: 0.8rem;
  }
  
  .footer-contenido {
    padding: 25px 5%;
  }
}
</style>