<template>
  <div class="course-detail" v-loading="loading">
    <div class="course-header">
      <div class="course-cover">
        <img :src="course.coverImage || '/placeholder.jpg'" alt="course.name" />
      </div>
      <div class="course-info">
        <h1>{{ course.name }}</h1>
        <p class="course-desc">{{ course.description }}</p>
        <div class="course-meta">
          <span class="teacher">讲师：{{ course.teacher }}</span>
          <span class="students">{{ course.students }}人学习</span>
          <span class="duration">课时：{{ course.duration || 0 }}分钟</span>
        </div>
        <div class="course-price">
          <span class="price">¥{{ course.price }}</span>
          <span class="original-price" v-if="course.originalPrice">¥{{ course.originalPrice }}</span>
        </div>
        <div class="course-actions">
          <el-button type="primary" size="large" @click="handleEnroll">立即报名</el-button>
          <el-button size="large" @click="handleAddWishlist">加入心愿单</el-button>
        </div>
      </div>
    </div>

    <div class="course-content">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="课程详情" name="detail">
          <div class="detail-content" v-html="course.detail || '暂无详情'"></div>
        </el-tab-pane>
        <el-tab-pane label="课程目录" name="chapters">
          <el-empty v-if="!course.chapters || course.chapters.length === 0" description="暂无课程目录" />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCourseById } from '@/api/course'
import { createEnrollment } from '@/api/enrollment'
import { addToWishlist } from '@/api/wishlist'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const course = ref({})
const loading = ref(false)
const activeTab = ref('detail')

const fetchCourse = async () => {
  loading.value = true
  try {
    const res = await getCourseById(route.params.id)
    course.value = res.data
  } catch (error) {
    console.error(error)
    ElMessage.error('加载课程失败')
  } finally {
    loading.value = false
  }
}

const handleEnroll = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    await createEnrollment({ courseId: course.value.id, payType: 0 })
    ElMessage.success('报名成功')
    router.push('/enrollments')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '报名失败')
  }
}

const handleAddWishlist = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    await addToWishlist(course.value.id)
    ElMessage.success('已添加到心愿单')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '添加失败')
  }
}

onMounted(() => {
  fetchCourse()
})
</script>

<style scoped>
.course-detail {
  padding: 20px;
}

.course-header {
  display: flex;
  gap: 30px;
  margin-bottom: 30px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}

.course-cover {
  width: 400px;
  height: 250px;
  flex-shrink: 0;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.course-info {
  flex: 1;
}

.course-info h1 {
  font-size: 24px;
  margin-bottom: 10px;
}

.course-desc {
  color: #666;
  margin-bottom: 20px;
}

.course-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  color: #999;
}

.teacher {
  color: #409eff;
}

.course-price {
  margin-bottom: 20px;
}

.price {
  font-size: 28px;
  color: #f56c6c;
  font-weight: bold;
}

.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
  margin-left: 10px;
}

.course-actions {
  display: flex;
  gap: 10px;
}

.course-content {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}

.detail-content {
  line-height: 2;
  color: #666;
}
</style>
