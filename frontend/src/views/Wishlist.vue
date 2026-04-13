<template>
  <div class="wishlist-page">
    <h2>我的心愿单</h2>
    <el-empty v-if="wishlist.length === 0" description="暂无心愿单内容" />
    <el-row :gutter="20" v-else>
      <el-col :span="6" v-for="item in wishlist" :key="item.id">
        <el-card class="wishlist-card">
          <img :src="item.courseCover || '/placeholder.jpg'" class="course-image" />
          <div class="course-info">
            <h4 class="course-name">{{ item.courseName }}</h4>
            <div class="course-price">
              <span class="price">¥{{ item.price }}</span>
            </div>
            <div class="card-actions">
              <el-button type="primary" size="small" @click="goToCourse(item.courseId)">立即报名</el-button>
              <el-button type="danger" size="small" @click="handleRemove(item.id)">删除</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getWishlist, removeFromWishlist } from '@/api/wishlist'

const router = useRouter()
const wishlist = ref([])

const fetchWishlist = async () => {
  try {
    const res = await getWishlist()
    wishlist.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleRemove = async (id) => {
  try {
    await ElMessageBox.confirm('确定要从心愿单中移除吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await removeFromWishlist(id)
    ElMessage.success('移除成功')
    fetchWishlist()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('移除失败')
    }
  }
}

const goToCourse = (id) => {
  router.push(`/course/${id}`)
}

onMounted(() => {
  fetchWishlist()
})
</script>

<style scoped>
.wishlist-page {
  padding: 20px;
}

.wishlist-page h2 {
  margin-bottom: 20px;
}

.wishlist-card {
  margin-bottom: 20px;
}

.course-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.course-info {
  padding: 10px 0;
}

.course-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-price {
  margin-bottom: 10px;
}

.price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.card-actions {
  display: flex;
  gap: 10px;
}
</style>
