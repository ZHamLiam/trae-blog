# Trae Blog 博客系统

一个现代化的前后端分离博客系统，提供文章发布、评论、点赞、分类、标签等功能，支持Markdown编辑，具有阅读进度显示等特色功能。

## 项目介绍

Trae Blog是一个功能完善的博客系统，采用前后端分离架构，前端使用Vue 3构建，后端使用Spring Boot开发。系统包含前台展示和后台管理两大部分，支持用户注册、登录、文章发布、评论、点赞等功能，并实现了RBAC权限管理。

## 技术栈

### 前端技术栈

- **核心框架**：Vue 3 + Vite
- **状态管理**：Pinia
- **路由管理**：Vue Router 4
- **UI组件库**：Ant Design Vue 4
- **HTTP客户端**：Axios
- **Markdown编辑器**：md-editor-v3
- **图表库**：ECharts

### 后端技术栈

- **核心框架**：Spring Boot 2.7
- **ORM框架**：MyBatis-Plus 3.5
- **数据库**：MySQL 8.0
- **数据库连接池**：Druid
- **缓存**：Redis
- **安全框架**：Spring Security + JWT
- **API文档**：Knife4j (基于Swagger)
- **工具库**：Hutool

## 功能特点

### 前台功能

- **文章浏览**：支持分页、分类、标签筛选
- **文章详情**：Markdown渲染、阅读进度显示、预计阅读时间
- **文章互动**：评论、点赞功能
- **用户中心**：注册、登录、个人信息管理
- **搜索功能**：支持文章标题、内容搜索
- **响应式设计**：适配不同设备屏幕

### 后台功能

- **仪表盘**：访问统计、用户增长、文章数据分析
- **内容管理**：文章、分类、标签、评论管理
- **用户管理**：用户信息、角色分配
- **权限管理**：基于RBAC的角色权限管理
- **系统设置**：网站配置、个人资料设置

## 系统架构

```
├── frontend                 # 前端项目
│   ├── public               # 静态资源
│   ├── src                  # 源代码
│   │   ├── api              # API接口
│   │   ├── assets           # 主题和样式资源
│   │   ├── components       # 公共组件
│   │   ├── router           # 路由配置
│   │   ├── store            # Pinia状态管理
│   │   ├── utils            # 工具函数
│   │   ├── views            # 页面视图
│   │   │   ├── admin        # 后台管理页面
│   │   │   ├── auth         # 认证页面
│   │   │   └── front        # 前台展示页面
│   │   ├── App.vue          # 根组件
│   │   └── main.js          # 入口文件
│   └── vite.config.js       # Vite配置
│
├── backend                  # 后端项目
│   ├── src/main             # 源代码
│   │   ├── java/com/trae/blog
│   │   │   ├── common       # 公共类
│   │   │   ├── config       # 配置类
│   │   │   ├── controller   # 控制器
│   │   │   ├── dto          # 数据传输对象
│   │   │   ├── entity       # 实体类
│   │   │   ├── mapper       # MyBatis接口
│   │   │   ├── security     # 安全相关
│   │   │   ├── service      # 服务层
│   │   │   └── util         # 工具类
│   │   └── resources        # 资源文件
│   │       ├── db           # 数据库脚本
│   │       └── application.yml # 应用配置
│   └── pom.xml              # Maven配置
```

## 特色功能展示

### 阅读进度显示

系统实现了文章阅读进度显示功能，当用户阅读文章时，顶部会显示当前阅读进度条，直观展示阅读位置。

### Markdown编辑与渲染

使用md-editor-v3实现Markdown编辑器，支持实时预览、代码高亮等功能，为创作者提供良好的写作体验。

### 数据可视化

后台管理系统集成ECharts图表库，提供访问量、用户增长、文章发布等数据的可视化展示。

## 安装部署

### 环境要求

- Node.js 16+
- JDK 1.8+
- MySQL 8.0+
- Redis 6.0+

### 前端部署

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 开发环境运行
npm run dev

# 生产环境构建
npm run build
```

### 后端部署

```bash
# 进入后端目录
cd backend

# Maven打包
mvn clean package

# 运行应用
java -jar target/blog-0.0.1-SNAPSHOT.jar
```

### 数据库配置

1. 创建MySQL数据库：`db_trae_blog_schema`
2. 导入数据库脚本：`backend/src/main/resources/db/schema.sql`
3. 导入初始数据：`backend/src/main/resources/db/data.sql`
4. 修改数据库连接配置：`backend/src/main/resources/application.yml`

## 使用说明

### 前台访问

- 首页：`http://localhost:5173/`
- 文章详情：`http://localhost:5173/article/{id}`
- 分类页：`http://localhost:5173/category/{id}`
- 标签页：`http://localhost:5173/tag/{id}`
- 搜索页：`http://localhost:5173/search`

### 后台管理

- 登录页：`http://localhost:5173/login`
- 管理首页：`http://localhost:5173/admin/dashboard`

## 开发计划

- [ ] 支持多语言国际化
- [ ] 集成第三方登录
- [ ] 移动端APP开发
- [ ] 文章订阅功能
- [ ] 性能优化与缓存改进

## 贡献指南

欢迎提交问题和功能需求，也欢迎提交Pull Request贡献代码。

## 许可证

[MIT License](LICENSE)