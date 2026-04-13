<template>
  <div class="order-confirm">
    <h2>确认订单</h2>

    <div class="section">
      <h3>收货地址</h3>
      <el-radio-group v-model="selectedAddress" v-if="addresses.length > 0">
        <el-radio v-for="addr in addresses" :key="addr.id" :label="addr.id" border>
          <div class="address-item">
            <div class="address-header">
              <span class="name">{{ addr.receiverName }}</span>
              <span class="phone">{{ addr.receiverPhone }}</span>
              <el-tag v-if="addr.isDefault" type="success" size="small">默认</el-tag>
            </div>
            <div class="address-detail">
              {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}
            </div>
          </div>
        </el-radio>
      </el-radio-group>
      <el-empty v-else description="暂无收货地址">
        <el-button type="primary" @click="$router.push('/addresses')">去添加</el-button>
      </el-empty>
    </div>

    <div class="section">
      <h3>商品清单</h3>
      <el-table :data="cartItems" border>
        <el-table-column label="商品" width="400">
          <template #default="{ row }">
            <div class="product-cell">
              <el-image :src="row.productImage || '/placeholder.jpg'" class="product-thumb" />
              <div class="product-info">
                <div class="product-name">{{ row.productName }}</div>
                <div class="product-price">¥{{ row.productPrice }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="150">
          <template #default="{ row }">
            <span>¥{{ row.productPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="150">
          <template #default="{ row }">
            <span>{{ row.quantity }}</span>
          </template>
        </el-table-column>
        <el-table-column label="小计" width="150">
          <template #default="{ row }">
            <span class="subtotal">¥{{ (row.productPrice * row.quantity).toFixed(2) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="section">
      <h3>订单备注</h3>
      <el-input
        v-model="remark"
        type="textarea"
        :rows="3"
        placeholder="请输入订单备注（选填）"
      />
    </div>

    <div class="section order-summary">
      <div class="summary-row">
        <span>商品总额:</span>
        <span>¥{{ totalAmount.toFixed(2) }}</span>
      </div>
      <div class="summary-row">
        <span>运费:</span>
        <span>¥{{ freightAmount.toFixed(2) }}</span>
      </div>
      <div class="summary-row total">
        <span>应付总额:</span>
        <span class="total-price">¥{{ payAmount.toFixed(2) }}</span>
      </div>
      <div class="submit-btn">
        <el-button type="primary" size="large" :disabled="!selectedAddress" @click="submitOrder">
          提交订单
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAddresses } from '@/api/address'
import { getCarts } from '@/api/cart'
import { createOrder } from '@/api/order'

const route = useRoute()
const router = useRouter()
const addresses = ref([])
const selectedAddress = ref(null)
const cartItems = ref([])
const remark = ref('')

const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    return sum + item.productPrice * item.quantity
  }, 0)
})

const freightAmount = computed(() => {
  return totalAmount.value >= 99 ? 0 : 10
})

const payAmount = computed(() => {
  return totalAmount.value + freightAmount.value
})

const fetchAddresses = async () => {
  try {
    const res = await getAddresses()
    addresses.value = res.data
    const defaultAddr = addresses.value.find(addr => addr.isDefault === 1)
    if (defaultAddr) {
      selectedAddress.value = defaultAddr.id
    } else if (addresses.value.length > 0) {
      selectedAddress.value = addresses.value[0].id
    }
  } catch (error) {
    console.error(error)
  }
}

const fetchCartItems = async () => {
  const cartIds = route.query.cartIds?.split(',') || []
  if (cartIds.length === 0) {
    ElMessage.warning('请先选择商品')
    router.push('/cart')
    return
  }
  try {
    const res = await getCarts()
    cartItems.value = res.data.filter(item => cartIds.includes(String(item.id)))
    if (cartItems.value.length === 0) {
      ElMessage.warning('商品信息有误')
      router.push('/cart')
    }
  } catch (error) {
    console.error(error)
  }
}

const submitOrder = async () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  try {
    const cartIds = cartItems.value.map(item => item.id)
    await createOrder({
      cartIds,
      addressId: selectedAddress.value,
      remark: remark.value
    })
    ElMessage.success('订单创建成功')
    router.push(`/orders`)
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchAddresses()
  fetchCartItems()
})
</script>

<style scoped>
.order-confirm {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
}

.order-confirm h2 {
  margin-bottom: 20px;
  color: #333;
}

.section {
  margin-bottom: 30px;
}

.section h3 {
  margin-bottom: 15px;
  color: #333;
  font-size: 16px;
}

.address-item {
  padding: 10px 0;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 5px;
}

.address-header .name {
  font-weight: bold;
  color: #333;
}

.address-header .phone {
  color: #666;
}

.address-detail {
  color: #999;
  font-size: 14px;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 15px;
}

.product-thumb {
  width: 60px;
  height: 60px;
  border-radius: 4px;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.product-price {
  font-size: 12px;
  color: #999;
}

.subtotal {
  color: #f56c6c;
  font-weight: bold;
}

.order-summary {
  background-color: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  color: #666;
}

.summary-row.total {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #ddd;
  font-size: 16px;
}

.total-price {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}

.submit-btn {
  margin-top: 20px;
  text-align: right;
}
</style>
