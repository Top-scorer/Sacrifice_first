// main.js文件在Vue项目中的作用是创建Vue实例、引入插件和库、注册全局组件、配置全局设置等，是整个应用程序的入口文件，使得整个Vue应用能够正常运行。
import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

Vue.config.productionTip = false;
Vue.use(ElementUI);

window.addEventListener('storage', (event) => {
  if (event.key === 'currentUser') {
    // 检测到 currentUser 变化时，刷新页面
    window.location.reload()
  }
})

new Vue({
  el:'#app',
  router,
  render: h => h(App)
}).$mount('#app')
