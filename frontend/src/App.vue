// App.vue 文件作为项目的根组件，是所有其他组件的父组件。在Vue项目中起到了连接其他组件的作用，定义了项目的整体结构和样式，并提供了对路由和状态管理的支持
<template>
  <div id='app'>
  <router-view></router-view>
  </div>
</template>

<script>


export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin: 0;
  padding: 0;
  min-height: 100vh;
  margin-top: 0px; /* 顶部不留白 */
}
body{
  margin:0;
  padding:0;
  border:0
}

</style>
