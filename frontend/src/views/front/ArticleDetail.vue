<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Card, Avatar, Tag, Divider, Button, Comment, Form, Input, message } from 'ant-design-vue';
import { LikeOutlined, MessageOutlined, EyeOutlined } from '@ant-design/icons-vue';

const route = useRoute();
const router = useRouter();
const articleId = route.params.id;

// 文章数据
const article = ref({});
// 评论列表
const comments = ref([]);
// 相关文章
const relatedArticles = ref([]);
// 评论内容
const commentContent = ref('');
// 评论提交中
const submitting = ref(false);

// 模拟数据 - 实际项目中应该从API获取
onMounted(() => {
  // 模拟文章数据
  article.value = {
    id: articleId,
    title: 'Spring Boot实战：构建RESTful API',
    content: `<p>Spring Boot是Java生态中最流行的框架之一，它极大地简化了Spring应用的初始搭建和开发过程。本文将详细介绍如何使用Spring Boot构建RESTful API。</p>

<h2>1. 项目初始化</h2>
<p>首先，我们需要创建一个Spring Boot项目。可以通过Spring Initializr网站或者使用Spring Boot CLI来创建。</p>
<pre><code>spring init --dependencies=web,jpa,mysql my-rest-api</code></pre>

<h2>2. 配置数据库连接</h2>
<p>在application.yml中配置数据库连接信息：</p>
<pre><code>spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true</code></pre>

<h2>3. 创建实体类</h2>
<p>创建JPA实体类：</p>
<pre><code>@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String email;
    private String password;
}</code></pre>

<h2>4. 创建Repository</h2>
<p>创建JPA Repository接口：</p>
<pre><code>public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}</code></pre>

<h2>5. 创建Service层</h2>
<p>创建Service层处理业务逻辑：</p>
<pre><code>@Service
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    public User createUser(User user) {
        return userRepository.save(user);
    }
}</code></pre>

<h2>6. 创建Controller</h2>
<p>创建RESTful API控制器：</p>
<pre><code>@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
    
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}</code></pre>

<h2>7. 异常处理</h2>
<p>创建全局异常处理器：</p>
<pre><code>@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}</code></pre>

<h2>8. 启动应用</h2>
<p>最后，启动Spring Boot应用：</p>
<pre><code>@SpringBootApplication
public class MyRestApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyRestApiApplication.class, args);
    }
}</code></pre>

<h2>总结</h2>
<p>通过Spring Boot，我们可以快速构建RESTful API，它提供了很多便利的功能，如自动配置、内嵌服务器、健康检查等。在实际项目中，我们还可以添加更多功能，如安全认证、缓存、文档生成等。</p>`,
    coverImage: 'https://picsum.photos/800/400?random=2',
    author: '李四',
    authorAvatar: 'https://randomuser.me/api/portraits/men/32.jpg',
    categoryId: 2,
    categoryName: '后端开发',
    viewCount: 856,
    likeCount: 124,
    commentCount: 18,
    createTime: '2023-05-12 14:20:00',
    tags: ['Spring Boot', 'Java', 'RESTful']
  };
  
  // 模拟评论数据
  comments.value = [
    {
      id: 1,
      content: '非常详细的教程，对我帮助很大！',
      userId: 101,
      username: '技术爱好者',
      userAvatar: 'https://randomuser.me/api/portraits/men/43.jpg',
      createTime: '2023-05-13 09:30:00',
      likeCount: 5
    },
    {
      id: 2,
      content: '请问如何处理跨域问题？',
      userId: 102,
      username: '初学者',
      userAvatar: 'https://randomuser.me/api/portraits/women/26.jpg',
      createTime: '2023-05-13 10:15:00',
      likeCount: 0,
      children: [
        {
          id: 3,
          content: '可以使用@CrossOrigin注解或者配置CorsFilter',
          userId: 103,
          username: '资深开发',
          userAvatar: 'https://randomuser.me/api/portraits/men/32.jpg',
          createTime: '2023-05-13 10:30:00',
          likeCount: 3,
          replyTo: '初学者'
        }
      ]
    }
  ];
  
  // 模拟相关文章
  relatedArticles.value = [
    { id: 5, title: 'Spring Security详解：保护你的API', viewCount: 723 },
    { id: 6, title: 'Spring Data JPA使用技巧', viewCount: 612 },
    { id: 7, title: 'Spring Boot单元测试最佳实践', viewCount: 498 }
  ];
});

