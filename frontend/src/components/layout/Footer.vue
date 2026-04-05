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
  min-height: 300px;
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
  background: linear-gradient(transparent 0%, black 70%);
  padding: 55px 5% 45px;
}

.footer-grid {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: clamp(25px, 5vw, 35px);
  padding: 30px 0;
  border-radius: 12px 12px 0 0;
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
  height: clamp(70px, 12vw, 90px);
  width: auto;
  max-height: 90px;
  object-fit: contain;
  object-position: center;
}

.footer-copy {
  font-family: 'Raleway', Arial, sans-serif;
  font-size: clamp(1rem, 2vw, 1.2rem);
  font-weight: 600;
  color: white;
  line-height: 1.4;
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
  font-size: clamp(1rem, 2.2vw, 1.2rem);
  font-weight: 600;
  color: white;
  text-decoration: none;
  transition: color 0.2s ease;
  white-space: nowrap;
}

.footer-nav a:hover {
  color: #cc0000;
}

@media (max-width: 1150px) {
  .footer-grid {
  padding: 8px 5px;
  }

  .footer-copy {
  font-size: 0.85rem;
  font-weight: 600;
}

.footer-nav a {
  font-size: 0.85rem;
}

}

@media (max-width: 950px) {
  .footer-wrapper {
    min-height: auto;

  }
  
  .footer-contenido {
    position: static;
    background: black;
    padding: 5px 4% 30px 4%;
  }
  
  .footer-grid {
    grid-template-columns: 1fr;
    text-align: center;
    gap: clamp(20px, 6vw, 30px);
    padding: 0;
    background-color: transparent;
  }

  .footer-col-izq,
  .footer-col-der {
    align-items: center;
  }

  .footer-nav {
    flex-direction: column;
    align-items: center;
    gap: clamp(12px, 4vw, 20px);
  }
}

@media (max-width: 480px) {
  .footer-wrapper {
    min-height: 190px;
  }
  
  .footer-contenido {
    padding: 5px 8% 25px 8%;
  }
  
  .footer-grid {
    gap: 18px;
  }
}
</style>