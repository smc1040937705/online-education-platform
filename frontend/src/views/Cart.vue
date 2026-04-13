<template>
  <div class="cart">
    <h2>购物车</h2>
    
    <el-empty v-if="carts.length === 0" description="购物车是空的" />
    
    <div v-else>
      <el-table :data="carts" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
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
            <span class="price">¥{{ row.productPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="200">
          <template #default="{ row }">
            <el-input-number
              v-model="row.quantity"
              :min="1"
              :max="row.stock"
              @change="updateQuantity(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="150">
          <template #default="{ row }">
            <span class="subtotal">¥{{ (row.productPrice * row.quantity).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="danger" link @click="removeCart(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="cart-footer">
        <div class="select-all">
          <el-checkbox v-model="allSelected" @change="toggleSelectAll">全选</el-checkbox>
        </div>
        <div class="cart-summary">
          <span class="selected-count">已选 {{ selectedCount }} 件商品</span>
          <span class="total-price">
            合计: <strong>¥{{ totalPrice.toFixed(2) }}</strong>
          </span>
          <el-button type="danger" size="large" :disabled="selectedCount === 0" @click="checkout">
            去结算
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCarts, updateCartQuantity, deleteCart, selectAllCart } from '@/api/cart'

const router = useRouter()
const carts = ref([])
const selectedItems = ref([])

const selectedCount = computed(() => selectedItems.value.length)

const totalPrice = computed(() => {
  return selectedItems.value.reduce((sum, item) => {
    return sum + item.productPrice * item.quantity
  }, 0)
})

const allSelected = computed({
  get: () => carts.value.length > 0 && carts.value.every(item => item.selected === 1),
  set: (val) => toggleSelectAll(val)
})

const fetchCarts = async () => {
  try {
    const res = await getCarts()
    carts.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleSelectionChange = (selection) => {
  selectedItems.value = selection
}

const updateQuantity = async (row) => {
  try {
    await updateCartQuantity(row.id, { quantity: row.quantity })
  } catch (error) {
    console.error(error)
  }
}

const removeCart = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCart(id)
    ElMessage.success('删除成功')
    fetchCarts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const toggleSelectAll = async (val) => {
  try {
    await selectAllCart({ selected: val ? 1 : 0 })
    fetchCarts()
  } catch (error) {
    console.error(error)
  }
}

const checkout = () => {
  const selectedIds = selectedItems.value.map(item => item.id)
  router.push({
    path: '/order-confirm',
    query: { cartIds: selectedIds.join(',') }
  })
}

onMounted(() => {
  fetchCarts()
})
</script>

<style scoped>
.cart {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  min-height: 500px;
}

.cart h2 {
  margin-bottom: 20px;
  color: #333;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 15px;
}

.product-thumb {
  width: 80px;
  height: 80px;
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

.price {
  color: #666;
}

.subtotal {
  color: #f56c6c;
  font-weight: bold;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.cart-summary {
  display: flex;
  align-items: center;
  gap: 20px;
}

.selected-count {
  color: #666;
}

.total-price {
  font-size: 16px;
  color: #666;
}

.total-price strong {
  color: #f56c6c;
  font-size: 24px;
}
</style>
