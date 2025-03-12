-- 清空现有数据（保留admin用户和默认分类）
DELETE FROM `tb_comment` WHERE 1=1;
DELETE FROM `tb_article_tag` WHERE 1=1;
DELETE FROM `tb_article` WHERE 1=1;
DELETE FROM `tb_tag` WHERE 1=1;
DELETE FROM `tb_category` WHERE `name` != '默认分类';
DELETE FROM `tb_user` WHERE `username` != 'admin';

-- 添加用户数据
INSERT INTO `tb_user` (`username`, `password`, `nickname`, `avatar`, `email`, `role`, `status`) VALUES
('user1', '$2a$10$Y9uV9YjFuNlATDGz5MKFe.6GhRJV1Gx7RQhU8tHAQYIRNz3UDIvuy', '张三', 'https://randomuser.me/api/portraits/men/1.jpg', 'user1@example.com', 'user', 1),
('user2', '$2a$10$Y9uV9YjFuNlATDGz5MKFe.6GhRJV1Gx7RQhU8tHAQYIRNz3UDIvuy', '李四', 'https://randomuser.me/api/portraits/women/2.jpg', 'user2@example.com', 'user', 1),
('user3', '$2a$10$Y9uV9YjFuNlATDGz5MKFe.6GhRJV1Gx7RQhU8tHAQYIRNz3UDIvuy', '王五', 'https://randomuser.me/api/portraits/men/3.jpg', 'user3@example.com', 'user', 1),
('user4', '$2a$10$Y9uV9YjFuNlATDGz5MKFe.6GhRJV1Gx7RQhU8tHAQYIRNz3UDIvuy', '赵六', 'https://randomuser.me/api/portraits/women/4.jpg', 'user4@example.com', 'user', 1),
('user5', '$2a$10$Y9uV9YjFuNlATDGz5MKFe.6GhRJV1Gx7RQhU8tHAQYIRNz3UDIvuy', '钱七', 'https://randomuser.me/api/portraits/men/5.jpg', 'user5@example.com', 'user', 0);

-- 添加分类数据
INSERT INTO `tb_category` (`name`, `description`, `sort`) VALUES
('技术', '技术相关文章', 100),
('生活', '生活随笔', 90),
('旅游', '旅游见闻', 80),
('美食', '美食推荐', 70),
('读书', '读书笔记', 60),
('健康', '健康养生', 50),
('职场', '职场经验', 40);

-- 添加标签数据
INSERT INTO `tb_tag` (`name`, `description`) VALUES
('Java', 'Java编程语言'),
('Python', 'Python编程语言'),
('前端', '前端开发技术'),
('后端', '后端开发技术'),
('数据库', '数据库相关技术'),
('云计算', '云计算相关技术'),
('人工智能', 'AI相关技术'),
('随笔', '生活随笔'),
('心情', '心情记录'),
('攻略', '旅游攻略'),
('健身', '健身相关'),
('美食', '美食相关'),
('阅读', '阅读相关'),
('职场', '职场相关'),
('Vue', 'Vue.js框架'),
('React', 'React框架'),
('Spring', 'Spring框架'),
('Docker', 'Docker容器技术');

