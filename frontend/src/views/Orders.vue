<template>
  <div class="orders">
    <h2>我的订单</h2>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待付款" name="pending" />
      <el-tab-pane label="待发货" name="paid" />
      <el-tab-pane label="待收货" name="shipped" />
      <el-tab-pane label="已完成" name="completed" />
    </el-tabs>

    <el-empty v-if="orders.length === 0" description="暂无订单" />

    <div v-else class="order-list">
      <el-card v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <span class="order-no">订单号: {{ order.orderNo }}</span>
          <span class="order-time">{{ order.createdAt }}</span>
          <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
        </div>

        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <el-image :src="item.productImage || '/placeholder.jpg'" class="item-image" />
            <div class="item-info">
              <div class="item-name">{{ item.productName }}</div>
              <div class="item-price">¥{{ item.productPrice }} x {{ item.quantity }}</div>
            </div>
            <div class="item-total">¥{{ item.totalPrice }}</div>
          </div>
        </div>

        <div class="order-footer">
          <div class="order-total">
            <span>共 {{ order.items.length }} 件商品</span>
            <span class="total-amount">
              实付: <strong>¥{{ order.payAmount }}</strong>
            </span>
          </div>
          <div class="order-actions">
            <el-button v-if="order.status === 0" type="primary" @click="payOrder(order.id)">
              立即支付
            </el-button>
            <el-button v-if="order.status === 0" @click="cancelOrder(order.id)">
              取消订单
            </el-button>
            <el-button v-if="order.status === 2" type="primary" @click="receiveOrder(order.id)">
              确认收货
            </el-button>
            <el-button @click="viewOrder(order.id)">查看详情</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <el-pagination
      v-if="total > 0"
      class="pagination"
      :current-page="page"
      :page-size="size"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders, cancelOrder as cancelOrderApi, payOrder as payOrderApi, receiveOrder as receiveOrderApi } from '@/api/order'

const router = useRouter()
const activeTab = ref('all')
const orders = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const statusMap = {
  all: null,
  pending: 0,
  paid: 1,
  shipped: 2,
  completed: 3
}

const getStatusText = (status) => {
  const map = {
    '-1': '已取消',
    '0': '待付款',
    '1': '待发货',
    '2': '待收货',
    '3': '已完成'
  }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = {
    '-1': 'info',
    '0': 'warning',
    '1': 'primary',
    '2': 'success',
    '3': 'success'
  }
  return map[status] || 'info'
}

const fetchOrders = async () => {
  try {
    const res = await getOrders({ page: page.value, size: size.value })
    let data = res.data.records
    if (activeTab.value !== 'all') {
      data = data.filter(order => order.status === statusMap[activeTab.value])
    }
    orders.value = data
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  }
}

const handleTabChange = () => {
  page.value = 1
  fetchOrders()
}

const handlePageChange = (newPage) => {
  page.value = newPage
  fetchOrders()
}

const payOrder = async (id) => {
  try {
    await payOrderApi(id)
    ElMessage.success('支付成功')
    fetchOrders()
  } catch (error) {
    console.error(error)
  }
}

const cancelOrder = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelOrderApi(id)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const receiveOrder = async (id) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await receiveOrderApi(id)
    ElMessage.success('确认收货成功')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const viewOrder = (id) => {
  router.push(`/order/${id}`)
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.orders {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  min-height: 500px;
}

.orders h2 {
  margin-bottom: 20px;
  color: #333;
}

.order-list {
  margin-top: 20px;
}

.order-card {
  margin-bottom: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  margin-bottom: 15px;
}

.order-no {
  color: #666;
  font-size: 14px;
}

.order-time {
  color: #999;
  font-size: 14px;
}

.order-items {
  margin-bottom: 15px;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.order-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  margin-right: 15px;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.item-price {
  font-size: 12px;
  color: #999;
}

.item-total {
  font-size: 14px;
  color: #f56c6c;
  font-weight: bold;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.order-total {
  display: flex;
  align-items: center;
  gap: 20px;
  color: #666;
}

.total-amount strong {
  color: #f56c6c;
  font-size: 18px;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  justify-content: center;
  display: flex;
}
</style>
