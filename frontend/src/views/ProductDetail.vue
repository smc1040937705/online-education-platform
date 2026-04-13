<template>
  <div class="product-detail" v-loading="loading">
    <el-row :gutter="40" v-if="product">
      <el-col :span="10">
        <el-image
          :src="product.mainImage || '/placeholder.jpg'"
          fit="cover"
          class="product-image"
        />
      </el-col>
      <el-col :span="14">
        <div class="product-info">
          <h1 class="product-title">{{ product.name }}</h1>
          <p class="product-desc">{{ product.description }}</p>
          <div class="product-price-box">
            <span class="price-label">价格</span>
            <span class="price">¥{{ product.price }}</span>
            <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
          </div>
          <div class="product-meta">
            <div class="meta-item">
              <span class="label">销量:</span>
              <span>{{ product.sales }}</span>
            </div>
            <div class="meta-item">
              <span class="label">库存:</span>
              <span>{{ product.stock }}</span>
            </div>
            <div class="meta-item">
              <span class="label">分类:</span>
              <span>{{ product.categoryName }}</span>
            </div>
          </div>
          <div class="product-actions">
            <div class="quantity-selector">
              <span class="label">数量:</span>
              <el-input-number v-model="quantity" :min="1" :max="product.stock" />
            </div>
            <div class="action-buttons">
              <el-button type="primary" size="large" @click="addToCart">
                <el-icon><Shopping-cart /></el-icon>
                加入购物车
              </el-button>
              <el-button type="danger" size="large" @click="buyNow">立即购买</el-button>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <div class="product-detail-content" v-if="product">
      <el-tabs>
        <el-tab-pane label="商品详情">
          <div class="detail-content" v-html="product.detail"></div>
        </el-tab-pane>
        <el-tab-pane label="商品参数">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="商品名称">{{ product.name }}</el-descriptions-item>
            <el-descriptions-item label="商品分类">{{ product.categoryName }}</el-descriptions-item>
            <el-descriptions-item label="商品原价">¥{{ product.originalPrice || product.price }}</el-descriptions-item>
            <el-descriptions-item label="商品现价">¥{{ product.price }}</el-descriptions-item>
            <el-descriptions-item label="商品库存">{{ product.stock }}</el-descriptions-item>
            <el-descriptions-item label="商品销量">{{ product.sales }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart } from '@element-plus/icons-vue'
import { getProduct } from '@/api/product'
import { addToCart as addToCartApi } from '@/api/cart'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const product = ref(null)
const quantity = ref(1)

const fetchProduct = async () => {
  loading.value = true
  try {
    const res = await getProduct(route.params.id)
    product.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const addToCart = async () => {
  try {
    await addToCartApi({
      productId: product.value.id,
      quantity: quantity.value
    })
    ElMessage.success('已加入购物车')
  } catch (error) {
    console.error(error)
  }
}

const buyNow = async () => {
  try {
    await addToCartApi({
      productId: product.value.id,
      quantity: quantity.value
    })
    router.push('/cart')
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchProduct()
})
</script>

<style scoped>
.product-detail {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
}

.product-image {
  width: 100%;
  height: 400px;
  border-radius: 8px;
}

.product-info {
  padding: 20px;
}

.product-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.product-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

.product-price-box {
  background-color: #f5f5f5;
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.price-label {
  font-size: 14px;
  color: #999;
  margin-right: 10px;
}

.price {
  font-size: 28px;
  color: #f56c6c;
  font-weight: bold;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
  margin-left: 10px;
}

.product-meta {
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
}

.meta-item {
  font-size: 14px;
  color: #666;
}

.meta-item .label {
  color: #999;
  margin-right: 5px;
}

.product-actions {
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.quantity-selector .label {
  font-size: 14px;
  color: #666;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.product-detail-content {
  margin-top: 40px;
}

.detail-content {
  line-height: 1.8;
  color: #666;
}
</style>
