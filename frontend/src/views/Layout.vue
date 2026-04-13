<template>
  <div class="layout">
    <el-header class="header">
      <div class="logo">在线教育平台</div>
      <div class="nav">
        <el-menu
          :default-active="$route.path"
          mode="horizontal"
          router
          background-color="#409EFF"
          text-color="#fff"
          active-text-color="#ffd04b"
        >
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/courses">课程</el-menu-item>
          <el-menu-item index="/wishlist">
            心愿单
          </el-menu-item>
          <el-menu-item index="/enrollments">我的课程</el-menu-item>
        </el-menu>
      </div>
      <div class="user">
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            {{ userStore.userInfo?.username || '用户' }}
            <el-icon><Arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    
    <el-main class="main">
      <router-view />
    </el-main>
    
    <el-footer class="footer">
      <p>© 2024 在线教育平台 - All Rights Reserved</p>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userStore.logout()
        ElMessage.success('已退出登录')
        router.push('/login')
      })
      break
  }
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  align-items: center;
  background-color: #409EFF;
  padding: 0 20px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  margin-right: 40px;
}

.nav {
  flex: 1;
}

.user {
  color: #fff;
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 5px;
}

.main {
  flex: 1;
  padding: 20px;
  background-color: #f5f5f5;
}

.footer {
  text-align: center;
  color: #999;
  padding: 20px;
  background-color: #fff;
}
</style>
