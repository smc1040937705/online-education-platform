<template>
  <div class="enrollments-page">
    <h2>我的课程</h2>
    
    <el-tabs v-model="activeTab">
      <el-tab-pane label="全部课程" name="all">
        <el-empty v-if="enrollments.length === 0" description="暂无报名课程" />
        <el-table v-else :data="enrollments" stripe>
          <el-table-column prop="courseName" label="课程名称">
            <template #default="{ row }">
              <div class="course-cell">
                <img :src="row.courseCover || '/placeholder.jpg'" class="course-thumb" />
                <span>{{ row.courseName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="payAmount" label="金额">
            <template #default="{ row }">
              ¥{{ row.payAmount }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="progress" label="进度">
            <template #default="{ row }">
              <el-progress :percentage="row.progress || 0" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button v-if="row.status === 0" type="primary" size="small" @click="handlePay(row.id)">去支付</el-button>
              <el-button v-if="row.status === 1" type="success" size="small" @click="goToLearn(row)">开始学习</el-button>
              <el-button v-if="row.status !== 2" type="danger" size="small" @click="handleCancel(row.id)">取消</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="待支付" name="0">
        <el-table :data="enrollments.filter(e => e.status === 0)" stripe>
          <el-table-column prop="courseName" label="课程名称" />
          <el-table-column prop="payAmount" label="金额" />
          <el-table-column label="操作">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handlePay(row.id)">去支付</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="学习中" name="1">
        <el-table :data="enrollments.filter(e => e.status === 1)" stripe>
          <el-table-column prop="courseName" label="课程名称" />
          <el-table-column prop="progress" label="进度">
            <template #default="{ row }">
              <el-progress :percentage="row.progress || 0" />
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="{ row }">
              <el-button type="success" size="small" @click="goToLearn(row)">继续学习</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="已完成" name="2">
        <el-table :data="enrollments.filter(e => e.status === 2)" stripe>
          <el-table-column prop="courseName" label="课程名称" />
          <el-table-column prop="completeTime" label="完成时间" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getEnrollmentList, payEnrollment, cancelEnrollment } from '@/api/enrollment'

const router = useRouter()
const enrollments = ref([])
const activeTab = ref('all')

const fetchEnrollments = async () => {
  try {
    const res = await getEnrollmentList()
    enrollments.value = res.data.records || []
  } catch (error) {
    console.error(error)
  }
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info', 4: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待支付', 1: '学习中', 2: '已完成', 3: '已取消', 4: '已取消' }
  return texts[status] || '未知'
}

const handlePay = async (id) => {
  try {
    await payEnrollment(id)
    ElMessage.success('支付成功')
    fetchEnrollments()
  } catch (error) {
    ElMessage.error('支付失败')
  }
}

const handleCancel = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消报名吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelEnrollment(id)
    ElMessage.success('取消成功')
    fetchEnrollments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

const goToLearn = (row) => {
  router.push(`/course/${row.courseId}`)
}

onMounted(() => {
  fetchEnrollments()
})
</script>

<style scoped>
.enrollments-page {
  padding: 20px;
}

.enrollments-page h2 {
  margin-bottom: 20px;
}

.course-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.course-thumb {
  width: 60px;
  height: 40px;
  object-fit: cover;
  border-radius: 4px;
}
</style>
