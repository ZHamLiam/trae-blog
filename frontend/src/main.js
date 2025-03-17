import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import './style.css'
import './assets/theme.css'
import './assets/typography.css'
import './assets/dark-theme-overrides.css'
import './assets/responsive.css'
import './assets/admin-global.css'
import { formatDate } from './utils/dateFormat'
import { useThemeStore } from './store/theme'

const app = createApp(App)
const pinia = createPinia()

// 使用Pinia状态管理
app.use(pinia)

// 使用Vue Router
app.use(router)

// 使用Ant Design Vue
app.use(Antd)

// 全局注册日期格式化函数
app.config.globalProperties.$formatDate = formatDate

// 挂载应用前初始化主题
const themeStore = useThemeStore(pinia)
themeStore.initTheme()

app.mount('#app')
