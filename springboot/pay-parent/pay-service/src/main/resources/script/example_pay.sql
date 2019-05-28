/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : localhost:3306
 Source Schema         : example_pay

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 16/04/2019 10:51:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for pay_order
-- ----------------------------
DROP TABLE IF EXISTS `pay_order`;
CREATE TABLE `pay_order`  (
  `id` bigint(20) NOT NULL,
  `pay_type` tinyint(4) DEFAULT NULL COMMENT '1支付宝 2微信 3小程序',
  `terminal_type` tinyint(4) DEFAULT NULL COMMENT '1 h5 2 app',
  `app_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '产品名称',
  `product_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '如充电10分钟',
  `product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '产品id(物联网设备代表设备id)可更换',
  `out_trade_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '商户生成订单号',
  `trade_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '交易号 第三方支付回掉返回',
  `original_price` decimal(10, 2) DEFAULT NULL COMMENT '原价',
  `special_price` decimal(10, 2) DEFAULT NULL COMMENT '优惠价(即实际支付金额)',
  `total_refund_fee` decimal(10, 2) DEFAULT NULL COMMENT '退款的总金额',
  `pay_time` datetime(0) DEFAULT NULL COMMENT '支付时间',
  `buyer_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '购买人的id',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '购买人的ip地址',
  `return_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '前端跳转地址',
  `notify_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '回调地址 支付宝支付回掉和退款回掉同一个',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态0初始化1支付成功2退款中3退款完成',
  `update_time` datetime(0) DEFAULT NULL COMMENT '最近一次更新时间',
  `create_time` datetime(0) DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_out_trade_no`(`out_trade_no`) USING BTREE,
  UNIQUE INDEX `uk_order_trade_no`(`trade_no`) USING BTREE,
  UNIQUE INDEX `uk_order_out_refund_no`(`trade_no`) USING BTREE,
  INDEX `idx_order_pay_time`(`pay_time`) USING BTREE,
  INDEX `idx_order_buyer_id`(`buyer_id`) USING BTREE,
  INDEX `idx_order_product_id`(`product_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pay_order
-- ----------------------------
INSERT INTO `pay_order` VALUES (170216329389805568, 1, 1, '2019010262725753', '111', '111', '2019041516585536000010001', NULL, 0.00, 0.00, NULL, NULL, '1', '0:0:0:0:0:0:0:1', 'http://www.sina.com/index.html', 'http://www.baidu.com/cmsapiorderH5Pay/h5PayNotify/1', 0, '2019-04-15 16:58:55', '2019-04-15 16:58:55', NULL);
INSERT INTO `pay_order` VALUES (170218103458758656, 2, 1, NULL, '111', '111', '2019041517055777400000001', NULL, 0.00, 1.00, NULL, NULL, '1', '0:0:0:0:0:0:0:1', 'http://www.sina.com/index.html', 'http://www.baidu.com/cmsapiorderH5Pay/h5PayNotify/2', 0, '2019-04-15 17:05:58', '2019-04-15 17:05:58', NULL);
INSERT INTO `pay_order` VALUES (170218311890501632, 2, 1, NULL, '111', '111', '2019041517062672900000001', NULL, 0.00, 1.00, NULL, NULL, '1', '0:0:0:0:0:0:0:1', 'http://www.sina.com/index.html', 'http://www.baidu.com/cmsapiorderH5Pay/h5PayNotify/2', 0, '2019-04-15 17:06:27', '2019-04-15 17:06:27', NULL);
INSERT INTO `pay_order` VALUES (170218425480642560, 2, 1, NULL, '111', '111', '2019041517071205000000001', NULL, 0.00, 1.00, NULL, NULL, '1', '0:0:0:0:0:0:0:1', 'http://www.sina.com/index.html', 'http://www.baidu.com/cmsapiorderH5Pay/h5PayNotify/2', 0, '2019-04-15 17:07:12', '2019-04-15 17:07:12', NULL);
INSERT INTO `pay_order` VALUES (170218740049248256, 2, 1, NULL, '111', '111', '2019041517073999000000001', NULL, 0.00, 1.00, NULL, NULL, '1', '0:0:0:0:0:0:0:1', 'http://www.sina.com/index.html', 'http://www.baidu.com/cmsapiorderH5Pay/h5PayNotify/2', 0, '2019-04-15 17:07:40', '2019-04-15 17:07:40', NULL);
INSERT INTO `pay_order` VALUES (170236703359893504, 2, 1, NULL, '111', '111', '2019041517093999300000001', NULL, 0.00, 1.00, NULL, NULL, '1', '0:0:0:0:0:0:0:1', 'http://www.sina.com/index.html', 'http://www.baidu.com/cmsapiorderH5Pay/h5PayNotify/2', 0, '2019-04-15 17:09:40', '2019-04-15 17:09:40', NULL);

-- ----------------------------
-- Table structure for pay_order_refund
-- ----------------------------
DROP TABLE IF EXISTS `pay_order_refund`;
CREATE TABLE `pay_order_refund` (
  `id` bigint(20) NOT NULL,
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单id',
  `out_trade_no` varchar(25) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '商户订单号',
  `total_amount` decimal(10,2) DEFAULT NULL COMMENT '订单支付金额',
  `out_refund_no` varchar(25) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '退款订单号',
  `refund_amount` decimal(10,2) DEFAULT NULL COMMENT '退款金额',
  `refund_status` tinyint(4) DEFAULT NULL COMMENT '1发起退款成功2退款成功3退款失败',
  `refund_success_time` datetime DEFAULT NULL COMMENT '退款成功时间',
  `refund_notify_url` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '退款回调地址主要针对微信支付宝不需要',
  `refund_type` tinyint(4) DEFAULT NULL COMMENT '退款类型 1结算退款2人工退款',
  `remark` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  `creator` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `editor` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `edit_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_order_refund_out_refund_no` (`out_refund_no`) USING BTREE,
  KEY `idx_order_refund_order_id` (`order_id`) USING BTREE,
  KEY `idx_order_refund_out_trade_no` (`out_trade_no`),
  KEY `idx_order_refund_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

SET FOREIGN_KEY_CHECKS = 1;
