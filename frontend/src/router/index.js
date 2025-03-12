import { createRouter, createWebHistory } from 'vue-router'

// 前台页面
const Home = () => import('../views/front/Home.vue')
const ArticleDetail = () => import('../views/front/ArticleDetail.vue')
const CategoryList = () => import('../views/front/CategoryList.vue')
const TagList = () => import('../views/front/TagList.vue')
const Search = () => import('../views/front/Search.vue')

// 后台页面
const AdminLayout = () => import('../views/admin/AdminLayout.vue')
const Dashboard = () => import('../views/admin/Dashboard.vue')
const ArticleManage = () => import('../views/admin/article/ArticleManage.vue')
const ArticleEdit = () => import('../views/admin/article/ArticleEdit.vue')
const CategoryManage = () => import('../views/admin/category/CategoryManage.vue')
const TagManage = () => import('../views/admin/tag/TagManage.vue')
const CommentManage = () => import('../views/admin/comment/CommentManage.vue')
const UserManage = () => import('../views/admin/user/UserManage.vue')

// 认证页面
const Login = () => import('../views/auth/Login.vue')
const Register = () => import('../views/auth/Register.vue')

const routes = [
  // 前台路由
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '博客首页' }
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: ArticleDetail,
    meta: { title: '文章详情' }
  },
  {
    path: '/category/:id',
    name: 'CategoryList',
    component: CategoryList,
    meta: { title: '分类文章' }
  },
  {
    path: '/tag/:id',
    name: 'TagList',
    component: TagList,
    meta: { title: '标签文章' }
  },
  {
    path: '/search',
    name: 'Search',
    component: Search,
    meta: { title: '搜索结果' }
  },
  
  // 认证路由
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { title: '注册' }
  },
  
  // 后台路由
  {
    path: '/admin',
    name: 'AdminLayout',
    component: AdminLayout,
    meta: { requiresAuth: true, title: '后台管理' },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '仪表盘' }
      },
      {
        path: 'article',
        name: 'ArticleManage',
        component: ArticleManage,
        meta: { title: '文章管理' }
      },
      {
        path: 'article/edit',
        name: 'ArticleAdd',
        component: ArticleEdit,
        meta: { title: '添加文章' }
      },
      {
        path: 'article/edit/:id',
        name: 'ArticleEdit',
        component: ArticleEdit,
        meta: { title: '编辑文章' }
      },
      {
        path: 'category',
        name: 'CategoryManage',
        component: CategoryManage,
        meta: { title: '分类管理' }
      },
      {
        path: 'tag',
        name: 'TagManage',
        component: TagManage,
        meta: { title: '标签管理' }
      },
      {
        path: 'comment',
        name: 'CommentManage',
        component: CommentManage,
        meta: { title: '评论管理' }
      },
      {
        path: 'user',
        name: 'UserManage',
        component: UserManage,
        meta: { title: '用户管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - Trae Blog` : 'Trae Blog'
  
  // 检查是否需要登录权限
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (!token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router