// 提交评论
const submitComment = () => {
  if (!commentContent.value.trim()) {
    message.warning('评论内容不能为空');
    return;
  }
  
  submitting.value = true;
  
  // 模拟提交评论
  setTimeout(() => {
    // 实际项目中应该调用API提交评论
    const newComment = {
      id: Date.now(),
      content: commentContent.value,
      userId: 999, // 假设当前登录用户ID
      username: '当前用户',
      userAvatar: 'https://randomuser.me/api/portraits/men/85.jpg',
      createTime: new Date().toLocaleString(),
      likeCount: 0
    };
    
    comments.value.unshift(newComment);
    article.value.commentCount += 1;
    commentContent.value = '';
    submitting.value = false;
    message.success('评论发布成功');
  }, 1000);
};

// 点赞文章
const likeArticle = () => {
  // 实际项目中应该调用API进行点赞
  article.value.likeCount += 1;
  message.success('点赞成功');
};

// 跳转到文章详情页
const goToArticle = (id) => {
  router.push(`/article/${id}`);
};
</script>

<template>
  <div class="article-detail-container">
    <div class="content-wrapper">
      <!-- 文章内容 -->
      <Card class="article-card">
        <h1 class="article-title">{{ article.title }}</h1>
        
        <div class="article-meta">
          <div class="author-info">
            <Avatar :src="article.authorAvatar" :size="32" />
            <span class="author-name">{{ article.author }}</span>
          </div>
          <div class="article-info">
            <span><EyeOutlined /> {{ article.viewCount }}</span>
            <span><LikeOutlined /> {{ article.likeCount }}</span>
            <span><MessageOutlined /> {{ article.commentCount }}</span>
            <span>{{ $formatDate(article.createTime) }}</span>
          </div>
        </div>
        
        <div class="article-tags">
          <Tag v-for="tag in article.tags" :key="tag" color="blue">{{ tag }}</Tag>
          <Tag color="green">{{ article.categoryName }}</Tag>
        </div>
        
        <Divider />
        
        <div class="article-content" v-html="article.content"></div>
        
        <div class="article-actions">
          <Button type="primary" @click="likeArticle">
            <LikeOutlined /> 点赞 ({{ article.likeCount }})
          </Button>
        </div>
      </Card>
      
      <!-- 评论区 -->
      <Card class="comment-card">
        <template #title>
          <div class="comment-title">评论 ({{ article.commentCount }})</div>
        </template>
        
        <!-- 评论表单 -->
        <div class="comment-form">
          <Form>
            <Form.Item>
              <Input.TextArea 
                v-model:value="commentContent" 
                :rows="4" 
                placeholder="写下你的评论..."
              />
            </Form.Item>
            <Form.Item>
              <Button 
                type="primary" 
                :loading="submitting" 
                @click="submitComment"
              >
                发表评论
              </Button>
            </Form.Item>
          </Form>
        </div>
        
        <Divider />
        
        <!-- 评论列表 -->
        <div class="comment-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <Comment
              :author="comment.username"
              :avatar="comment.userAvatar"
              :content="comment.content"
              :datetime="$formatDate(comment.createTime)"
            >
              <template #actions>
                <span><LikeOutlined /> {{ comment.likeCount }}</span>
                <span>回复</span>
              </template>
              
              <!-- 嵌套评论 -->
              <template v-if="comment.children && comment.children.length > 0">
                <Comment
                  v-for="reply in comment.children"
                  :key="reply.id"
                  :author="reply.username"
                  :avatar="reply.userAvatar"
                  :content="reply.content"
                  :datetime="$formatDate(reply.createTime)"
                >
                  <template #actions>
                    <span><LikeOutlined /> {{ reply.likeCount }}</span>
                    <span>回复</span>
                  </template>
                </Comment>
              </template>
            </Comment>
          </div>
        </div>
      </Card>
    </div>
    
    <!-- 侧边栏 -->
    <div class="sidebar">
      <Card class="sidebar-card">
        <template #title>相关文章</template>
        <div class="related-articles">
          <div v-for="article in relatedArticles" :key="article.id" class="related-article-item" @click="goToArticle(article.id)">
            <span class="related-article-title">{{ article.title }}</span>
            <span class="related-article-views">{{ article.viewCount }}</span>
          </div>
        </div>
      </Card>
    </div>
  </div>
</template>

<style scoped>
.article-detail-container {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
  display: flex;
  gap: 24px;
}

.content-wrapper {
  flex: 1;
}

.sidebar {
  width: 300px;
}

.article-card {
  margin-bottom: 24px;
}

.article-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 16px;
  color: #333;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  }
</style>