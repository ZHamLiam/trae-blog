-- 角色表
CREATE TABLE IF NOT EXISTS `tb_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `sort` int(11) DEFAULT '0' COMMENT '排序值',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS `tb_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` varchar(50) NOT NULL COMMENT '权限名称',
  `code` varchar(50) NOT NULL COMMENT '权限编码',
  `type` tinyint(1) NOT NULL COMMENT '权限类型：1-菜单，2-按钮，3-接口',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父权限ID，如果是顶级权限则为0',
  `path` varchar(255) DEFAULT NULL COMMENT '权限路径（前端路由路径或后端接口路径）',
  `component` varchar(255) DEFAULT NULL COMMENT '前端组件路径',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `sort` int(11) DEFAULT '0' COMMENT '排序值',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `tb_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `tb_role_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `permission_id` bigint(20) NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 初始数据插入
-- 角色数据
INSERT INTO `tb_role` (`name`, `code`, `description`, `sort`) VALUES
('超级管理员', 'admin', '系统超级管理员，拥有所有权限', 1),
('普通用户', 'user', '普通用户，拥有基本权限', 2),
('内容管理员', 'content_manager', '内容管理员，负责管理文章、分类、标签等内容', 3);

-- 权限数据
-- 菜单权限
INSERT INTO `tb_permission` (`name`, `code`, `type`, `parent_id`, `path`, `component`, `icon`, `sort`) VALUES
('系统管理', 'system', 1, 0, '/system', 'Layout', 'el-icon-setting', 1),
('用户管理', 'user', 1, 1, '/system/user', 'system/user/index', 'el-icon-user', 1),
('角色管理', 'role', 1, 1, '/system/role', 'system/role/index', 'el-icon-s-custom', 2),
('权限管理', 'permission', 1, 1, '/system/permission', 'system/permission/index', 'el-icon-key', 3),
('内容管理', 'content', 1, 0, '/content', 'Layout', 'el-icon-document', 2),
('文章管理', 'article', 1, 5, '/content/article', 'content/article/index', 'el-icon-document-copy', 1),
('分类管理', 'category', 1, 5, '/content/category', 'content/category/index', 'el-icon-folder', 2),
('标签管理', 'tag', 1, 5, '/content/tag', 'content/tag/index', 'el-icon-collection-tag', 3),
('评论管理', 'comment', 1, 5, '/content/comment', 'content/comment/index', 'el-icon-chat-line-square', 4),
('仪表盘', 'dashboard', 1, 0, '/dashboard', 'dashboard/index', 'el-icon-s-data', 0);

-- 按钮权限
INSERT INTO `tb_permission` (`name`, `code`, `type`, `parent_id`, `path`, `sort`) VALUES
-- 用户管理按钮
('用户查看', 'user:view', 2, 2, NULL, 1),
('用户新增', 'user:create', 2, 2, NULL, 2),
('用户编辑', 'user:update', 2, 2, NULL, 3),
('用户删除', 'user:delete', 2, 2, NULL, 4),
('用户分配角色', 'user:assign', 2, 2, NULL, 5),
-- 角色管理按钮
('角色查看', 'role:view', 2, 3, NULL, 1),
('角色新增', 'role:create', 2, 3, NULL, 2),
('角色编辑', 'role:update', 2, 3, NULL, 3),
('角色删除', 'role:delete', 2, 3, NULL, 4),
('角色分配权限', 'role:assign', 2, 3, NULL, 5),
-- 权限管理按钮
('权限查看', 'permission:view', 2, 4, NULL, 1),
('权限新增', 'permission:create', 2, 4, NULL, 2),
('权限编辑', 'permission:update', 2, 4, NULL, 3),
('权限删除', 'permission:delete', 2, 4, NULL, 4),
-- 文章管理按钮
('文章查看', 'article:view', 2, 6, NULL, 1),
('文章新增', 'article:create', 2, 6, NULL, 2),
('文章编辑', 'article:update', 2, 6, NULL, 3),
('文章删除', 'article:delete', 2, 6, NULL, 4),
-- 分类管理按钮
('分类查看', 'category:view', 2, 7, NULL, 1),
('分类新增', 'category:create', 2, 7, NULL, 2),
('分类编辑', 'category:update', 2, 7, NULL, 3),
('分类删除', 'category:delete', 2, 7, NULL, 4),
-- 标签管理按钮
('标签查看', 'tag:view', 2, 8, NULL, 1),
('标签新增', 'tag:create', 2, 8, NULL, 2),
('标签编辑', 'tag:update', 2, 8, NULL, 3),
('标签删除', 'tag:delete', 2, 8, NULL, 4),
-- 评论管理按钮
('评论查看', 'comment:view', 2, 9, NULL, 1),
('评论回复', 'comment:reply', 2, 9, NULL, 2),
('评论删除', 'comment:delete', 2, 9, NULL, 3);

-- 为超级管理员分配所有权限
INSERT INTO `tb_role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `tb_permission`;

-- 为普通用户分配基本权限
INSERT INTO `tb_role_permission` (`role_id`, `permission_id`)
SELECT 2, id FROM `tb_permission` WHERE `code` IN ('dashboard', 'article:view', 'category:view', 'tag:view', 'comment:view', 'comment:reply');

-- 为内容管理员分配内容管理权限
INSERT INTO `tb_role_permission` (`role_id`, `permission_id`)
SELECT 3, id FROM `tb_permission` WHERE `code` LIKE 'content%' OR `code` LIKE 'article%' OR `code` LIKE 'category%' OR `code` LIKE 'tag%' OR `code` LIKE 'comment%' OR `code` = 'dashboard';