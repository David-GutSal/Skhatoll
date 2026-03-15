<template>
  <div class="topbar">
    <div class="logo">
      <img src="@/assets/imgs/logo-provisional.png" alt="logo">
    </div>

    <div class="auth">
      <form class="login">
        <input type="text" placeholder="Usuario">
        <input type="password" placeholder="Contraseña">
        <button>Login</button>
      </form>

      <form class="registro">
        <input type="text" placeholder="Usuario">
        <input type="password" placeholder="Contraseña">
        <button>Registro</button>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: "TopBar"
}
</script>

<style scoped>
.topbar{
  display:flex;
  justify-content:space-between;
  align-items:center;
  padding:10px 30px;
  background:#111;
  color:white;
}
.logo img{
  height:50px;
}
.auth{
  display:flex;
  gap:20px;
}
.auth form{
  display:flex;
  gap:5px;
}
.auth input{
  padding:4px;
}
</style>