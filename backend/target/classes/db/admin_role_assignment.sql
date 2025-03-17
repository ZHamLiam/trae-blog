-- 为admin用户分配超级管理员角色

-- 首先确保admin用户存在（假设admin用户ID为1，如果不是请调整）
-- 注意：在实际环境中，建议先查询确认admin用户的实际ID
-- SELECT id FROM tb_user WHERE username = 'admin';

-- 为admin用户分配超级管理员角色（角色ID为1）
INSERT INTO `tb_user_role` (`user_id`, `role_id`) VALUES
(1, 1);

-- 如果不确定admin用户的ID，可以使用以下SQL语句
-- INSERT INTO `tb_user_role` (`user_id`, `role_id`)
-- SELECT id, 1 FROM `tb_user` WHERE username = 'admin';

-- 注意：如果记录已存在，上述语句可能会因为唯一约束而失败
-- 可以使用以下语句避免重复插入
-- INSERT IGNORE INTO `tb_user_role` (`user_id`, `role_id`)
-- SELECT id, 1 FROM `tb_user` WHERE username = 'admin';