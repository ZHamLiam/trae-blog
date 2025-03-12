import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import './style.css'
import { formatDate } from './utils/dateFormat'

const app = createApp(App)

// 使用Pinia状态管理
app.use(createPinia())

// 使用Vue Router
app.use(router)

// 使用Ant Design Vue
app.use(Antd)

// 全局注册日期格式化函数
app.config.globalProperties.$formatDate = formatDate

app.mount('#app')
