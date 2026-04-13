<template>
  <div class="products">
    <div class="filter-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索商品"
        :prefix-icon="Search"
        clearable
        @change="fetchProducts"
        style="width: 300px; margin-right: 20px;"
      />
      <el-select
        v-model="categoryId"
        placeholder="选择分类"
        clearable
        @change="fetchProducts"
        style="width: 200px; margin-right: 20px;"
      >
        <el-option
          v-for="category in categories"
          :key="category.id"
          :label="category.name"
          :value="category.id"
        />
      </el-select>
      <el-button type="primary" @click="fetchProducts">搜索</el-button>
    </div>

    <el-row :gutter="20" v-loading="loading">
      <el-col :span="6" v-for="product in products" :key="product.id">
        <el-card class="product-card" @click="goToProduct(product.id)">
          <img :src="product.mainImage || '/placeholder.jpg'" class="product-image" />
          <div class="product-info">
            <h4 class="product-name">{{ product.name }}</h4>
            <p class="product-desc">{{ product.description }}</p>
            <div class="product-price">
              <span class="price">¥{{ product.price }}</span>
              <span class="sales">销量: {{ product.sales }}</span>
            </div>
            <div class="product-tags">
              <el-tag v-if="product.isHot" type="danger" size="small">热门</el-tag>
              <el-tag v-if="product.isNew" type="success" size="small">新品</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

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
import { Search } from '@element-plus/icons-vue'
import { getProducts } from '@/api/product'
import { getCategories } from '@/api/category'

const router = useRouter()
const loading = ref(false)
const products = ref([])
const categories = ref([])
const keyword = ref('')
const categoryId = ref(null)
const page = ref(1)
const size = ref(12)
const total = ref(0)

const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await getProducts({
      page: page.value,
      size: size.value,
      keyword: keyword.value,
      categoryId: categoryId.value
    })
    products.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handlePageChange = (newPage) => {
  page.value = newPage
  fetchProducts()
}

const goToProduct = (id) => {
  router.push(`/product/${id}`)
}

onMounted(() => {
  fetchProducts()
  fetchCategories()
})
</script>

<style scoped>
.products {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
}

.filter-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.product-card {
  cursor: pointer;
  transition: transform 0.3s;
  margin-bottom: 20px;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.product-info {
  padding: 10px 0;
}

.product-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.price {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
}

.sales {
  font-size: 12px;
  color: #999;
}

.product-tags {
  display: flex;
  gap: 5px;
}

.pagination {
  margin-top: 20px;
  justify-content: center;
  display: flex;
}
</style>
