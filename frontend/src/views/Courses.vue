<template>
  <div class="courses-page">
    <div class="filter-bar">
      <el-select v-model="categoryId" placeholder="选择分类" clearable @change="fetchCourses">
        <el-option label="全部分类" :value="null" />
        <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
      </el-select>
      <el-input v-model="keyword" placeholder="搜索课程" clearable @keyup.enter="fetchCourses" style="width: 200px; margin-left: 10px;">
        <template #append>
          <el-button icon="Search" @click="fetchCourses" />
        </template>
      </el-input>
    </div>

    <el-row :gutter="20" v-loading="loading">
      <el-col :span="6" v-for="course in courses" :key="course.id">
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

    <el-pagination
      v-if="total > 0"
      :current-page="page"
      :page-size="size"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="handlePageChange"
      style="margin-top: 20px; text-align: center;"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCourseList, getHotCourses } from '@/api/course'
import { getCategoryList } from '@/api/category'

const router = useRouter()
const courses = ref([])
const categories = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(12)
const total = ref(0)
const categoryId = ref(null)
const keyword = ref('')

const fetchCourses = async () => {
  loading.value = true
  try {
    const res = await getCourseList({
      page: page.value,
      size: size.value,
      categoryId: categoryId.value,
      keyword: keyword.value
    })
    courses.value = res.data.data
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data.data || []
  } catch (error) {
    console.error(error)
  }
}

const handlePageChange = (newPage) => {
  page.value = newPage
  fetchCourses()
}

const goToCourse = (id) => {
  router.push(`/course/${id}`)
}

onMounted(() => {
  fetchCourses()
  fetchCategories()
})
</script>

<style scoped>
.courses-page {
  padding: 20px;
}

.filter-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
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
