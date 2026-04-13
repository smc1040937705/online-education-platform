<template>
  <div class="addresses">
    <div class="header">
      <h2>收货地址</h2>
      <el-button type="primary" @click="showAddDialog">添加地址</el-button>
    </div>

    <el-empty v-if="addresses.length === 0" description="暂无收货地址" />

    <el-row :gutter="20" v-else>
      <el-col :span="8" v-for="addr in addresses" :key="addr.id">
        <el-card class="address-card" :class="{ 'default': addr.isDefault }">
          <div class="address-header">
            <span class="name">{{ addr.receiverName }}</span>
            <span class="phone">{{ addr.receiverPhone }}</span>
            <el-tag v-if="addr.isDefault" type="success" size="small">默认</el-tag>
          </div>
          <div class="address-detail">
            {{ addr.province }}{{ addr.city }}{{ addr.district }}
            <br />
            {{ addr.detailAddress }}
          </div>
          <div class="address-actions">
            <el-button type="primary" link @click="editAddress(addr)">编辑</el-button>
            <el-button type="danger" link @click="deleteAddress(addr.id)">删除</el-button>
            <el-button v-if="!addr.isDefault" type="success" link @click="setDefault(addr.id)">
              设为默认
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加/编辑地址对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑地址' : '添加地址'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="form.receiverName" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="form.province" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="form.district" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="form.detailAddress" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="邮编">
          <el-input v-model="form.zipCode" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAddresses, addAddress, updateAddress, deleteAddress as deleteAddressApi, setDefaultAddress } from '@/api/address'

const addresses = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref()
const currentId = ref(null)

const form = reactive({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  zipCode: ''
})

const rules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const fetchAddresses = async () => {
  try {
    const res = await getAddresses()
    addresses.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const showAddDialog = () => {
  isEdit.value = false
  currentId.value = null
  Object.keys(form).forEach(key => form[key] = '')
  dialogVisible.value = true
}

const editAddress = (addr) => {
  isEdit.value = true
  currentId.value = addr.id
  Object.assign(form, addr)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateAddress(currentId.value, form)
      ElMessage.success('修改成功')
    } else {
      await addAddress(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchAddresses()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const deleteAddress = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAddressApi(id)
    ElMessage.success('删除成功')
    fetchAddresses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const setDefault = async (id) => {
  try {
    await setDefaultAddress(id)
    ElMessage.success('设置成功')
    fetchAddresses()
  } catch (error) {
    console.error(error)
  }
}

fetchAddresses()
</script>

<style scoped>
.addresses {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  color: #333;
}

.address-card {
  margin-bottom: 20px;
  position: relative;
}

.address-card.default {
  border: 2px solid #67c23a;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.address-header .name {
  font-weight: bold;
  color: #333;
}

.address-header .phone {
  color: #666;
}

.address-detail {
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
}

.address-actions {
  display: flex;
  gap: 10px;
}
</style>
