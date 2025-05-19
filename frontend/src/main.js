// src/main.js
import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// Font Awesome 설정
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import {
  faSearch,
  faUser,
  faSignOutAlt,
  faSpinner,
  faEye,
  faEyeSlash,
  faPlus,
  faEdit,
  faTimes,
  faCamera,
  faCheckCircle,
  faCode,
  faPercentage,
  faFire,
  faCalendar,
  faClock,
  faBlog,
  faHome,
  faArrowLeft,
} from '@fortawesome/free-solid-svg-icons'
import { faGithub, faTwitter, faDiscord, faGoogle } from '@fortawesome/free-brands-svg-icons'

// Font Awesome 아이콘 추가
library.add(
  // Solid icons
  faSearch,
  faUser,
  faSignOutAlt,
  faSpinner,
  faEye,
  faEyeSlash,
  faPlus,
  faEdit,
  faTimes,
  faCamera,
  faCheckCircle,
  faCode,
  faPercentage,
  faFire,
  faCalendar,
  faClock,
  faBlog,
  faHome,
  faArrowLeft,
  // Brand icons
  faGithub,
  faTwitter,
  faDiscord,
  faGoogle,
)

const app = createApp(App)

app.use(createPinia())
app.use(router)

// 전역 컴포넌트 등록
app.component('font-awesome-icon', FontAwesomeIcon)

app.mount('#app')
