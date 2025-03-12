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
('读书', '读书笔记', 60);

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
('攻略', '旅游攻略');

-- 添加文章数据
INSERT INTO `tb_article` (`title`, `content`, `summary`, `cover_image`, `author_id`, `category_id`, `view_count`, `like_count`, `comment_count`, `is_top`, `status`) VALUES
('Java开发入门指南', '<h1>Java开发入门指南</h1><p>Java是一种广泛使用的计算机编程语言，拥有跨平台、面向对象、泛型编程的特性，广泛应用于企业级Web应用开发和移动应用开发。</p><h2>1. 安装JDK</h2><p>首先需要安装JDK（Java Development Kit），可以从Oracle官网下载最新版本。</p><h2>2. 配置环境变量</h2><p>安装完成后，需要配置环境变量，包括JAVA_HOME、PATH等。</p><h2>3. 第一个Java程序</h2><p>创建一个Hello.java文件，内容如下：</p><pre><code>public class Hello {\n    public static void main(String[] args) {\n        System.out.println("Hello, World!");\n    }\n}</code></pre><p>然后编译运行：</p><pre><code>javac Hello.java\njava Hello</code></pre><p>恭喜你完成了第一个Java程序！</p>', 'Java是一种广泛使用的计算机编程语言，本文将介绍Java开发的基础知识和入门步骤。', 'https://picsum.photos/800/400?random=1', 1, 2, 1024, 128, 16, 1, 1),
('Python数据分析实战', '<h1>Python数据分析实战</h1><p>Python是数据分析领域最流行的编程语言之一，本文将介绍如何使用Python进行数据分析。</p><h2>1. 安装必要的库</h2><p>首先需要安装以下库：</p><pre><code>pip install numpy pandas matplotlib seaborn scikit-learn</code></pre><h2>2. 数据加载与预处理</h2><p>使用pandas加载数据：</p><pre><code>import pandas as pd\ndf = pd.read_csv("data.csv")\ndf.head()</code></pre><h2>3. 数据可视化</h2><p>使用matplotlib和seaborn进行数据可视化：</p><pre><code>import matplotlib.pyplot as plt\nimport seaborn as sns\n\nsns.pairplot(df)\nplt.show()</code></pre><h2>4. 构建模型</h2><p>使用scikit-learn构建机器学习模型：</p><pre><code>from sklearn.model_selection import train_test_split\nfrom sklearn.linear_model import LinearRegression\n\nX = df.drop("target", axis=1)\ny = df["target"]\nX_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)\n\nmodel = LinearRegression()\nmodel.fit(X_train, y_train)\nscore = model.score(X_test, y_test)\nprint(f"模型得分：{score}")</code></pre>', 'Python是数据分析领域最流行的编程语言之一，本文将介绍如何使用Python进行数据分析。', 'https://picsum.photos/800/400?random=2', 1, 2, 896, 112, 14, 1, 1),
('Vue.js前端开发实践', '<h1>Vue.js前端开发实践</h1><p>Vue.js是一套用于构建用户界面的渐进式JavaScript框架。本文将介绍Vue.js的基本用法和实践经验。</p><h2>1. 安装Vue.js</h2><p>可以通过CDN引入：</p><pre><code>&lt;script src="https://cdn.jsdelivr.net/npm/vue@2/dist/vue.js"&gt;&lt;/script&gt;</code></pre><p>或者通过npm安装：</p><pre><code>npm install vue</code></pre><h2>2. 创建Vue实例</h2><pre><code>new Vue({\n  el: "#app",\n  data: {\n    message: "Hello Vue!"\n  }\n})</code></pre><h2>3. 指令与组件</h2><p>Vue.js提供了丰富的指令，如v-if、v-for、v-bind等，还可以创建自定义组件：</p><pre><code>Vue.component("my-component", {\n  props: ["title"],\n  template: "&lt;div&gt;{{ title }}&lt;/div&gt;"\n})</code></pre><h2>4. 状态管理</h2><p>对于复杂应用，可以使用Vuex进行状态管理：</p><pre><code>const store = new Vuex.Store({\n  state: {\n    count: 0\n  },\n  mutations: {\n    increment(state) {\n      state.count++\n    }\n  }\n})</code></pre>', 'Vue.js是一套用于构建用户界面的渐进式JavaScript框架。本文将介绍Vue.js的基本用法和实践经验。', 'https://picsum.photos/800/400?random=3', 2, 2, 768, 96, 12, 0, 1),
('MySQL数据库优化技巧', '<h1>MySQL数据库优化技巧</h1><p>MySQL是最流行的关系型数据库之一，本文将分享一些MySQL数据库优化的实用技巧。</p><h2>1. 索引优化</h2><p>合理使用索引是提高查询性能的关键：</p><pre><code>-- 为常用查询字段创建索引\nCREATE INDEX idx_user_name ON users(name);\n\n-- 使用复合索引\nCREATE INDEX idx_user_name_email ON users(name, email);</code></pre><h2>2. 查询优化</h2><p>优化SQL查询语句：</p><pre><code>-- 避免使用SELECT *\nSELECT id, name, email FROM users WHERE status = 1;\n\n-- 使用EXPLAIN分析查询\nEXPLAIN SELECT * FROM users WHERE name = "John";</code></pre><h2>3. 配置优化</h2><p>调整MySQL配置参数：</p><pre><code>innodb_buffer_pool_size = 1G\ninnodb_log_file_size = 256M\nmax_connections = 500</code></pre><h2>4. 定期维护</h2><p>定期进行表优化和分析：</p><pre><code>OPTIMIZE TABLE users;\nANALYZE TABLE users;</code></pre>', 'MySQL是最流行的关系型数据库之一，本文将分享一些MySQL数据库优化的实用技巧。', 'https://picsum.photos/800/400?random=4', 2, 2, 640, 80, 10, 0, 1),
('云计算架构设计', '<h1>云计算架构设计</h1><p>云计算已经成为现代IT基础设施的重要组成部分，本文将介绍云计算架构设计的关键要素。</p><h2>1. 基础设施即服务（IaaS）</h2><p>IaaS提供虚拟化的计算资源，如虚拟机、存储和网络。常见的IaaS平台包括AWS EC2、Azure Virtual Machines和Google Compute Engine。</p><h2>2. 平台即服务（PaaS）</h2><p>PaaS提供应用开发和运行的平台，如AWS Elastic Beanstalk、Azure App Service和Google App Engine。</p><h2>3. 软件即服务（SaaS）</h2><p>SaaS直接提供应用软件服务，如Salesforce、Office 365和Google Workspace。</p><h2>4. 微服务架构</h2><p>在云环境中，微服务架构是一种流行的设计模式：</p><pre><code>// 使用Docker容器化微服务\ndocker build -t my-service .\ndocker run -p 8080:8080 my-service\n\n// 使用Kubernetes编排微服务\nkubectl apply -f deployment.yaml\nkubectl apply -f service.yaml</code></pre>', '云计算已经成为现代IT基础设施的重要组成部分，本文将介绍云计算架构设计的关键要素。', 'https://picsum.photos/800/400?random=5', 3, 2, 512, 64, 8, 0, 1),
('人工智能在医疗领域的应用', '<h1>人工智能在医疗领域的应用</h1><p>人工智能技术正在革新医疗行业，本文将探讨AI在医疗领域的创新应用。</p><h2>1. 医学影像分析</h2><p>AI可以辅助医生分析X光片、CT和MRI等医学影像，提高诊断准确率。</p><h2>2. 药物研发</h2><p>AI可以加速药物发现和开发过程，预测药物的效果和副作用。</p><h2>3. 个性化治疗</h2><p>基于患者的基因组数据和病史，AI可以推荐个性化的治疗方案。</p><h2>4. 医疗机器人</h2><p>AI驱动的机器人可以辅助外科手术，提高手术精度和安全性。</p><h2>5. 健康监测</h2><p>AI可以分析可穿戴设备收集的数据，监测用户的健康状况并提供建议。</p>', '人工智能技术正在革新医疗行业，本文将探讨AI在医疗领域的创新应用。', 'https://picsum.photos/800/400?random=6', 3, 2, 384, 48, 6, 0, 1),
('我的西藏行记', '<h1>我的西藏行记</h1><p>上个月，我终于实现了去西藏的梦想，这里记录下这段难忘的旅程。</p><h2>1. 拉萨</h2><p>拉萨是西藏的首府，这里有著名的布达拉宫、大昭寺和八廓街。布达拉宫的宏伟壮观令人叹为观止，大昭寺的虔诚氛围让人心生敬意，八廓街的热闹与藏族文化让人流连忘返。</p><h2>2. 纳木错</h2><p>纳木错是西藏三大圣湖之一，湖水清澈湛蓝，周围是连绵的雪山，美不胜收。在这里看日出日落，仿佛置身天堂。</p><h2>3. 林芝</h2><p>林芝被称为西藏的江南，这里有雪山、峡谷、森林和湖泊，自然风光十分优美。尤其是春天，桃花盛开的季节，美不胜收。</p>', '上个月，我终于实现了去西藏的梦想，这里记录下这段难忘的旅程。', 'https://picsum.photos/800/400?random=7', 4, 3, 256, 32, 4, 0, 1),
('美食探店：成都火锅', '<h1>美食探店：成都火锅</h1><p>作为一个美食爱好者，我最近探访了成都几家有名的火锅店，分享给大家。</p><h2>1. 海底捞</h2><p>服务确实一流，锅底味道浓郁，食材新鲜，尤其是他们的手打虾滑和鲜切毛肚，非常推荐。</p><h2>2. 大龙燚</h2><p>成都本地人喜欢的火锅店，麻辣味十足，锅底用料讲究，牛油的香气和辣椒的麻辣完美融合。</p><h2>3. 小龙坎</h2><p>连锁品牌中的良心店，价格相对亲民，味道也不错，尤其是他们的鸭肠和黄喉，处理得非常干净。</p>', '作为一个美食爱好者，我最近探访了成都几家有名的火锅店，分享给大家。', 'https://picsum.photos/800/400?random=8', 4, 4, 192, 24, 3, 0, 1),
('读《百年孤独》有感', '<h1>读《百年孤独》有感</h1><p>最近读完了加西亚·马尔克斯的《百年孤独》，这本魔幻现实主义的经典之作给我留下了深刻的印象。</p><h2>1. 布恩迪亚家族的命运</h2><p>小说通过布恩迪亚家族七代人的兴衰，展现了拉丁美洲的历史变迁和人类的命运轮回。家族成员的名字不断重复，性格和命运也似乎注定要重复，这种循环感让人感到一种宿命的悲剧。</p><h2>2. 魔幻与现实</h2><p>小说中充满了魔幻色彩，如升天的丽贝卡、预言的羊皮纸、不死的梅尔基亚德斯等，但这些魔幻元素又与现实紧密结合，反映了拉丁美洲的历史和文化。</p><h2>3. 孤独的主题</h2><p>正如书名所示，孤独是贯穿全书的主题。每个角色都以自己的方式经历着孤独，无论是何塞·阿卡迪奥的固执、乌尔苏拉的坚韧，还是奥雷里亚诺的叛逆，都无法逃脱孤独的命运。</p>', '最近读完了加西亚·马尔克斯的《百年孤独》，这本魔幻现实主义的经典之作给我留下了深刻的印象。', 'https://picsum.photos/800/400?random=9', 5, 5, 128, 16, 2, 0, 1),
('人工智能伦理思考', '<h1>人工智能伦理思考</h1><p>随着人工智能技术的快速发展，AI伦理问题变得越来越重要。本文将探讨AI发展中的几个关键伦理问题。</p><h2>1. 隐私与监控</h2><p>AI技术使大规模数据收集和分析成为可能，这引发了严重的隐私担忧。面部识别技术的广泛应用尤其引起争议，如何平衡安全需求和隐私保护是一个难题。</p><h2>2. 算法偏见</h2><p>AI系统可能继承或放大训练数据中的偏见，导致对某些群体的歧视。例如，招聘算法可能对女性或少数族裔产生不公平的结果。</p><h2>3. 自主武器系统</h2><p>AI驱动的自主武器系统引发了关于战争伦理的深刻问题。让机器决定生死是否道德？谁为AI武器的行为负责？</p><h2>4. 工作自动化</h2><p>AI和自动化可能导致大规模失业，社会需要思考如何应对这一挑战，包括再培训、通用基本收入等解决方案。</p>', '随着人工智能技术的快速发展，AI伦理问题变得越来越重要。本文将探讨AI发展中的几个关键伦理问题。', 'https://picsum.photos/800/400?random=10', 1, 2, 96, 12, 1, 0, 1);

