# frontend

This template should help get you started developing with Vue 3 in Vite.

## Recommended IDE Setup

[VS Code](https://code.visualstudio.com/) + [Vue (Official)](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Recommended Browser Setup

- Chromium-based browsers (Chrome, Edge, Brave, etc.):
  - [Vue.js devtools](https://chromewebstore.google.com/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd)
  - [Turn on Custom Object Formatter in Chrome DevTools](http://bit.ly/object-formatters)
- Firefox:
  - [Vue.js devtools](https://addons.mozilla.org/en-US/firefox/addon/vue-js-devtools/)
  - [Turn on Custom Object Formatter in Firefox DevTools](https://fxdx.dev/firefox-devtools-custom-object-formatters/)

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Project Setup

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Compile and Minify for Production

```sh
npm run build
```

```
frontend
│
├─ index.html
├─ public
├─ src
│  ├─ App.vue
│  ├─ assets
│  │  ├─ imgs
│  │  └─ styles
│  │
│  ├─ components
│  │  ├─ juego
│  │  │  ├─ BotonMiRol.vue
│  │  │  ├─ CabeceraJugador.vue
│  │  │  ├─ IndicadorDiaNoche.test.js
│  │  │  ├─ IndicadorDiaNoche.vue
│  │  │  ├─ ListaPersonajes.vue
│  │  │  ├─ ListaReglas.vue
│  │  │  ├─ MesaJugadores.vue
│  │  │  ├─ PanelControlNarrador.vue
│  │  │  ├─ PanelVotacionesJugador.vue
│  │  │  ├─ poderes
│  │  │  ├─ roles
│  │  │  │
│  │  │  └─ ZonaPoderes.vue
│  │  ├─ layout
│  │  └─ lobby
│  │
│  ├─ composables
│  ├─ data
│  ├─ main.js
│  ├─ plugins
│  ├─ router
│  ├─ services
│  ├─ store
│  │  ├─ GameEvents.js
│  │  ├─ index.js
│  │  └─ modules
│  │
│  ├─ test
│  │  └─ setup.js
│  └─ views
│     ├─ CargaRolView.vue
│     ├─ EliminadoView.vue
│     ├─ EsperaNarradorView.vue
│     ├─ InicioView.vue
│     ├─ JugadorView.vue
│     ├─ LobbyView.vue
│     ├─ NarradorView.vue
│     ├─ PersonajesView.vue
│     ├─ ReglasView.vue
│     ├─ ResultadosView.vue
│     └─ SalaView.vue
├─ vite.config.js
└─ vitest.config.js


```