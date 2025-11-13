import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('../components/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/index.vue'),
        meta: { title: '数据统计', icon: 'DataAnalysis' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/users/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'resources',
        name: 'Resources',
        component: () => import('../views/resources/index.vue'),
        meta: { title: '学习资源管理', icon: 'Reading' }
      },
      {
        path: 'questions',
        name: 'Questions',
        component: () => import('../views/questions/index.vue'),
        meta: { title: '题库管理', icon: 'Document' }
      },
      {
        path: 'community',
        name: 'Community',
        component: () => import('../views/community/index.vue'),
        meta: { title: '社区管理', icon: 'ChatDotRound' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('admin_token')
  
  if (to.path === '/login') {
    next()
  } else {
    if (!token) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router
