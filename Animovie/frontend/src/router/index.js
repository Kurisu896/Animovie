import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import SubjectList from '../views/SubjectList.vue'
import MyCollections from '../views/MyCollections.vue'
import MyTimeline from '../views/MyTimeline.vue'
import MyTopics from '../views/MyTopics.vue'
import SubjectDetail from '../views/SubjectDetail.vue'
import TopicDetail from '../views/TopicDetail.vue'
import ForumList from '../views/ForumList.vue'
import AllTimeline from '../views/AllTimeline.vue'
import Ranking from '../views/Ranking.vue'
import Search from '../views/Search.vue'
import Profile from '../views/Profile.vue'
import UserHome from '../views/UserHome.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/subject', component: SubjectList },
  { path: '/subject/:id', component: SubjectDetail },
  { path: '/my/collections', component: MyCollections },
  { path: '/my/timeline', component: MyTimeline },
  { path: '/my/topics', component: MyTopics },
  { path: '/discussion/topic/:id', component: TopicDetail },
  { path: '/forum', component: ForumList },
  { path: '/timeline', component: AllTimeline },
  { path: '/rank', component: Ranking },
  { path: '/search', component: Search },
  { path: '/profile', component: Profile },
  { path: '/user/:id', component: UserHome }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
