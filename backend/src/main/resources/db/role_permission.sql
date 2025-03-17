-- 角色和权限管理相关表结构

-- 创建角色表
CREATE TABLE IF NOT EXISTS `tb_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序值，值越大排序越靠前',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 创建权限表
CREATE TABLE IF NOT EXISTS `tb_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` varchar(50) NOT NULL COMMENT '权限名称',
  `code` varchar(50) NOT NULL COMMENT '权限编码',
  `type` tinyint(4) NOT NULL COMMENT '权限类型：1-菜单，2-按钮，3-接口',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父权限ID，如果是顶级权限则为0',
  `path` varchar(200) DEFAULT NULL COMMENT '权限路径（前端路由路径或后端接口路径）',
  `component` varchar(200) DEFAULT NULL COMMENT '前端组件路径',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序值，值越大排序越靠前',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 创建角色权限关联表
CREATE TABLE IF NOT EXISTS `tb_role_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `permission_id` bigint(20) NOT NULL COMMENT '权限ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_role_permission` (`role_id`,`permission_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS `tb_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_role` (`user_id`,`role_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 修改用户表，删除role字段（将在用户角色关联表中维护）
-- 注意：执行前请确保已将现有用户的角色信息迁移到用户角色关联表中
-- ALTER TABLE `tb_user` DROP COLUMN `role`;

-- 初始化角色数据
INSERT INTO `tb_role` (`name`, `code`, `description`, `sort`, `status`) VALUES
('超级管理员', 'super_admin', '系统超级管理员，拥有所有权限', 100, 1),
('管理员', 'admin', '系统管理员，拥有大部分管理权限', 90, 1),
('内容编辑', 'editor', '内容编辑，负责内容管理', 80, 1),
('普通用户', 'user', '普通用户，基本功能访问权限', 70, 1);

-- 初始化权限数据（菜单权限）
INSERT INTO `tb_permission` (`name`, `code`, `type`, `parent_id`, `path`, `component`, `icon`, `sort`, `status`) VALUES
-- 顶级菜单
('控制台', 'dashboard', 1, 0, '/dashboard', 'dashboard/index', 'dashboard', 100, 1),
('内容管理', 'content', 1, 0, '/content', 'Layout', 'document', 90, 1),
('用户管理', 'user', 1, 0, '/user', 'Layout', 'user', 80, 1),
('系统管理', 'system', 1, 0, '/system', 'Layout', 'setting', 70, 1),

-- 内容管理子菜单
('文章管理', 'article', 1, 2, '/content/article', 'content/article/index', 'article', 95, 1),
('分类管理', 'category', 1, 2, '/content/category', 'content/category/index', 'category', 94, 1),
('标签管理', 'tag', 1, 2, '/content/tag', 'content/tag/index', 'tag', 93, 1),
('评论管理', 'comment', 1, 2, '/content/comment', 'content/comment/index', 'comment', 92, 1),

-- 用户管理子菜单
('用户列表', 'user_list', 1, 3, '/user/list', 'user/list/index', 'peoples', 85, 1),

-- 系统管理子菜单
('角色管理', 'role', 1, 4, '/system/role', 'system/role/index', 'role', 75, 1),
('权限管理', 'permission', 1, 4, '/system/permission', 'system/permission/index', 'permission', 74, 1);

-- 初始化权限数据（按钮权限）
INSERT INTO `tb_permission` (`name`, `code`, `type`, `parent_id`, `path`, `sort`, `status`) VALUES
-- 文章管理按钮
('文章查看', 'article:view', 2, 5, NULL, 95, 1),
('文章新增', 'article:add', 2, 5, NULL, 94, 1),
('文章编辑', 'article:edit', 2, 5, NULL, 93, 1),
('文章删除', 'article:delete', 2, 5, NULL, 92, 1),

-- 分类管理按钮
('分类查看', 'category:view', 2, 6, NULL, 95, 1),
('分类新增', 'category:add', 2, 6, NULL, 94, 1),
('分类编辑', 'category:edit', 2, 6, NULL, 93, 1),
('分类删除', 'category:delete', 2, 6, NULL, 92, 1),

-- 标签管理按钮
('标签查看', 'tag:view', 2, 7, NULL, 95, 1),
('标签新增', 'tag:add', 2, 7, NULL, 94, 1),
('标签编辑', 'tag:edit', 2, 7, NULL, 93, 1),
('标签删除', 'tag:delete', 2, 7, NULL, 92, 1),