-- 添加文章数据
INSERT INTO `tb_article` (`title`, `content`, `summary`, `cover_image`, `author_id`, `category_id`, `view_count`, `like_count`, `comment_count`, `is_top`, `status`) VALUES
-- 技术类文章
('Java开发入门指南', '# Java开发入门指南

Java是一种广泛使用的计算机编程语言，拥有跨平台、面向对象、泛型编程的特性，广泛应用于企业级Web应用开发和移动应用开发。

## 1. 安装JDK

首先需要安装JDK（Java Development Kit），可以从Oracle官网下载最新版本。

## 2. 配置环境变量

安装完成后，需要配置环境变量，包括JAVA_HOME、PATH等。

## 3. 第一个Java程序

创建一个Hello.java文件，内容如下：

```java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

然后编译运行：

```bash
javac Hello.java
java Hello
```

恭喜你完成了第一个Java程序！', 'Java是一种广泛使用的计算机编程语言，本文将介绍Java开发的基础知识和入门步骤。', 'https://picsum.photos/800/400?random=1', 1, 1, 1024, 128, 16, 1, 1),

('Spring Boot实战教程', '# Spring Boot实战教程

Spring Boot是由Pivotal团队提供的全新框架，旨在简化Spring应用的初始搭建和开发过程。

## 1. 创建项目

使用Spring Initializr创建一个新项目：

```
https://start.spring.io/
```

## 2. 项目结构

Spring Boot项目结构清晰：

```
src/main/java - Java源代码
src/main/resources - 配置文件
src/test - 测试代码
```

## 3. 创建RESTful API

```java
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }
}
```

## 4. 运行应用

直接运行主类或使用Maven：

```bash
mvn spring-boot:run
```', 'Spring Boot是简化Spring应用开发的框架，本文将介绍如何使用Spring Boot快速开发Web应用。', 'https://picsum.photos/800/400?random=2', 1, 1, 896, 112, 14, 1, 1),

('Vue.js 3.0核心特性详解', '# Vue.js 3.0核心特性详解

Vue.js 3.0带来了许多令人兴奋的新特性，本文将详细介绍这些变化。

## 1. Composition API

Composition API是Vue 3最大的特性之一，它提供了一种更灵活的方式来组织组件逻辑：

```javascript
import { ref, computed, onMounted } from "vue";

export default {
  setup() {
    const count = ref(0);
    const doubleCount = computed(() => count.value * 2);
    
    function increment() {
      count.value++;
    }
    
    onMounted(() => {
      console.log("Component mounted!");
    });
    
    return {
      count,
      doubleCount,
      increment
    };
  }
}
```

## 2. Teleport组件

Teleport允许我们将组件的一部分模板"传送"到DOM的其他位置：

```html
<teleport to="body">
  <div class="modal">
    <!-- 模态框内容 -->
  </div>
</teleport>
```

## 3. 片段（Fragments）

Vue 3组件可以有多个根节点：

```html
<template>
  <header>Header</header>
  <main>Content</main>
  <footer>Footer</footer>
</template>
```', 'Vue.js 3.0带来了许多令人兴奋的新特性，本文将详细介绍Composition API、Teleport和Fragments等核心变化。', 'https://picsum.photos/800/400?random=3', 2, 1, 768, 96, 12, 0, 1),

('React Hooks完全指南', '# React Hooks完全指南

React Hooks是React 16.8引入的新特性，它可以让你在不编写class的情况下使用state和其他React特性。

## 1. useState Hook

useState让函数组件也能有状态：

```javascript
import React, { useState } from "react";

function Counter() {
  const [count, setCount] = useState(0);
  
  return (
    <div>
      <p>You clicked {count} times</p>
      <button onClick={() => setCount(count + 1)}>
        Click me
      </button>
    </div>
  );
}
```

## 2. useEffect Hook

useEffect让你在函数组件中执行副作用操作：

```javascript
import React, { useState, useEffect } from "react";

function Example() {
  const [count, setCount] = useState(0);

  useEffect(() => {
    document.title = `You clicked ${count} times`;
  });

  return (
    <div>
      <p>You clicked {count} times</p>
      <button onClick={() => setCount(count + 1)}>
        Click me
      </button>
    </div>
  );
}
```', 'React Hooks是React 16.8引入的新特性，本文将详细介绍useState、useEffect等Hook的使用方法和最佳实践。', 'https://picsum.photos/800/400?random=4', 2, 1, 640, 80, 10, 0, 1),

('MySQL高级查询优化', '# MySQL高级查询优化

MySQL是最流行的关系型数据库之一，本文将分享一些高级查询优化技巧。

## 1. 索引优化

合理使用索引是提高查询性能的关键：

```sql
-- 为常用查询字段创建索引
CREATE INDEX idx_user_name ON users(name);