-- 添加文章标签关联数据
INSERT INTO `tb_article_tag` (`article_id`, `tag_id`) VALUES
(1, 1), -- Java开发入门指南 - Java
(1, 4), -- Java开发入门指南 - 后端
(2, 2), -- Python数据分析实战 - Python
(2, 7), -- Python数据分析实战 - 人工智能
(3, 3), -- Vue.js前端开发实践 - 前端
(4, 5), -- MySQL数据库优化技巧 - 数据库
(4, 4), -- MySQL数据库优化技巧 - 后端
(5, 6), -- 云计算架构设计 - 云计算
(5, 4), -- 云计算架构设计 - 后端
(6, 7), -- 人工智能在医疗领域的应用 - 人工智能
(7, 10), -- 我的西藏行记 - 攻略
(7, 8), -- 我的西藏行记 - 随笔
(8, 8), -- 美食探店：成都火锅 - 随笔
(9, 8), -- 读《百年孤独》有感 - 随笔
(9, 9), -- 读《百年孤独》有感 - 心情
(10, 7); -- 人工智能伦理思考 - 人工智能

-- 添加评论数据
INSERT INTO `tb_comment` (`content`, `article_id`, `user_id`, `parent_id`, `reply_user_id`, `level`, `like_count`, `status`) VALUES
-- Java开发入门指南的评论
('这篇文章对初学者非常有帮助，谢谢分享！', 1, 2, 0, 0, 1, 8, 1),
('我按照教程操作，成功运行了第一个Java程序，感谢作者！', 1, 3, 0, 0, 1, 6, 1),
('请问有没有更多关于Java面向对象编程的教程推荐？', 1, 4, 0, 0, 1, 4, 1),
('推荐《Thinking in Java》这本书，非常适合深入学习面向对象编程。', 1, 1, 3, 4, 2, 5, 1),
('文章中的环境变量配置部分可以再详细一些。', 1, 5, 0, 0, 1, 2, 1),

-- Python数据分析实战的评论
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
('我在医院工作，确实看到了AI技术带来的变化，期待更多应用。', 6, 5, 0, 0, 1, 2, 1),

-- 我的西藏行记的评论
('照片拍得真美，让人也想去西藏看看。', 7, 2, 0, 0, 1, 2, 1),
('请问去西藏需要注意高原反应吗？有什么建议？', 7, 3, 0, 0, 1, 1, 1),

-- 美食探店：成都火锅的评论
('看得我口水直流，下次去成都一定要去尝尝。', 8, 5, 0, 0, 1, 2, 1),

-- 读《百年孤独》有感的评论
('这本书我也很喜欢，作者对孤独的描写很有深度。', 9, 4, 0, 0, 1, 1, 1),

-- 人工智能伦理思考的评论
('算法偏见这个问题确实值得重视，需要更多的监管和技术解决方案。', 10, 2, 0, 0, 1, 1, 1);