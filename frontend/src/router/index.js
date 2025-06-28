import Vue from 'vue'
import VueRouter from 'vue-router'
import { jwtDecode } from 'jwt-decode'


Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { public: true }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/discover',
    name: 'Discover',
    component: () => import('../views/Discover.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/select',
    name: 'Select',
    component: () => import('../views/Select.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/mine',
    name: 'Mine',
    component: () => import('../views/Mine.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/random',
    name: 'Random',
    component: () => import('../views/Random.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/create',
    name: 'Create',
    component: () => import('../views/Create.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('../views/Search.vue'),
    props: (route) => ({ keyword: route.query.keyword }),
    meta: { requiresAuth: true }
  },
  {
    path: '/videoplayer',
    name: 'VideoPlayer',
    component: () => import('../components/VideoPlayer.vue'),
    props: (route) => ({ id: route.query.id }),
    meta: { requiresAuth: true }
  },
  {
    path: '/userplayer',
    name: 'UserPlayer',
    component: () => import('../components/UserPlayer.vue'),
    props: (route) => ({ username: route.query.username }),
    meta: { requiresAuth: true }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { requiresAuth: true, requireRole: 'admin' }
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('../views/User.vue'),
    meta: { requiresAuth: true, requireRole: 'admin' }
  },
  {
    path: '/video',
    name: 'Video',
    component: () => import('../views/Video.vue'),
    meta: { requiresAuth: true, requireRole: 'admin' }
  },
  {
    path: '/videocheck',
    name: 'Videocheck',
    component: () => import('../views/Videocheck.vue'),
    meta: { requiresAuth: true, requireRole: 'admin' }
  },
  {
    path: '/videoremoval',
    name: 'Videoremoval',
    component: () => import('../views/Videoremoval.vue'),
    meta: { requiresAuth: true, requireRole: 'admin' }
  },
  {
    path: '/useralgorithm',
    name: 'UserAlgorithm',
    component: () => import('../views/Useralgorithm.vue'),
    meta: { requiresAuth: true, requireRole: 'admin' }
  },
  {
    path: '/videoalgorithm',
    name: 'VideoAlgorithm',
    component: () => import('../views/Videoalgorithm.vue'),
    meta: { requiresAuth: true, requireRole: 'admin' }
  },
  {
    path: '*',
    redirect: '/login' // 默认重定向到登录页
  }
]


const router = new VueRouter({
  mode: 'history',
  routes
})

router.beforeEach((to, from, next) => {
  console.group(`路由守卫: ${from.path} → ${to.path}`);
  console.log('路由信息:', to);
  console.log('当前token:', localStorage.getItem('token'));
  console.log('当前userRole:', localStorage.getItem('userRole'));

  // 1. 处理公共路由
  if (to.meta.public) {
    console.log('放行: 公共路由');
    console.groupEnd();
    return next();
  }

  const token = localStorage.getItem('token');

  // 2. 处理无token情况
  if (!token) {
    if (to.path !== '/login') {
      console.log('重定向: 无token → /login');
      console.groupEnd();
      return next({
        path: '/login',
        query: { redirect: to.fullPath }
      });
    }
    console.log('放行: 已在登录页');
    console.groupEnd();
    return next();
  }

  // 3. 安全解码JWT
  let decoded;
  try {
    decoded = jwtDecode(token);
    console.log('JWT解码成功:', decoded);
  } catch (error) {
    console.error('JWT解析失败:', error);
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    localStorage.removeItem('userRole');
    console.groupEnd();
    return next('/login');
  }

  // 4. 检查token过期
  if (decoded.exp && Date.now() >= decoded.exp * 1000) {
    console.log('token已过期');
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    localStorage.removeItem('userRole');
    console.groupEnd();
    return next('/login');
  }

  const userRole = localStorage.getItem('userRole') || 'guest';
  const username = decoded.username;
  console.log('用户信息:', { username, userRole });

  // 5. 处理角色不匹配
  if (localStorage.getItem("currentUser") !== username) {
    console.log('角色不匹配: 重置');
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    localStorage.removeItem('userRole');
    console.groupEnd();
    return next('/login');
  }

  // 6. 检查路由权限
  if (to.meta.requireRole === 'admin' && username !== 'admin') {
    console.log('权限不足: 需要角色', to.meta.requireRole);
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    localStorage.removeItem('userRole');
    console.groupEnd();
    return next('/login');
  }

  // 6. 检查路由权限
  if (to.meta.requireRole && to.meta.requireRole !== userRole) {
    console.log('权限不足: 需要角色', to.meta.requireRole);
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    localStorage.removeItem('userRole');
    console.groupEnd();
    return next('/login');
  }

  // 7. 所有检查通过
  console.log('放行: 所有检查通过');
  console.groupEnd();
  next();
});

export default router