-- 使用复合索引
CREATE INDEX idx_user_name_email ON users(name, email);
```

## 2. EXPLAIN分析查询

使用EXPLAIN命令分析查询执行计划：

```sql
EXPLAIN SELECT * FROM users WHERE name = "John";
```

关注type列（system > const > eq_ref > ref > range > index > ALL），越靠前的执行效率越高。

## 3. 查询重写

优化SQL查询语句：

```sql
-- 避免使用SELECT *
SELECT id, name, email FROM users WHERE status = 1;

-- 使用LIMIT限制结果集大小
SELECT id, name FROM users ORDER BY created_at DESC LIMIT 10;
```', 'MySQL是最流行的关系型数据库之一，本文将分享索引优化、EXPLAIN分析和查询重写等高级查询优化技巧。', 'https://picsum.photos/800/400?random=5', 3, 1, 512, 64, 8, 0, 1),

('Docker容器化应用实战', '# Docker容器化应用实战

Docker是一个开源的应用容器引擎，让开发者可以打包他们的应用以及依赖包到一个可移植的容器中。

## 1. Docker基础概念

Docker的三个基本概念：

- 镜像（Image）：Docker容器的模板
- 容器（Container）：镜像的运行实例
- 仓库（Repository）：存储镜像的地方

## 2. 创建Dockerfile

Dockerfile是用来构建Docker镜像的文本文件：

```dockerfile
FROM node:14
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 3000
CMD ["npm", "start"]
```

## 3. 构建和运行容器

```bash
# 构建镜像
docker build -t my-app .

# 运行容器
docker run -p 3000:3000 my-app
```

## 4. Docker Compose

使用Docker Compose管理多容器应用：

```yaml
version: "3"
services:
  web:
    build: .
    ports:
      - "3000:3000"
  db:
    image: mongo
    volumes:
      - mongo-data:/data/db
volumes:
  mongo-data:
