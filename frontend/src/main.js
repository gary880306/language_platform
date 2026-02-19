import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhTw from 'element-plus/es/locale/lang/zh-tw'

import App from './App.vue'
import router from './router'

const app = createApp(App)

// 註冊 Element Plus 圖示
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhTw })

app.mount('#app')
