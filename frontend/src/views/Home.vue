<template>
  <div class="home">
    <!-- Banner -->
    <el-carousel height="400px" class="banner">
      <el-carousel-item>
        <div class="banner-item" style="background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);">
          <h2>欢迎来到在线教育平台</h2>
          <p>精品课程，名师指导</p>
        </div>
      </el-carousel-item>
      <el-carousel-item>
        <div class="banner-item" style="background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);">
          <h2>热门课程</h2>
          <p>紧跟技术潮流，提升专业技能</p>
        </div>
      </el-carousel-item>
      <el-carousel-item>
        <div class="banner-item" style="background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);">
          <h2>限时特惠</h2>
          <p>超值课程，不容错过</p>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 热门课程 -->
    <div class="section">
      <div class="section-header">
        <h3>热门课程</h3>
        <el-button type="text" @click="$router.push('/courses')">查看更多</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="6" v-for="course in hotCourses" :key="course.id">
          <el-card class="course-card" @click="goToCourse(course.id)">
            <img :src="course.coverImage || '/placeholder.jpg'" class="course-image" />
            <div class="course-info">
              <h4 class="course-name">{{ course.name }}</h4>
              <p class="course-desc">{{ course.description }}</p>
              <div class="course-meta">
                <span class="teacher">{{ course.teacher }}</span>
                <span class="students">{{ course.students }}人学习</span>
              </div>
              <div class="course-price">
                <span class="price">¥{{ course.price }}</span>
                <span class="original-price" v-if="course.originalPrice">¥{{ course.originalPrice }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 新品课程 -->
    <div class="section">
      <div class="section-header">
        <h3>新品课程</h3>
        <el-button type="text" @click="$router.push('/courses')">查看更多</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="6" v-for="course in newCourses" :key="course.id">
          <el-card class="course-card" @click="goToCourse(course.id)">
            <img :src="course.coverImage || '/placeholder.jpg'" class="course-image" />
            <div class="course-info">
              <h4 class="course-name">{{ course.name }}</h4>
              <p class="course-desc">{{ course.description }}</p>
              <div class="course-meta">
                <span class="teacher">{{ course.teacher }}</span>
                <span class="students">{{ course.students }}人学习</span>
              </div>
              <div class="course-price">
                <span class="price">¥{{ course.price }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getHotCourses, getNewCourses } from '@/api/course'

const router = useRouter()
const hotCourses = ref([])
const newCourses = ref([])

const fetchHotCourses = async () => {
  try {
    const res = await getHotCourses(8)
    hotCourses.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const fetchNewCourses = async () => {
  try {
    const res = await getNewCourses(8)
    newCourses.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const goToCourse = (id) => {
  router.push(`/course/${id}`)
}

onMounted(() => {
  fetchHotCourses()
  fetchNewCourses()
})
</script>

<style scoped>
.banner {
  margin-bottom: 30px;
  border-radius: 8px;
  overflow: hidden;
}

.banner-item {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #fff;
}

.banner-item h2 {
  font-size: 36px;
  margin-bottom: 10px;
}

.banner-item p {
  font-size: 18px;
}

.section {
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 20px;
  color: #333;
}

.course-card {
  cursor: pointer;
  transition: transform 0.3s;
  margin-bottom: 20px;
}

.course-card:hover {
  transform: translateY(-5px);
}

.course-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.course-info {
  padding: 10px 0;
}

.course-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-desc {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.teacher {
  color: #409eff;
}

.course-price {
  display: flex;
  align-items: center;
  gap: 10px;
}

.price {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
}

.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
}
</style>
