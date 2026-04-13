# 在线教育平台

一个基于 Spring Boot + Vue 3 的在线教育平台系统，提供课程浏览、购买、学习管理等核心功能。

## 技术栈

### 后端
- Spring Boot 3.1.5
- JDK 21
- MyBatis-Plus
- Spring Security + JWT
- H2 数据库

### 前端
- Vue 3 + Composition API
- Vue Router 4
- Pinia (状态管理)
- Element Plus
- Axios

## 项目结构

```
├── backend/                 # Spring Boot 后端
│   ├── src/main/java/
│   │   └── com/example/ecommerce/
│   │       ├── config/         # 配置类
│   │       ├── controller/    # 控制器
│   │       ├── dto/           # 数据传输对象
│   │       ├── entity/        # 实体类
│   │       ├── mapper/       # MyBatis Mapper
│   │       ├── security/      # 安全配置
│   │       └── service/      # 业务逻辑
│   └── pom.xml
│
└── frontend/               # Vue 3 前端
    ├── src/
    │   ├── api/             # API 接口
    │   ├── router/          # 路由配置
    │   ├── stores/          # Pinia 状态管理
    │   └── views/           # 页面组件
    └── package.json
```

## 功能模块

### 用户模块
- 用户注册/登录
- 个人资料管理
- JWT 身份认证

### 课程模块
- 课程列表浏览（分页、分类、搜索）
- 热门课程推荐
- 最新课程展示
- 课程详情查看

### 学习模块
- 课程报名
- 我的课程列表
- 学习进度管理
- 心愿单收藏

## 快速开始

### 后端启动

```bash
cd backend
mvn spring-boot:run
```

后端服务默认运行在：http://localhost:8080

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端服务默认运行在：http://localhost:3000

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/auth/login` | POST | 用户登录 |
| `/auth/register` | POST | 用户注册 |
| `/courses` | GET | 课程列表 |
| `/courses/{id}` | GET | 课程详情 |
| `/courses/hot` | GET | 热门课程 |
| `/enrollments` | GET | 用户报名列表 |
| `/enrollments` | POST | 创建报名 |
| `/wishlist` | GET | 心愿单列表 |
| `/wishlist` | POST | 添加到心愿单 |

## 默认账号

注册后即可登录使用。

## 测试

### 后端单元测试
```bash
cd backend
mvn test
```

### 前端测试
```bash
cd frontend
npm test
```

## License

MIT