```', 'Docker是一个开源的应用容器引擎，本文将介绍Docker的基本概念和容器化应用的实战经验。', 'https://picsum.photos/800/400?random=6', 3, 1, 384, 48, 6, 0, 1),

-- 生活类文章
('我的西藏行记', '# 我的西藏行记

上个月，我终于实现了去西藏的梦想，这里记录下这段难忘的旅程。

## 1. 拉萨

拉萨是西藏的首府，这里有著名的布达拉宫、大昭寺和八廓街。布达拉宫的宏伟壮观令人叹为观止，大昭寺的虔诚氛围让人心生敬意，八廓街的热闹与藏族文化让人流连忘返。

## 2. 纳木错

纳木错是西藏三大圣湖之一，湖水清澈湛蓝，周围是连绵的雪山，美不胜收。在这里看日出日落，仿佛置身天堂。

## 3. 林芝

林芝被称为西藏的江南，这里有雪山、峡谷、森林和湖泊，自然风光十分优美。尤其是春天，桃花盛开的季节，美不胜收。', '上个月，我终于实现了去西藏的梦想，这里记录下这段难忘的旅程。', 'https://picsum.photos/800/400?random=7', 4, 3, 256, 32, 4, 0, 1),

('美食探店：成都火锅', '# 美食探店：成都火锅

作为一个美食爱好者，我最近探访了成都几家有名的火锅店，分享给大家。

## 1. 海底捞

服务确实一流，锅底味道浓郁，食材新鲜，尤其是他们的手打虾滑和鲜切毛肚，非常推荐。

## 2. 大龙燚

成都本地人喜欢的火锅店，麻辣味十足，锅底用料讲究，牛油的香气和辣椒的麻辣完美融合。

## 3. 小龙坎

连锁品牌中的良心店，价格相对亲民，味道也不错，尤其是他们的鸭肠和黄喉，处理得非常干净。', '作为一个美食爱好者，我最近探访了成都几家有名的火锅店，分享给大家。', 'https://picsum.photos/800/400?random=8', 4, 4, 192, 24, 3, 0, 1),

('读《百年孤独》有感', '# 读《百年孤独》有感

最近读完了加西亚·马尔克斯的《百年孤独》，这本魔幻现实主义的经典之作给我留下了深刻的印象。

## 1. 布恩迪亚家族的命运

小说通过布恩迪亚家族七代人的兴衰，展现了拉丁美洲的历史变迁和人类的命运轮回。家族成员的名字不断重复，性格和命运也似乎注定要重复，这种循环感让人感到一种宿命的悲剧。

## 2. 魔幻与现实

小说中充满了魔幻色彩，如升天的丽贝卡、预言的羊皮纸、不死的梅尔基亚德斯等，但这些魔幻元素又与现实紧密结合，反映了拉丁美洲的历史和文化。

## 3. 孤独的主题

正如书名所示，孤独是贯穿全书的主题。每个角色都以自己的方式经历着孤独，无论是何塞·阿卡迪奥的固执、乌尔苏拉的坚韧，还是奥雷里亚诺的叛逆，都无法逃脱孤独的命运。', '最近读完了加西亚·马尔克斯的《百年孤独》，这本魔幻现实主义的经典之作给我留下了深刻的印象。', 'https://picsum.photos/800/400?random=9', 5, 5, 128, 16, 2, 0, 1),

('人工智能伦理思考', '# 人工智能伦理思考

随着人工智能技术的快速发展，AI伦理问题变得越来越重要。本文将探讨AI发展中的几个关键伦理问题。

## 1. 隐私与监控

AI技术使大规模数据收集和分析成为可能，这引发了严重的隐私担忧。面部识别技术的广泛应用尤其引起争议，如何平衡安全需求和隐私保护是一个难题。

## 2. 算法偏见

AI系统可能继承或放大训练数据中的偏见，导致对某些群体的歧视。例如，招聘算法可能对女性或少数族裔产生不公平的结果。

## 3. 自主武器系统

AI驱动的自主武器系统引发了关于战争伦理的深刻问题。让机器决定生死是否道德？谁为AI武器的行为负责？

## 4. 工作自动化

AI和自动化可能导致大规模失业，社会需要思考如何应对这一挑战，包括再培训、通用基本收入等解决方案。', '随着人工智能技术的快速发展，AI伦理问题变得越来越重要。本文将探讨AI发展中的几个关键伦理问题。', 'https://picsum.photos/800/400?random=10', 1, 2, 96, 12, 1, 0, 1);

-- 添加文章标签关联数据
INSERT INTO `tb_article_tag` (`article_id`, `tag_id`) VALUES
(1, 1), -- Java开发入门指南 - Java
(1, 4), -- Java开发入门指南 - 后端
(2, 17), -- Spring Boot实战教程 - Spring
(2, 4), -- Spring Boot实战教程 - 后端
(3, 15), -- Vue.js 3.0核心特性详解 - Vue
(3, 3), -- Vue.js 3.0核心特性详解 - 前端
(4, 16), -- React Hooks完全指南 - React
(4, 3), -- React Hooks完全指南 - 前端
(5, 5), -- MySQL高级查询优化 - 数据库
(5, 4), -- MySQL高级查询优化 - 后端
(6, 18), -- Docker容器化应用实战 - Docker
(6, 4), -- Docker容器化应用实战 - 后端
(7, 10), -- 我的西藏行记 - 攻略
(7, 8), -- 我的西藏行记 - 随笔
(8, 12), -- 美食探店：成都火锅 - 美食
(8, 8), -- 美食探店：成都火锅 - 随笔
(9, 13), -- 读《百年孤独》有感 - 阅读
(9, 8), -- 读《百年孤独》有感 - 随笔
(10, 7), -- 人工智能伦理思考 - 人工智能
(10, 14); -- 人工智能伦理思考 - 职场

-- 添加评论数据
INSERT INTO `tb_comment` (`content`, `article_id`, `user_id`, `parent_id`, `reply_user_id`, `level`, `like_count`, `status`) VALUES
-- Java开发入门指南的评论
('这篇文章对初学者非常有帮助，谢谢分享！', 1, 2, 0, 0, 1, 8, 1),
('我按照教程操作，成功运行了第一个Java程序，感谢作者！', 1, 3, 0, 0, 1, 6, 1),
('请问有没有更多关于Java面向对象编程的教程推荐？', 1, 4, 0, 0, 1, 4, 1),
('推荐《Thinking in Java》这本书，非常适合深入学习面向对象编程。', 1, 1, 3, 4, 2, 5, 1),
('文章中的环境变量配置部分可以再详细一些。', 1, 5, 0, 0, 1, 2, 1),

-- Spring Boot实战教程的评论
('Python在数据分析领域确实很强大，我用它完成了几个数据可视化项目。', 2, 3, 0, 0, 1, 7, 1),
('scikit-learn的使用示例非常清晰，对我的项目有很大帮助。', 2, 4, 0, 0, 1, 5, 1),
('请问除了文中提到的库，还有哪些Python数据分析库值得学习？', 2, 2, 0, 0, 1, 3, 1),
('建议了解一下PySpark，适合处理大规模数据集。', 2, 1, 8, 2, 2, 4, 1),
('TensorFlow和PyTorch也很重要，特别是如果你想深入机器学习领域。', 2, 5, 8, 2, 2, 3, 1),

-- Vue.js前端开发实践的评论
('Vue.js确实很容易上手，我用它重构了公司的前端项目。', 3, 2, 0, 0, 1, 6, 1),
('Vuex的状态管理确实很方便，但对小项目来说可能有点过度设计。', 3, 4, 0, 0, 1, 4, 1),
('请问Vue3和Vue2有哪些主要区别？', 3, 3, 0, 0, 1, 2, 1),

-- MySQL数据库优化技巧的评论
('索引优化的部分对我帮助很大，数据库查询速度提升了不少。', 4, 3, 0, 0, 1, 5, 1),
('配置优化这部分很实用，特别是对高并发系统。', 4, 5, 0, 0, 1, 3, 1),

-- 云计算架构设计的评论
('微服务架构这部分讲得很好，正好我们团队在做相关迁移。', 5, 2, 0, 0, 1, 4, 1),
('Docker和Kubernetes的示例很实用，但对初学者可能有点难度。', 5, 4, 0, 0, 1, 2, 1),

-- 人工智能在医疗领域的应用的评论
('AI在医疗影像分析上的应用前景确实很广阔。', 6, 3, 0, 0, 1, 3, 1),
('希望未来AI能够帮助降低医疗成本，让更多人获得优质医疗服务。', 6, 5, 0, 0, 1, 2, 1),

-- 我的西藏行记的评论
('文章写得很生动，仿佛身临其境，也想去西藏旅行了。', 7, 2, 0, 0, 1, 3, 1),
('请问去西藏需要注意高原反应吗？有什么建议？', 7, 3, 0, 0, 1, 2, 1),
('建议提前服用红景天，多喝水，不要剧烈运动，适应几天再去高海拔地区。', 7, 4, 2, 3, 2, 1, 1),

-- 美食探店：成都火锅的评论
('看完文章立刻订了大龙燚的位置，周末去尝尝。', 8, 3, 0, 0, 1, 2, 1),
('成都还有很多好吃的，期待博主更多美食推荐。', 8, 5, 0, 0, 1, 1, 1),

-- 读《百年孤独》有感的评论
('这本书我读了三遍，每次都有不同的感受，确实是经典之作。', 9, 4, 0, 0, 1, 1, 1),

-- 人工智能伦理思考的评论
('AI伦理确实是个值得深思的话题，尤其是算法偏见问题。', 10, 2, 0, 0, 1, 1, 1);