-- 评论管理按钮
('评论查看', 'comment:view', 2, 8, NULL, 95, 1),
('评论审核', 'comment:audit', 2, 8, NULL, 94, 1),
('评论删除', 'comment:delete', 2, 8, NULL, 93, 1),

-- 用户管理按钮
('用户查看', 'user:view', 2, 9, NULL, 95, 1),
('用户新增', 'user:add', 2, 9, NULL, 94, 1),
('用户编辑', 'user:edit', 2, 9, NULL, 93, 1),
('用户删除', 'user:delete', 2, 9, NULL, 92, 1),
('用户角色分配', 'user:assign_role', 2, 9, NULL, 91, 1),

-- 角色管理按钮
('角色查看', 'role:view', 2, 10, NULL, 95, 1),
('角色新增', 'role:add', 2, 10, NULL, 94, 1),
('角色编辑', 'role:edit', 2, 10, NULL, 93, 1),
('角色删除', 'role:delete', 2, 10, NULL, 92, 1),
('角色权限分配', 'role:assign_permission', 2, 10, NULL, 91, 1),

-- 权限管理按钮
('权限查看', 'permission:view', 2, 11, NULL, 95, 1),
('权限新增', 'permission:add', 2, 11, NULL, 94, 1),
('权限编辑', 'permission:edit', 2, 11, NULL, 93, 1),
('权限删除', 'permission:delete', 2, 11, NULL, 92, 1);

-- 初始化权限数据（接口权限）
INSERT INTO `tb_permission` (`name`, `code`, `type`, `parent_id`, `path`, `sort`, `status`) VALUES
-- 文章接口
('文章列表接口', 'api:articles', 3, 5, '/articles', 95, 1),
('文章详情接口', 'api:articles:detail', 3, 5, '/articles/*', 94, 1),
('文章新增接口', 'api:articles:add', 3, 5, '/articles', 93, 1),
('文章更新接口', 'api:articles:update', 3, 5, '/articles/*', 92, 1),
('文章删除接口', 'api:articles:delete', 3, 5, '/articles/*', 91, 1),

-- 用户接口
('用户列表接口', 'api:users', 3, 9, '/users', 95, 1),
('用户详情接口', 'api:users:detail', 3, 9, '/users/*', 94, 1),
('用户新增接口', 'api:users:add', 3, 9, '/users', 93, 1),
('用户更新接口', 'api:users:update', 3, 9, '/users/*', 92, 1),
('用户删除接口', 'api:users:delete', 3, 9, '/users/*', 91, 1),

-- 角色接口
('角色列表接口', 'api:roles', 3, 10, '/roles', 95, 1),
('角色详情接口', 'api:roles:detail', 3, 10, '/roles/*', 94, 1),
('角色新增接口', 'api:roles:add', 3, 10, '/roles', 93, 1),
('角色更新接口', 'api:roles:update', 3, 10, '/roles/*', 92, 1),
('角色删除接口', 'api:roles:delete', 3, 10, '/roles/*', 91, 1),
('角色权限分配接口', 'api:roles:permissions', 3, 10, '/roles/*/permissions', 90, 1),

-- 权限接口
('权限列表接口', 'api:permissions', 3, 11, '/permissions', 95, 1),
('权限详情接口', 'api:permissions:detail', 3, 11, '/permissions/*', 94, 1),
('权限新增接口', 'api:permissions:add', 3, 11, '/permissions', 93, 1),
('权限更新接口', 'api:permissions:update', 3, 11, '/permissions/*', 92, 1),
('权限删除接口', 'api:permissions:delete', 3, 11, '/permissions/*', 91, 1),
('权限树接口', 'api:permissions:tree', 3, 11, '/permissions/tree', 90, 1);

-- 初始化角色权限关联数据
-- 超级管理员拥有所有权限
INSERT INTO `tb_role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `tb_permission`;

-- 管理员拥有除系统管理外的所有权限
INSERT INTO `tb_role_permission` (`role_id`, `permission_id`)
SELECT 2, id FROM `tb_permission` WHERE `parent_id` NOT IN (SELECT id FROM `tb_permission` WHERE `code