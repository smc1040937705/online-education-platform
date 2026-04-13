<template>
  <div class="profile">
    <h2>个人中心</h2>

    <el-card class="profile-card">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="头像">
          <el-input v-model="form.avatar" placeholder="请输入头像URL" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>账户余额</span>
        </div>
      </template>
      <div class="balance-info">
        <div class="balance-amount">¥{{ userStore.userInfo?.balance || 0 }}</div>
        <el-button type="primary" @click="showRecharge = true">充值</el-button>
      </div>
    </el-card>

    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleChangePassword" :loading="changingPassword">
            修改密码
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 充值对话框 -->
    <el-dialog v-model="showRecharge" title="账户充值" width="400px">
      <el-form :model="rechargeForm" label-width="80px">
        <el-form-item label="充值金额">
          <el-input-number v-model="rechargeForm.amount" :min="1" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRecharge = false">取消</el-button>
        <el-button type="primary" @click="handleRecharge" :loading="recharging">确认充值</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getProfile, updateProfile, updatePassword, recharge } from '@/api/user'

const userStore = useUserStore()
const formRef = ref()
const passwordFormRef = ref()
const saving = ref(false)
const changingPassword = ref(false)
const showRecharge = ref(false)
const recharging = ref(false)

const form = reactive({
  username: '',
  email: '',
  phone: '',
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rechargeForm = reactive({
  amount: 100
})

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const fetchProfile = async () => {
  try {
    const res = await getProfile()
    Object.assign(form, res.data)
  } catch (error) {
    console.error(error)
  }
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    await updateProfile({
      email: form.email,
      phone: form.phone,
      avatar: form.avatar
    })
    ElMessage.success('保存成功')
    userStore.fetchUserInfo()
  } catch (error) {
    console.error(error)
  } finally {
    saving.value = false
  }
}

const handleChangePassword = async () => {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return

  changingPassword.value = true
  try {
    await updatePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    console.error(error)
  } finally {
    changingPassword.value = false
  }
}

const handleRecharge = async () => {
  recharging.value = true
  try {
    await recharge({ amount: rechargeForm.amount })
    ElMessage.success('充值成功')
    showRecharge.value = false
    userStore.fetchUserInfo()
  } catch (error) {
    console.error(error)
  } finally {
    recharging.value = false
  }
}

onMounted(() => {
  fetchProfile()
})
</script>

<style scoped>
.profile {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
}

.profile h2 {
  margin-bottom: 20px;
  color: #333;
}

.profile-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
}

.balance-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.balance-amount {
  font-size: 32px;
  color: #f56c6c;
  font-weight: bold;
}
</style>
