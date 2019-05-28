/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : localhost:3306
 Source Schema         : example_sys

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 01/05/2019 09:12:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint(20) NOT NULL,
  `config_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` datetime(0) DEFAULT NULL,
  `editor` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `edit_time` datetime(0) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_config_config_key`(`config_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (175885841015513088, '1', '1', NULL, 'admin', '2019-05-01 08:27:32', NULL, NULL);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint(20) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'url',
  `level` tinyint(255) DEFAULT NULL COMMENT '菜单等级1顶级2子菜单3按钮',
  `permission` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '权限名称 如sysUser:edit',
  `status` tinyint(4) DEFAULT NULL COMMENT '0禁用1正常',
  `sort` int(11) DEFAULT NULL COMMENT '排序每一个菜单下都是从0开始',
  `creator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` datetime(0) DEFAULT NULL,
  `editor` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `edit_time` datetime(0) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_menu_permission`(`permission`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (165390286267088896, 0, '根目录', NULL, NULL, 0, NULL, 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (165436136594866176, 165390286267088896, '系统管理', 'layui-icon layui-icon-user', NULL, 1, NULL, 1, 1, NULL, NULL, NULL, '2019-05-01 08:09:39');
INSERT INTO `sys_menu` VALUES (165436637738696704, 165436136594866176, '用户管理', NULL, 'views/sys/sys_user.html', 2, 'sysUser:list', 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (165436727484219392, 165436637738696704, '编辑', NULL, NULL, 3, 'sysUser:edit', 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (166133725745119232, 165436637738696704, '删除', NULL, NULL, 3, 'sysUser:delete', 1, 2, NULL, NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (166133782183673856, 165436637738696704, '查询', NULL, NULL, 3, 'sysUser:info', 1, 3, NULL, NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (166134229267120128, 165436136594866176, '角色管理', NULL, 'views/sys/sys_role.html', 2, 'sysRole:list', 1, 2, NULL, NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (166134280815116288, 166134229267120128, '编辑', NULL, NULL, 3, 'sysRole:edit', 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (166134982526369792, 166134229267120128, '删除', NULL, NULL, 3, 'sysRole:delete', 1, 2, NULL, NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (166135061362507776, 166134229267120128, '查询', NULL, NULL, 3, 'sysRole:info', 1, 3, NULL, NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (166135362647752704, 165436136594866176, '菜单管理', NULL, 'views/sys/sys_menu.html', 2, 'sysMenu:list', 1, 3, NULL, NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (166135446684827648, 166135362647752704, '新增', NULL, NULL, 3, 'sysMenu:edit', 1, 1, NULL, NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (166160926762864640, 166135362647752704, '查询', NULL, NULL, 3, 'sysMenu:info', 1, 2, NULL, NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (175881501961887744, 165436136594866176, '参数管理', '点击选择', 'views/sys/sys_config.html', 2, 'sysConfig:list', 1, 4, 'admin', '2019-05-01 08:10:18', NULL, NULL);
INSERT INTO `sys_menu` VALUES (175881558132006912, 175881501961887744, '编辑', '点击选择', NULL, 3, 'sysConfig:edit', 1, 1, 'admin', '2019-05-01 08:10:31', NULL, NULL);
INSERT INTO `sys_menu` VALUES (175881683688497152, 175881501961887744, '删除', '点击选择', NULL, 3, 'sysConfig:delete', 1, 2, 'admin', '2019-05-01 08:11:01', NULL, NULL);
INSERT INTO `sys_menu` VALUES (175881775589892096, 175881501961887744, '查看', '点击选择', NULL, 3, 'sysConfig:info', 1, 3, 'admin', '2019-05-01 08:11:23', NULL, NULL);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint(20) NOT NULL,
  `role_cn_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` datetime(0) DEFAULT NULL,
  `editor` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `edit_time` datetime(0) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_role_name`(`role_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (165390286267088896, '超级管理员', 'Super', 1, NULL, '2019-04-02 09:21:57', NULL, '2019-05-01 08:11:38');
INSERT INTO `sys_role` VALUES (166132763487895552, '普通管理员', 'Common', 1, NULL, '2019-04-04 10:32:17', NULL, NULL);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint(20) NOT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  `menu_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (166162093827952640, 166132763487895552, 165390286267088896);
INSERT INTO `sys_role_menu` VALUES (166162093840535552, 166132763487895552, 165436136594866176);
INSERT INTO `sys_role_menu` VALUES (166162093853118464, 166132763487895552, 165436637738696704);
INSERT INTO `sys_role_menu` VALUES (166162093861507072, 166132763487895552, 165436727484219392);
INSERT INTO `sys_role_menu` VALUES (166162093865701376, 166132763487895552, 166133725745119232);
INSERT INTO `sys_role_menu` VALUES (166162093869895680, 166132763487895552, 166133782183673856);
INSERT INTO `sys_role_menu` VALUES (166162093874089984, 166132763487895552, 166134229267120128);
INSERT INTO `sys_role_menu` VALUES (166162093878284288, 166132763487895552, 166134280815116288);
INSERT INTO `sys_role_menu` VALUES (166162093882478592, 166132763487895552, 166134982526369792);
INSERT INTO `sys_role_menu` VALUES (166162093886672896, 166132763487895552, 166135061362507776);
INSERT INTO `sys_role_menu` VALUES (166162093890867200, 166132763487895552, 166135362647752704);
INSERT INTO `sys_role_menu` VALUES (166162093895061504, 166132763487895552, 166135446684827648);
INSERT INTO `sys_role_menu` VALUES (166162093895061505, 166132763487895552, 166160926762864640);
INSERT INTO `sys_role_menu` VALUES (175881838617698304, 165390286267088896, 165390286267088896);
INSERT INTO `sys_role_menu` VALUES (175881838630281216, 165390286267088896, 165436136594866176);
INSERT INTO `sys_role_menu` VALUES (175881838638669824, 165390286267088896, 165436637738696704);
INSERT INTO `sys_role_menu` VALUES (175881838642864128, 165390286267088896, 165436727484219392);
INSERT INTO `sys_role_menu` VALUES (175881838642864129, 165390286267088896, 166133725745119232);
INSERT INTO `sys_role_menu` VALUES (175881838647058432, 165390286267088896, 166133782183673856);
INSERT INTO `sys_role_menu` VALUES (175881838655447040, 165390286267088896, 166134229267120128);
INSERT INTO `sys_role_menu` VALUES (175881838655447041, 165390286267088896, 166134280815116288);
INSERT INTO `sys_role_menu` VALUES (175881838659641344, 165390286267088896, 166134982526369792);
INSERT INTO `sys_role_menu` VALUES (175881838663835648, 165390286267088896, 166135061362507776);
INSERT INTO `sys_role_menu` VALUES (175881838668029952, 165390286267088896, 166135362647752704);
INSERT INTO `sys_role_menu` VALUES (175881838668029953, 165390286267088896, 166135446684827648);
INSERT INTO `sys_role_menu` VALUES (175881838672224256, 165390286267088896, 166160926762864640);
INSERT INTO `sys_role_menu` VALUES (175881838676418560, 165390286267088896, 175881501961887744);
INSERT INTO `sys_role_menu` VALUES (175881838680612864, 165390286267088896, 175881558132006912);
INSERT INTO `sys_role_menu` VALUES (175881838684807168, 165390286267088896, 175881683688497152);
INSERT INTO `sys_role_menu` VALUES (175881838689001472, 165390286267088896, 175881775589892096);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(20) NOT NULL,
  `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL COMMENT '0锁定1正常',
  `creator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` datetime(0) DEFAULT NULL,
  `editor` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `edit_time` datetime(0) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_account`(`account`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (166132226201747456, 'admin', 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL, '2019-04-04 10:30:09', NULL, '2019-04-04 16:32:24');
INSERT INTO `sys_user` VALUES (166195562666786816, 'caolu', 'caolu', 'e10adc3949ba59abbe56e057f20f883e', 0, NULL, '2019-04-04 14:41:50', NULL, '2019-04-04 16:32:07');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (166223318024716288, 166195562666786816, 165390286267088896);
INSERT INTO `sys_user_role` VALUES (166223387058765824, 166132226201747456, 165390286267088896);
INSERT INTO `sys_user_role` VALUES (166223387067154432, 166132226201747456, 166132763487895552);

SET FOREIGN_KEY_CHECKS = 1;
