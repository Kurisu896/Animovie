/*
 Navicat Premium Data Transfer

 Source Server         : mysql9.2
 Source Server Type    : MySQL
 Source Server Version : 90200
 Source Host           : localhost:3306
 Source Schema         : bgm

 Target Server Type    : MySQL
 Target Server Version : 90200
 File Encoding         : 65001

 Date: 18/05/2026 22:55:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for collections
-- ----------------------------
DROP TABLE IF EXISTS `collections`;
CREATE TABLE `collections`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `subject_id` bigint(0) NOT NULL COMMENT '条目ID',
  `status` enum('WATCHING','WATCHED','DROPPED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收藏状态',
  `progress_episode` int(0) NULL DEFAULT 0 COMMENT '进度集数',
  `rating` int(0) NULL DEFAULT NULL COMMENT '评分',
  `short_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '吐槽',
  `updated_date` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_subject`(`user_id`, `subject_id`) USING BTREE,
  INDEX `fk_collections_subject`(`subject_id`) USING BTREE,
  CONSTRAINT `fk_collections_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_collections_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 103 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collections
-- ----------------------------
INSERT INTO `collections` VALUES (1, 1, 148, 'DROPPED', 0, 6, '还行，不算难看', '2026-04-07 17:39:46');
INSERT INTO `collections` VALUES (4, 1, 144, 'WATCHED', 9, 9, 'test11', '2025-11-27 22:25:47');
INSERT INTO `collections` VALUES (5, 1, 145, 'WATCHING', 27, 10, 'test22222', '2026-05-06 01:30:16');
INSERT INTO `collections` VALUES (9, 1, 139, 'WATCHED', 11, 9, '挺好', '2026-04-08 20:50:50');
INSERT INTO `collections` VALUES (12, 5, 145, 'WATCHED', 11, 10, 'test', '2025-12-04 16:20:52');
INSERT INTO `collections` VALUES (15, 56, 98, 'WATCHED', 26, 9, '年度神作', '2025-11-30 16:35:01');
INSERT INTO `collections` VALUES (16, 57, 99, 'WATCHING', 10, 8, '值得期待', '2025-11-30 16:35:02');
INSERT INTO `collections` VALUES (17, 58, 100, 'WATCHED', 16, 10, '献出心脏！', '2025-11-30 16:35:03');
INSERT INTO `collections` VALUES (18, 59, 101, 'DROPPED', 1, NULL, '胃疼，看不下去', '2025-11-30 16:35:04');
INSERT INTO `collections` VALUES (19, 60, 102, 'WATCHING', 5, 7, '日常系很放松', '2025-11-30 16:35:05');
INSERT INTO `collections` VALUES (20, 61, 103, 'WATCHED', 12, 8, '打戏超赞', '2025-11-30 16:35:06');
INSERT INTO `collections` VALUES (21, 62, 104, 'WATCHED', 12, 10, '孤独摇滚YYDS', '2025-11-30 16:35:07');
INSERT INTO `collections` VALUES (22, 63, 105, 'WATCHING', 3, 9, '原创佳作', '2025-11-30 16:35:08');
INSERT INTO `collections` VALUES (23, 64, 106, 'WATCHED', 11, 7, '题材新颖', '2025-11-30 16:35:09');
INSERT INTO `collections` VALUES (24, 65, 107, 'WATCHING', 8, 9, '治愈系神作', '2025-11-30 16:35:10');
INSERT INTO `collections` VALUES (25, 66, 108, 'WATCHED', 13, 8, '水墨画风很独特', '2025-11-30 16:35:11');
INSERT INTO `collections` VALUES (26, 67, 109, 'DROPPED', 5, 6, '看不懂维京历史', '2025-11-30 16:35:12');
INSERT INTO `collections` VALUES (27, 68, 110, 'WATCHING', 15, 9, '女主智商在线', '2025-11-30 16:35:13');
INSERT INTO `collections` VALUES (28, 69, 111, 'WATCHED', 23, 10, '神展开！', '2025-11-30 16:35:14');
INSERT INTO `collections` VALUES (29, 70, 112, 'WATCHING', 4, 7, '异世界穿越', '2025-11-30 16:35:15');
INSERT INTO `collections` VALUES (70, 63, 98, 'WATCHED', 26, 9, '超赞！', '2025-11-30 17:15:01');
INSERT INTO `collections` VALUES (71, 64, 98, 'WATCHING', 10, 8, '特效好评', '2025-11-30 17:15:02');
INSERT INTO `collections` VALUES (72, 65, 98, 'WATCHED', 26, 10, 'UFO永恒的神！', '2025-11-30 17:15:03');
INSERT INTO `collections` VALUES (73, 66, 98, 'DROPPED', 5, 5, '太吵了', '2025-11-30 17:15:04');
INSERT INTO `collections` VALUES (74, 67, 98, 'WATCHED', 26, 9, '年度霸权', '2025-11-30 17:15:05');
INSERT INTO `collections` VALUES (75, 68, 98, 'WATCHING', 0, NULL, '准备开始看', '2025-11-30 17:15:06');
INSERT INTO `collections` VALUES (76, 69, 98, 'WATCHED', 26, 8, '中规中矩的优秀', '2025-11-30 17:15:07');
INSERT INTO `collections` VALUES (77, 63, 99, 'WATCHED', 24, 10, '五条悟yyds', '2025-11-30 17:15:10');
INSERT INTO `collections` VALUES (78, 64, 99, 'WATCHING', 12, 9, '战斗分镜拉满', '2025-11-30 17:15:11');
INSERT INTO `collections` VALUES (79, 65, 99, 'WATCHED', 24, 8, '剧情紧凑', '2025-11-30 17:15:12');
INSERT INTO `collections` VALUES (80, 66, 99, 'DROPPED', 3, 6, '看不懂咒力设定', '2025-11-30 17:15:13');
INSERT INTO `collections` VALUES (81, 67, 99, 'WATCHING', 0, NULL, '准备入坑', '2025-11-30 17:15:14');
INSERT INTO `collections` VALUES (82, 68, 99, 'WATCHED', 24, 9, '黑闪帅炸', '2025-11-30 17:15:15');
INSERT INTO `collections` VALUES (83, 69, 99, 'WATCHING', 15, 7, '很期待后续', '2025-11-30 17:15:16');
INSERT INTO `collections` VALUES (84, 56, 145, 'WATCHED', 24, 10, '命运石之门的神作！', '2025-11-30 17:15:20');
INSERT INTO `collections` VALUES (85, 57, 145, 'WATCHED', 24, 10, '时间旅行的经典', '2025-11-30 17:15:21');
INSERT INTO `collections` VALUES (86, 58, 145, 'WATCHING', 15, 9, '红莉栖我老婆', '2025-11-30 17:15:22');
INSERT INTO `collections` VALUES (87, 59, 145, 'WATCHED', 24, 9, '中二病拯救世界', '2025-11-30 17:15:23');
INSERT INTO `collections` VALUES (88, 60, 145, 'WATCHING', 10, 7, '前期节奏太慢', '2026-04-08 16:24:03');
INSERT INTO `collections` VALUES (89, 61, 145, 'WATCHED', 24, 10, '必看作品', '2025-11-30 17:15:25');
INSERT INTO `collections` VALUES (90, 62, 145, 'WATCHING', 0, NULL, '听说很神', '2025-11-30 17:15:26');
INSERT INTO `collections` VALUES (91, 1, 160, 'WATCHING', 2, 8, 'test', '2025-12-04 15:06:40');
INSERT INTO `collections` VALUES (92, 5, 144, 'WATCHED', 14, 5, '1111', '2025-12-05 16:10:49');
INSERT INTO `collections` VALUES (94, 5, 160, 'WATCHED', 1, 8, '还行', '2025-12-21 18:50:57');
INSERT INTO `collections` VALUES (95, 1, 159, 'WATCHED', 8, NULL, '', '2025-12-06 00:25:16');
INSERT INTO `collections` VALUES (97, 1, 156, 'WATCHING', 0, 8, '', '2025-12-21 18:06:56');
INSERT INTO `collections` VALUES (101, 1, 157, 'WATCHED', 1, 10, 'test', '2025-12-24 21:15:52');
INSERT INTO `collections` VALUES (102, 1, 166, 'WATCHED', 0, 2, '111', '2026-04-07 17:38:46');
INSERT INTO `collections` VALUES (103, 60, 148, 'WATCHING', 5, NULL, '', '2026-04-08 16:32:21');

-- ----------------------------
-- Table structure for comment_likes
-- ----------------------------
DROP TABLE IF EXISTS `comment_likes`;
CREATE TABLE `comment_likes`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `comment_id` bigint(0) NOT NULL COMMENT '评论ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `created_at` datetime(0) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_comment_user`(`comment_id`, `user_id`) USING BTREE,
  INDEX `fk_comment_likes_user`(`user_id`) USING BTREE,
  CONSTRAINT `fk_comment_likes_comment` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_likes_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '话题回复点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment_likes
-- ----------------------------
INSERT INTO `comment_likes` VALUES (15, 100, 56, '2025-11-30 16:40:01');
INSERT INTO `comment_likes` VALUES (16, 101, 57, '2025-11-30 16:40:02');
INSERT INTO `comment_likes` VALUES (17, 102, 58, '2025-11-30 16:40:03');
INSERT INTO `comment_likes` VALUES (18, 103, 59, '2025-11-30 16:40:04');
INSERT INTO `comment_likes` VALUES (19, 104, 60, '2025-11-30 16:40:05');
INSERT INTO `comment_likes` VALUES (20, 105, 61, '2025-11-30 16:40:06');
INSERT INTO `comment_likes` VALUES (21, 106, 62, '2025-11-30 16:40:07');
INSERT INTO `comment_likes` VALUES (22, 107, 63, '2025-11-30 16:40:08');
INSERT INTO `comment_likes` VALUES (23, 108, 64, '2025-11-30 16:40:09');
INSERT INTO `comment_likes` VALUES (24, 109, 65, '2025-11-30 16:40:10');
INSERT INTO `comment_likes` VALUES (25, 110, 66, '2025-11-30 16:40:11');
INSERT INTO `comment_likes` VALUES (26, 111, 67, '2025-11-30 16:40:12');
INSERT INTO `comment_likes` VALUES (27, 112, 68, '2025-11-30 16:40:13');
INSERT INTO `comment_likes` VALUES (28, 113, 69, '2025-11-30 16:40:14');
INSERT INTO `comment_likes` VALUES (29, 114, 70, '2025-11-30 16:40:15');
INSERT INTO `comment_likes` VALUES (37, 118, 1, '2025-12-08 23:24:15');
INSERT INTO `comment_likes` VALUES (38, 117, 1, '2025-12-08 23:24:20');
INSERT INTO `comment_likes` VALUES (39, 144, 56, '2025-12-21 18:52:01');
INSERT INTO `comment_likes` VALUES (41, 147, 56, '2025-12-21 18:52:04');
INSERT INTO `comment_likes` VALUES (43, 148, 56, '2025-12-21 18:52:11');
INSERT INTO `comment_likes` VALUES (48, 148, 1, '2025-12-25 17:54:48');
INSERT INTO `comment_likes` VALUES (53, 144, 1, '2026-04-19 23:40:11');

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `topic_id` bigint(0) NOT NULL COMMENT '话题ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `parent_id` bigint(0) NULL DEFAULT NULL COMMENT '父评论ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `like_count` int(0) NOT NULL DEFAULT 0 COMMENT '点赞数',
  `floor` int(0) NOT NULL COMMENT '楼层',
  `created_at` datetime(0) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_comments_topic`(`topic_id`) USING BTREE,
  INDEX `fk_comments_user`(`user_id`) USING BTREE,
  INDEX `fk_comments_parent`(`parent_id`) USING BTREE,
  CONSTRAINT `fk_comments_parent` FOREIGN KEY (`parent_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_comments_topic` FOREIGN KEY (`topic_id`) REFERENCES `topics` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_comments_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 151 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '话题回复表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comments
-- ----------------------------
INSERT INTO `comments` VALUES (100, 28, 56, NULL, '主要还是ufo的制作和王道的剧情，很适合大众口味', 0, 1, '2025-11-30 16:39:01');
INSERT INTO `comments` VALUES (101, 29, 57, NULL, '当然是五条悟！谁能拒绝最强呢？', 0, 1, '2025-11-30 16:39:02');
INSERT INTO `comments` VALUES (102, 30, 58, NULL, 'MAPPA发挥稳定，经费爆炸，打斗场面非常精彩', 0, 1, '2025-11-30 16:39:03');
INSERT INTO `comments` VALUES (103, 31, 59, NULL, '我认为是最好的结局，虽然意难平，但符合他追求自由的个性', 0, 1, '2025-11-30 16:39:04');
INSERT INTO `comments` VALUES (104, 32, 60, NULL, '毕竟是喜剧，主打一个温馨日常，打斗不是主线', 0, 1, '2025-11-30 16:39:05');
INSERT INTO `comments` VALUES (105, 33, 61, NULL, '不可能的，她只会用吉他掩盖社恐，真正的社牛永远是喜多！', 0, 1, '2025-11-30 16:39:06');
INSERT INTO `comments` VALUES (106, 34, 62, NULL, '星歌姐姐才是真正的吉他英雄！波奇是未来！', 0, 1, '2025-11-30 16:39:07');
INSERT INTO `comments` VALUES (107, 35, 63, NULL, '千泷永不倒！官方发糖不要停！', 0, 1, '2025-11-30 16:39:08');
INSERT INTO `comments` VALUES (108, 36, 64, NULL, '我怀疑是医生在幕后操控一切，他才是最可怕的', 0, 1, '2025-11-30 16:39:09');
INSERT INTO `comments` VALUES (109, 37, 65, NULL, '至少还能活上千年吧，期待她最终找到生命的意义。', 0, 1, '2025-11-30 16:39:10');
INSERT INTO `comments` VALUES (110, 38, 66, NULL, '为了老婆，他什么都愿意做，这是忍者最高的浪漫', 0, 1, '2025-11-30 16:39:11');
INSERT INTO `comments` VALUES (111, 39, 67, NULL, '这是他成长蜕变的必经之路啊，只有经历了痛苦才能迎来真正的和平', 0, 1, '2025-11-30 16:39:12');
INSERT INTO `comments` VALUES (112, 40, 68, NULL, '我也等了好久了，壬氏的醋意太可爱了，快点撒糖！', 0, 1, '2025-11-30 16:39:13');
INSERT INTO `comments` VALUES (113, 41, 69, NULL, '很难说，但相信芥见下下！他总是能带来意想不到的惊喜', 0, 1, '2025-11-30 16:39:14');
INSERT INTO `comments` VALUES (114, 42, 70, NULL, '确实，希望能加快节奏，多讲一些主线剧情', 0, 1, '2025-11-30 16:39:15');
INSERT INTO `comments` VALUES (117, 7, 1, NULL, '111', 1, 1, '2025-11-30 19:17:53');
INSERT INTO `comments` VALUES (118, 7, 1, 117, '111', 1, 2, '2025-11-30 19:17:57');
INSERT INTO `comments` VALUES (119, 7, 1, NULL, '2222', 0, 3, '2025-12-01 10:24:42');
INSERT INTO `comments` VALUES (120, 7, 5, 117, '11111', 0, 4, '2025-12-05 10:19:55');
INSERT INTO `comments` VALUES (144, 53, 1, NULL, 'test', 2, 1, '2025-12-21 18:50:09');
INSERT INTO `comments` VALUES (145, 53, 1, 144, 'test', 0, 2, '2025-12-21 18:50:12');
INSERT INTO `comments` VALUES (146, 53, 5, 144, 'test', 0, 3, '2025-12-21 18:50:24');
INSERT INTO `comments` VALUES (147, 53, 5, NULL, 'test', 1, 4, '2025-12-21 18:50:28');
INSERT INTO `comments` VALUES (148, 53, 56, 144, '111', 2, 5, '2025-12-21 18:51:56');
INSERT INTO `comments` VALUES (149, 53, 56, NULL, '2222', 0, 6, '2025-12-21 18:51:59');
INSERT INTO `comments` VALUES (150, 60, 1, NULL, '111', 0, 1, '2025-12-24 22:39:56');

-- ----------------------------
-- Table structure for subject_comment_likes
-- ----------------------------
DROP TABLE IF EXISTS `subject_comment_likes`;
CREATE TABLE `subject_comment_likes`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `comment_id` bigint(0) NOT NULL COMMENT '评论ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `created_at` datetime(0) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sc_user`(`comment_id`, `user_id`) USING BTREE,
  INDEX `fk_subject_comment_likes_user`(`user_id`) USING BTREE,
  CONSTRAINT `fk_subject_comment_likes_comment` FOREIGN KEY (`comment_id`) REFERENCES `subject_comments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_subject_comment_likes_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '条目评论点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of subject_comment_likes
-- ----------------------------
INSERT INTO `subject_comment_likes` VALUES (46, 57, 56, '2025-11-30 16:37:01');
INSERT INTO `subject_comment_likes` VALUES (47, 58, 57, '2025-11-30 16:37:02');
INSERT INTO `subject_comment_likes` VALUES (48, 59, 58, '2025-11-30 16:37:03');
INSERT INTO `subject_comment_likes` VALUES (49, 60, 59, '2025-11-30 16:37:04');
INSERT INTO `subject_comment_likes` VALUES (50, 61, 60, '2025-11-30 16:37:05');
INSERT INTO `subject_comment_likes` VALUES (51, 62, 61, '2025-11-30 16:37:06');
INSERT INTO `subject_comment_likes` VALUES (52, 63, 62, '2025-11-30 16:37:07');
INSERT INTO `subject_comment_likes` VALUES (53, 64, 63, '2025-11-30 16:37:08');
INSERT INTO `subject_comment_likes` VALUES (54, 65, 64, '2025-11-30 16:37:09');
INSERT INTO `subject_comment_likes` VALUES (55, 66, 65, '2025-11-30 16:37:10');
INSERT INTO `subject_comment_likes` VALUES (56, 67, 66, '2025-11-30 16:37:11');
INSERT INTO `subject_comment_likes` VALUES (57, 68, 67, '2025-11-30 16:37:12');
INSERT INTO `subject_comment_likes` VALUES (58, 69, 68, '2025-11-30 16:37:13');
INSERT INTO `subject_comment_likes` VALUES (59, 70, 69, '2025-11-30 16:37:14');
INSERT INTO `subject_comment_likes` VALUES (60, 71, 70, '2025-11-30 16:37:15');
INSERT INTO `subject_comment_likes` VALUES (63, 73, 5, '2025-12-04 16:20:11');
INSERT INTO `subject_comment_likes` VALUES (64, 2, 5, '2025-12-05 15:51:31');
INSERT INTO `subject_comment_likes` VALUES (70, 81, 1, '2026-04-08 17:18:34');
INSERT INTO `subject_comment_likes` VALUES (71, 6, 1, '2026-04-08 17:18:38');

-- ----------------------------
-- Table structure for subject_comments
-- ----------------------------
DROP TABLE IF EXISTS `subject_comments`;
CREATE TABLE `subject_comments`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subject_id` bigint(0) NOT NULL COMMENT '条目ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `like_count` int(0) NOT NULL DEFAULT 0 COMMENT '点赞数',
  `created_at` datetime(0) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_subject_comments_subject`(`subject_id`) USING BTREE,
  INDEX `fk_subject_comments_user`(`user_id`) USING BTREE,
  CONSTRAINT `fk_subject_comments_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_subject_comments_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 83 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '条目评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of subject_comments
-- ----------------------------
INSERT INTO `subject_comments` VALUES (1, 144, 1, '评论test', 0, '2025-11-26 23:40:28');
INSERT INTO `subject_comments` VALUES (2, 144, 1, 'test2', 1, '2025-11-26 23:44:35');
INSERT INTO `subject_comments` VALUES (5, 145, 5, '111', 0, '2025-11-29 15:45:45');
INSERT INTO `subject_comments` VALUES (6, 145, 1, 'test', 1, '2025-11-30 14:18:12');
INSERT INTO `subject_comments` VALUES (57, 98, 56, '鬼灭之刃的特效是真厉害！', 0, '2025-11-30 16:36:01');
INSERT INTO `subject_comments` VALUES (58, 99, 57, '期待第二季的打斗场面。', 0, '2025-11-30 16:36:02');
INSERT INTO `subject_comments` VALUES (59, 100, 58, '艾尔敏帅爆了！', 0, '2025-11-30 16:36:03');
INSERT INTO `subject_comments` VALUES (60, 101, 59, '结局虽然悲壮，但很震撼。', 0, '2025-11-30 16:36:04');
INSERT INTO `subject_comments` VALUES (61, 102, 60, '阿尼亚太可爱了！', 0, '2025-11-30 16:36:05');
INSERT INTO `subject_comments` VALUES (62, 103, 61, '电锯人就是一出黑色幽默。', 0, '2025-11-30 16:36:06');
INSERT INTO `subject_comments` VALUES (63, 104, 62, '后藤一里真的是社恐天花板。', 0, '2025-11-30 16:36:07');
INSERT INTO `subject_comments` VALUES (64, 105, 63, '千束和泷奈的互动太甜了。', 0, '2025-11-30 16:36:08');
INSERT INTO `subject_comments` VALUES (65, 106, 64, '剧情发展越来越悬疑了。', 0, '2025-11-30 16:36:09');
INSERT INTO `subject_comments` VALUES (66, 107, 65, '看完感觉心灵都被治愈了。', 0, '2025-11-30 16:36:10');
INSERT INTO `subject_comments` VALUES (67, 108, 66, '打斗虽然血腥，但很有美感。', 0, '2025-11-30 16:36:11');
INSERT INTO `subject_comments` VALUES (68, 109, 67, '托尔芬的成长令人动容。', 0, '2025-11-30 16:36:12');
INSERT INTO `subject_comments` VALUES (69, 110, 68, '猫猫的探案能力简直了！', 0, '2025-11-30 16:36:13');
INSERT INTO `subject_comments` VALUES (70, 111, 69, '渋谷事变篇封神！', 0, '2025-11-30 16:36:14');
INSERT INTO `subject_comments` VALUES (71, 112, 70, '鲁迪加油啊！', 0, '2025-11-30 16:36:15');
INSERT INTO `subject_comments` VALUES (73, 160, 5, '111111111111\n', 1, '2025-12-04 16:20:09');
INSERT INTO `subject_comments` VALUES (74, 144, 5, '111', 0, '2025-12-05 15:50:54');
INSERT INTO `subject_comments` VALUES (81, 145, 1, '测试', 1, '2026-04-08 17:12:55');
INSERT INTO `subject_comments` VALUES (83, 145, 1, '222333', 0, '2026-04-08 20:40:29');

-- ----------------------------
-- Table structure for subjects
-- ----------------------------
DROP TABLE IF EXISTS `subjects`;
CREATE TABLE `subjects`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `cn_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '中文名',
  `jp_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '日文名',
  `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '别名',
  `first_air_date` date NULL DEFAULT NULL COMMENT '首播日期',
  `episodes` int(0) NULL DEFAULT NULL COMMENT '话数',
  `studio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '制作公司',
  `country` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '国家/地区',
  `official_site` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '官网',
  `category` enum('TV','OVA','MOVIE','MANGA') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签',
  `updated_date` datetime(0) NOT NULL COMMENT '最后修改时间',
  `cover_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面路径',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 169 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '条目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of subjects
-- ----------------------------
INSERT INTO `subjects` VALUES (98, '鬼灭之刃', '鬼滅の刃', 'Demon Slayer', '2019-04-06', 26, 'ufotable', '日本', 'https://kimetsu.com/', 'TV', '热血,奇幻', '2025-12-01 00:36:09', '/uploads/covers/鬼灭之刃.jpg');
INSERT INTO `subjects` VALUES (99, '咒术回战', '呪術廻戦', 'Jujutsu Kaisen', '2020-10-03', 24, 'MAPPA', '日本', 'https://jujutsukaisen.jp/', 'TV', '热血,战斗', '2025-12-01 00:36:24', '/uploads/covers/咒术回战.jpg');
INSERT INTO `subjects` VALUES (100, '进击的巨人 最终季', '進撃の巨人 The Final Season', 'Attack on Titan Final Season', '2020-12-07', 16, 'MAPPA', '日本', 'https://shingeki.tv/final/', 'TV', '热血,剧情', '2025-12-01 00:36:37', '/uploads/covers/进击的巨人_最终季.jpg');
INSERT INTO `subjects` VALUES (101, '进击的巨人 最终季 完结篇', '進撃の巨人 The Final Season 完結編', 'AoT The Final Chapters', '2023-03-04', 2, 'MAPPA', '日本', 'https://shingeki.tv/final/', 'TV', '热血,剧情', '2025-12-01 00:36:33', '/uploads/covers/进击的巨人_最终季_完结篇.jpg');
INSERT INTO `subjects` VALUES (102, '间谍过家家', 'SPY×FAMILY', 'Spy x Family', '2022-04-09', 25, 'WIT STUDIO・CloverWorks', '日本', 'https://spy-family.net/', 'TV', '喜剧,家庭', '2025-12-01 00:36:54', '/uploads/covers/间谍过家家.jpg');
INSERT INTO `subjects` VALUES (103, '链锯人', 'チェンソーマン', 'Chainsaw Man', '2022-10-12', 12, 'MAPPA', '日本', 'https://chainsawman.dog/', 'TV', '热血,奇幻', '2025-12-01 00:37:10', '/uploads/covers/链锯人.jpg');
INSERT INTO `subjects` VALUES (104, '孤独摇滚！', 'ぼっち・ざ・ろっく！', 'Bocchi the Rock!', '2022-10-09', 12, 'CloverWorks', '日本', 'https://bocchi.rocks/', 'TV', '音乐,校园', '2025-12-01 00:31:34', '/uploads/covers/孤独摇滚！.jpg');
INSERT INTO `subjects` VALUES (105, '莉可丽丝', 'リコリス・リコイル', 'Lycoris Recoil', '2022-07-02', 13, 'A-1 Pictures', '日本', 'https://lycoris-recoil.com/', 'TV', '动作,原创', '2025-12-01 00:31:18', '/uploads/covers/莉可丽丝.jpg');
INSERT INTO `subjects` VALUES (106, '我推的孩子', '【推しの子】', 'Oshi no Ko', '2023-04-12', 11, '動画工房', '日本', 'https://ichigoproduction.com/', 'TV', '偶像,悬疑', '2025-12-01 00:31:02', '/uploads/covers/我推的孩子.jpg');
INSERT INTO `subjects` VALUES (107, '葬送的芙莉莲', '葬送のフリーレン', 'Frieren', '2023-09-29', 28, 'Madhouse', '日本', 'https://frieren-anime.jp/', 'TV', '奇幻,冒险', '2025-12-01 09:19:20', '/uploads/covers/葬送的芙莉莲.jpg');
INSERT INTO `subjects` VALUES (108, '地狱乐', '地獄楽', 'Hell’s Paradise', '2023-04-01', 13, 'MAPPA', '日本', 'https://jigokuraku.com/', 'TV', '奇幻,战斗', '2025-12-01 00:29:55', '/uploads/covers/地狱乐.jpg');
INSERT INTO `subjects` VALUES (109, '冰海战记 第二季', 'ヴィンランド・サガ SEASON 2', 'Vinland Saga S2', '2023-01-09', 24, 'MAPPA', '日本', 'https://vinlandsaga.jp/', 'TV', '历史,冒险', '2025-12-01 00:29:32', '/uploads/covers/冰海战记_第二季.jpg');
INSERT INTO `subjects` VALUES (110, '药屋少女的呢喃', '薬屋のひとりごと', 'The Apothecary Diaries', '2023-10-21', 24, 'TOHO animation・OLM', '日本', 'https://kusuriyanohitorigoto.jp/', 'TV', '悬疑,古风', '2025-12-01 00:29:13', '/uploads/covers/药屋少女的呢喃.jpg');
INSERT INTO `subjects` VALUES (111, '咒术回战 第二季', '呪術廻戦 第2期', 'Jujutsu Kaisen S2', '2023-07-06', 23, 'MAPPA', '日本', 'https://jujutsukaisen.jp/', 'TV', '热血,战斗', '2025-12-01 00:28:45', '/uploads/covers/咒术回战_第二季.jpg');
INSERT INTO `subjects` VALUES (112, '无职转生 第二季', '無職転生 II', 'Mushoku Tensei S2', '2023-07-02', 25, 'Studio Bind', '日本', 'https://mushokutensei.jp/', 'TV', '奇幻,异世界', '2025-12-01 00:28:10', '/uploads/covers/无职转生_第二季.jpg');
INSERT INTO `subjects` VALUES (113, '国王排名', '王様ランキング', 'Ranking of Kings', '2021-10-15', 23, 'WIT STUDIO', '日本', 'https://osama-ranking.com/', 'TV', '奇幻,成长', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (114, '86—不存在的战区—', '86-エイティシックス-', '86', '2021-04-10', 23, 'A-1 Pictures', '日本', 'https://anime-86.com/', 'TV', '科幻,战争', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (115, '堀与宫村', 'ホリミヤ', 'Horimiya', '2021-01-10', 13, 'CloverWorks', '日本', 'https://horimiya-anime.com/', 'TV', '恋爱,校园', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (116, '重生之道', '東京リベンジャーズ', 'Tokyo Revengers', '2021-04-11', 24, 'LIDENFILMS', '日本', 'https://tokyo-revengers-anime.com/', 'TV', '热血,穿越', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (117, '古见同学有交流障碍症', '古見さんは、コミュ症です。', 'Komi-san', '2021-10-07', 12, 'OLM', '日本', 'https://komisan-official.com/', 'TV', '校园,喜剧', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (118, '鬼灭之刃 游郭篇', '鬼滅の刃 遊郭編', 'Demon Slayer Entertainment District', '2021-12-05', 11, 'ufotable', '日本', 'https://kimetsu.com/', 'TV', '热血,奇幻', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (119, '咒术回战 第0季剧场导入篇电视版', '呪術廻戦 0 TV', 'JJK 0 TV', '2022-09-18', 1, 'MAPPA', '日本', 'https://jujutsukaisen.jp/', 'TV', '热血,战斗', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (120, '致不灭的你', '不滅のあなたへ', 'To Your Eternity', '2021-04-12', 20, 'Brain’s Base', '日本', 'https://anime-fumetsunoanatae.com/', 'TV', '奇幻,感动', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (121, '来自深渊 烈日的黄金乡', 'メイドインアビス 烈日の黄金郷', 'Made in Abyss S2', '2022-07-06', 12, 'キネマシトラス', '日本', 'https://miabyss.com/', 'TV', '冒险,奇幻', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (122, '辉夜大小姐想让我告白 第三季', 'かぐや様は告らせたい -Ultra Romantic-', 'Kaguya-sama S3', '2022-04-09', 13, 'A-1 Pictures', '日本', 'https://kaguya.love/', 'TV', '喜剧,恋爱', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (124, '电锯人2 预留', 'チェンソーマン2', 'Chainsaw Man 2', '2025-01-01', 12, 'MAPPA', '日本', 'https://chainsawman.dog/', 'TV', '热血,奇幻', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (125, '海盗战记 第一季', 'ヴィンランド・サガ', 'Vinland Saga S1', '2019-07-07', 24, 'WIT STUDIO', '日本', 'https://vinlandsaga.jp/', 'TV', '历史,冒险', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (126, '来自深渊', 'メイドインアビス', 'Made in Abyss', '2017-07-07', 13, 'キネマシトラス', '日本', 'https://miabyss.com/', 'TV', '冒险,奇幻', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (127, '紫罗兰永恒花园', 'ヴァイオレット・エヴァーガーデン', 'Violet Evergarden', '2018-01-10', 13, '京都アニメーション', '日本', 'https://violet-evergarden.jp/', 'TV', '治愈,剧情', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (128, '一拳超人', 'ワンパンマン', 'One Punch Man', '2015-10-05', 12, 'Madhouse', '日本', 'https://onepunchman-anime.net/', 'TV', '热血,喜剧', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (129, 'Re：从零开始的异世界生活', 'Re：ゼロから始める異世界生活', 'Re:Zero', '2016-04-04', 25, 'WHITE FOX', '日本', 'https://re-zero-anime.jp/', 'TV', '异世界,奇幻', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (130, '灵能百分百 II', 'モブサイコ100 II', 'Mob Psycho 100 II', '2019-01-07', 13, 'Bones', '日本', 'http://mobpsycho100.com/', 'TV', '热血,超能力', '2025-11-25 21:11:16', '/uploads/covers/default.jpg');
INSERT INTO `subjects` VALUES (132, '石纪元', 'Dr.STONE', 'Dr. Stone', '2019-07-05', 24, 'TMS Entertainment', '日本', 'https://dr-stone.jp/', 'TV', '科幻,冒险', '2025-11-27 20:04:48', '/uploads/covers/石纪元.jpg');
INSERT INTO `subjects` VALUES (133, '赛博朋克：边缘行者', 'Cyberpunk: Edgerunners', 'Edgerunners', '2022-09-13', 10, 'TRIGGER', '日本', 'https://www.cyberpunk.net/', 'TV', '科幻,动作', '2025-11-27 20:02:52', '/uploads/covers/赛博朋克：边缘行者.jpg');
INSERT INTO `subjects` VALUES (137, '我推的孩子 第二季', '【推しの子】2', 'Oshi no Ko S2', '2023-07-03', 12, '動画工房', '日本', 'https://ichigoproduction.com/', 'TV', '偶像,悬疑', '2025-11-27 23:10:43', '/uploads/covers/我推的孩子_第二季.jpg');
INSERT INTO `subjects` VALUES (139, '更衣人偶坠入爱河', 'その着せ替え人形は恋をする', 'My Dress-Up Darling', '2022-01-09', 12, 'CloverWorks', '日本', 'https://bisque-doll-anime.com/', 'TV', '恋爱,校园', '2025-11-27 23:07:24', '/uploads/covers/更衣人偶坠入爱河.jpg');
INSERT INTO `subjects` VALUES (140, '更衣人偶坠入爱河 第二季', '着せ恋2', 'My Dress-Up Darling S2', '2025-01-01', 12, 'CloverWorks', '日本', 'https://bisque-doll-anime.com/', 'TV', '恋爱,校园', '2025-11-27 20:55:14', '/uploads/covers/更衣人偶坠入爱河_第二季.jpg');
INSERT INTO `subjects` VALUES (141, '迷宫饭', 'ダンジョン飯', 'Delicious in Dungeon', '2024-01-04', 24, 'TRIGGER', '日本', 'https://deliciousindungeon.jp/', 'TV', '奇幻,美食', '2025-11-25 22:23:59', '/uploads/covers/迷宫饭.jpg');
INSERT INTO `subjects` VALUES (142, '怪兽8号', '怪獣８号', 'Kaiju No.8', '2024-04-13', 12, 'Production I.G', '日本', 'https://kaiju-no8.net/', 'TV', '科幻,战斗', '2025-11-25 22:23:22', '/uploads/covers/怪兽8号.jpg');
INSERT INTO `subjects` VALUES (143, '想要成为影之实力者！', '陰の実力者になりたくて！', 'The Eminence in Shadow', '2022-10-05', 21, 'Nexus', '日本', 'https://shadow-garden.jp/', 'TV', '奇幻,搞笑', '2025-11-29 20:09:33', '/uploads/covers/想要成为影之实力者！.jpg');
INSERT INTO `subjects` VALUES (144, '新世纪福音战士', '新世紀エヴァンゲリオン', 'EVA', '1994-10-04', 24, 'Khara', '日本', 'https://evangelion.co.jp/', 'TV', '机甲,心理', '2025-11-27 23:10:06', '/uploads/covers/新世纪福音战士.jpg');
INSERT INTO `subjects` VALUES (145, '命运石之门', 'シュタインズ・ゲート', 'STEINS;GATE', '2011-04-06', 28, 'WHITE FOX', '日本', 'https://steinsgate.tv/', 'TV', '科幻,悬疑,游戏改', '2025-12-04 16:21:48', '/uploads/covers/命运石之门.jpg');
INSERT INTO `subjects` VALUES (146, '浪客剑心', 'るろうに剣心', 'Rurouni Kenshin', '2023-07-06', 24, 'LIDENFILMS', '日本', 'https://rurouni-kenshin.com/', 'TV', '武侠,经典', '2025-11-27 20:55:44', '/uploads/covers/浪客剑心.jpg');
INSERT INTO `subjects` VALUES (147, '关于我转生变成史莱姆这档事 第三季', '転生したらスライムだった件 第3期', 'Slime S3', '2024-04-05', 24, '8bit', '日本', 'https://ten-sura.com/', 'TV', '异世界,奇幻', '2025-11-25 22:11:45', '/uploads/covers/关于我转生变成史莱姆这档事_第三季.jpg');
INSERT INTO `subjects` VALUES (148, '盾之勇者成名录 第二季', '盾の勇者の成り上がり Season 2', 'Shield Hero S2', '2022-04-06', 13, 'Kinema Citrus', '日本', 'https://shieldhero-anime.jp/', 'TV', '异世界,战斗', '2025-11-25 22:02:57', '/uploads/covers/盾之勇者成名录_第二季.jpg');
INSERT INTO `subjects` VALUES (156, '功夫熊猫', 'Kung Fu Panda', 'KFP', '2008-06-06', 1, 'DreamWorks Animation', '美国', 'https://www.dreamworks.com/kung-fu-panda', 'MOVIE', '喜剧, 动作, 电影', '2025-11-30 19:09:56', '/uploads/covers/功夫熊猫.jpg');
INSERT INTO `subjects` VALUES (157, '罗小黑战记', 'The Legend of Hei', '罗小黑', '2019-09-12', 1, '寒木春华', '中国', 'http://luoxiaohei.com/', 'MOVIE', '奇幻, 治愈, 电影', '2025-11-30 19:09:52', '/uploads/covers/罗小黑战记.jpg');
INSERT INTO `subjects` VALUES (158, '瑞克和莫蒂', 'Rick and Morty', 'R&M', '2013-12-02', 71, 'Adult Swim', '美国', 'https://www.adultswim.com/videos/rick-and-morty', 'TV', '科幻, 喜剧, 成人动画', '2025-11-30 19:09:44', '/uploads/covers/瑞克和莫蒂.jpg');
INSERT INTO `subjects` VALUES (159, '时光代理人', 'Link Click', 'Shi Guang Dai Li Ren', '2021-04-30', 12, 'bilibili', '中国', 'https://www.bilibili.com/bangumi/media/md28229892', 'TV', '奇幻, 悬疑, 国产动画', '2026-04-08 18:30:59', '/uploads/covers/时光代理人.jpg');
INSERT INTO `subjects` VALUES (160, '蜘蛛侠：平行宇宙', 'Spider-Man: Into the Spider-Verse', '小黑蛛', '2018-12-14', 1, 'Sony Pictures Animation', '美国', 'https://www.intothespiderverse.movie/', 'MOVIE', '超级英雄, 动作, 电影', '2026-04-07 17:19:38', '/uploads/covers/蜘蛛侠：平行宇宙.jpg');
INSERT INTO `subjects` VALUES (166, '新增测试1', '新增测试', 'test', '2025-12-31', 14, 'test', 'test', 'test', 'TV', 'test', '2026-04-08 19:03:04', '/uploads/covers/default.jpg');

-- ----------------------------
-- Table structure for topics
-- ----------------------------
DROP TABLE IF EXISTS `topics`;
CREATE TABLE `topics`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subject_id` bigint(0) NOT NULL COMMENT '条目ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
  `status` enum('OPEN','LOCKED','REJECTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'LOCKED' COMMENT '状态',
  `reply_count` int(0) NOT NULL DEFAULT 0 COMMENT '回复数',
  `created_at` datetime(0) NOT NULL COMMENT '创建时间',
  `updated_at` datetime(0) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_topics_subject`(`subject_id`) USING BTREE,
  INDEX `fk_topics_user`(`user_id`) USING BTREE,
  CONSTRAINT `fk_topics_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_topics_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '话题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of topics
-- ----------------------------
INSERT INTO `topics` VALUES (3, 148, 1, '删除话题测试', '删除话题测试', 'OPEN', 1, '2025-11-28 17:34:51', '2025-11-30 14:16:11');
INSERT INTO `topics` VALUES (5, 147, 1, '新增话题测试', '新增话题测试', 'OPEN', 0, '2025-11-28 17:44:33', '2025-11-28 17:44:33');
INSERT INTO `topics` VALUES (7, 141, 5, '用户发布话题测试', '用户发布话题测试', 'OPEN', 4, '2025-11-28 21:02:14', '2025-12-05 23:18:24');
INSERT INTO `topics` VALUES (28, 98, 56, '鬼灭之刃为什么能这么火？', '剧情、制作、音乐都无可挑剔，但真的有那么神吗？', 'OPEN', 1, '2025-11-30 16:38:01', '2025-11-30 16:38:01');
INSERT INTO `topics` VALUES (29, 99, 57, '你最喜欢的咒术回战角色是哪个？', '是五条悟还是虎杖悠仁？', 'OPEN', 1, '2025-11-30 16:38:02', '2025-11-30 16:38:02');
INSERT INTO `topics` VALUES (30, 100, 58, '最终季的制作质量如何？', 'MAPPA接手后大家感觉怎么样？', 'OPEN', 1, '2025-11-30 16:38:03', '2025-11-30 16:38:03');
INSERT INTO `topics` VALUES (31, 101, 59, '你对艾伦的结局有什么看法？', '无论如何，艾伦是自由的！', 'OPEN', 1, '2025-11-30 16:38:04', '2025-11-30 16:38:04');
INSERT INTO `topics` VALUES (32, 102, 60, '约尔的打斗戏份是不是少了点？', '荆棘公主什么时候能再大显身手？', 'OPEN', 1, '2025-11-30 16:38:05', '2025-11-30 16:38:05');
INSERT INTO `topics` VALUES (33, 103, 61, '波奇到底会不会社交牛逼症？', '好奇波奇有没有可能完全治好社恐？', 'OPEN', 1, '2025-11-30 16:38:06', '2025-11-30 16:38:06');
INSERT INTO `topics` VALUES (34, 104, 62, '吉他英雄的真面目！', '波奇是吉他英雄这件事什么时候会被发现？', 'OPEN', 1, '2025-11-30 16:38:07', '2025-11-30 16:38:07');
INSERT INTO `topics` VALUES (35, 105, 63, '千束和泷奈的CP名是什么？', '你们更喜欢叫她们什么？', 'OPEN', 1, '2025-11-30 16:38:08', '2025-11-30 16:38:08');
INSERT INTO `topics` VALUES (36, 106, 64, '谁是真正的幕后黑手？', '除了神木光，还有其他人吗？', 'OPEN', 1, '2025-11-30 16:38:09', '2025-11-30 16:38:09');
INSERT INTO `topics` VALUES (37, 107, 65, '芙莉莲还会活多少年？', '期待她最终找到生命的意义。', 'OPEN', 1, '2025-11-30 16:38:10', '2025-11-30 16:38:10');
INSERT INTO `topics` VALUES (38, 108, 66, '画眉丸的爱妻之心太感人了', '这对夫妻的感情线真的很棒！', 'OPEN', 1, '2025-11-30 16:38:11', '2025-11-30 16:38:11');
INSERT INTO `topics` VALUES (39, 109, 67, '第二季的托尔芬太窝囊了？', '很多人说他没有第一季帅气了，你怎么看？', 'OPEN', 1, '2025-11-30 16:38:12', '2025-11-30 16:38:12');
INSERT INTO `topics` VALUES (40, 110, 68, '药屋少女的男主什么时候表白？', '壬氏大人什么时候能告白成功？', 'OPEN', 1, '2025-11-30 16:38:13', '2025-11-30 16:38:13');
INSERT INTO `topics` VALUES (41, 111, 69, '五条悟会复活吗？', '漫画最新话太刀了！', 'OPEN', 1, '2025-11-30 16:38:14', '2025-11-30 16:38:14');
INSERT INTO `topics` VALUES (42, 112, 70, '第二季的进度是不是太慢了？', '希望加快节奏，期待下一季。', 'OPEN', 1, '2025-11-30 16:38:15', '2025-11-30 16:38:15');
INSERT INTO `topics` VALUES (43, 160, 5, 'sile', '12', 'OPEN', 0, '2025-12-04 16:23:20', '2025-12-04 16:23:20');
INSERT INTO `topics` VALUES (50, 156, 1, '审核测试', 'LOCKED', 'OPEN', 0, '2025-12-16 15:16:59', '2025-12-16 15:24:17');
INSERT INTO `topics` VALUES (51, 157, 1, '打回重新编辑测试', '打回，重新编辑', 'OPEN', 0, '2025-12-16 15:24:42', '2025-12-16 15:45:47');
INSERT INTO `topics` VALUES (52, 145, 1, 'ai调用测试', 'ai调用测试', 'OPEN', 0, '2025-12-16 17:31:59', '2025-12-16 17:32:02');
INSERT INTO `topics` VALUES (53, 132, 5, '用户发布审核测试', '用户发布审核测试111', 'OPEN', 6, '2025-12-18 21:07:24', '2025-12-21 18:51:59');
INSERT INTO `topics` VALUES (59, 159, 5, '1231', '312', 'OPEN', 0, '2025-12-23 18:58:07', '2025-12-23 18:58:19');
INSERT INTO `topics` VALUES (60, 157, 1, 'test', 'testtesttest', 'OPEN', 1, '2025-12-24 22:39:38', '2025-12-24 22:40:07');
INSERT INTO `topics` VALUES (61, 160, 1, '11', '111111', 'OPEN', 0, '2025-12-25 17:59:20', '2025-12-25 17:59:28');
INSERT INTO `topics` VALUES (62, 160, 1, '111', '11', 'LOCKED', 0, '2026-04-08 20:03:53', '2026-04-08 21:11:14');

-- ----------------------------
-- Table structure for user_activity
-- ----------------------------
DROP TABLE IF EXISTS `user_activity`;
CREATE TABLE `user_activity`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `subject_id` bigint(0) NULL DEFAULT NULL COMMENT '条目ID',
  `type` enum('STATUS','PROGRESS','RATING','COMMENT') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '动态类型',
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详情',
  `created_at` datetime(0) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_user_activity_user`(`user_id`) USING BTREE,
  INDEX `fk_user_activity_subject`(`subject_id`) USING BTREE,
  CONSTRAINT `fk_user_activity_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_user_activity_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 356 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户动态表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_activity
-- ----------------------------
INSERT INTO `user_activity` VALUES (1, 1, 148, 'STATUS', 'WATCHING', '2025-11-25 23:24:19');
INSERT INTO `user_activity` VALUES (2, 1, 148, 'PROGRESS', '12', '2025-11-25 23:24:19');
INSERT INTO `user_activity` VALUES (3, 1, 148, 'RATING', '6', '2025-11-25 23:24:19');
INSERT INTO `user_activity` VALUES (4, 1, 148, 'COMMENT', '还行，不算难看', '2025-11-25 23:24:19');
INSERT INTO `user_activity` VALUES (5, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 19:38:37');
INSERT INTO `user_activity` VALUES (6, 1, 148, 'PROGRESS', '9', '2025-11-26 19:38:37');
INSERT INTO `user_activity` VALUES (7, 1, 148, 'PROGRESS', '8', '2025-11-26 20:39:31');
INSERT INTO `user_activity` VALUES (8, 1, 148, 'STATUS', 'DROPPED', '2025-11-26 20:42:00');
INSERT INTO `user_activity` VALUES (9, 1, 148, 'PROGRESS', '13', '2025-11-26 20:42:00');
INSERT INTO `user_activity` VALUES (10, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 20:42:05');
INSERT INTO `user_activity` VALUES (11, 1, 148, 'PROGRESS', '9', '2025-11-26 20:42:05');
INSERT INTO `user_activity` VALUES (12, 1, 148, 'STATUS', 'WATCHING', '2025-11-26 20:42:05');
INSERT INTO `user_activity` VALUES (13, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 20:42:06');
INSERT INTO `user_activity` VALUES (14, 1, 148, 'STATUS', 'DROPPED', '2025-11-26 20:43:22');
INSERT INTO `user_activity` VALUES (15, 1, 148, 'PROGRESS', '13', '2025-11-26 20:43:22');
INSERT INTO `user_activity` VALUES (16, 1, 148, 'PROGRESS', '9', '2025-11-26 20:43:24');
INSERT INTO `user_activity` VALUES (17, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 20:43:26');
INSERT INTO `user_activity` VALUES (18, 1, 148, 'PROGRESS', '8', '2025-11-26 20:43:26');
INSERT INTO `user_activity` VALUES (19, 1, 148, 'STATUS', 'DROPPED', '2025-11-26 20:43:28');
INSERT INTO `user_activity` VALUES (20, 1, 148, 'PROGRESS', '10', '2025-11-26 20:43:28');
INSERT INTO `user_activity` VALUES (21, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 20:45:12');
INSERT INTO `user_activity` VALUES (22, 1, 148, 'PROGRESS', '9', '2025-11-26 20:45:12');
INSERT INTO `user_activity` VALUES (23, 1, 148, 'STATUS', 'WATCHING', '2025-11-26 20:45:14');
INSERT INTO `user_activity` VALUES (24, 1, 148, 'PROGRESS', '11', '2025-11-26 20:45:14');
INSERT INTO `user_activity` VALUES (25, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 20:45:17');
INSERT INTO `user_activity` VALUES (26, 1, 148, 'PROGRESS', '9', '2025-11-26 20:45:17');
INSERT INTO `user_activity` VALUES (27, 1, 148, 'STATUS', 'WATCHING', '2025-11-26 20:45:20');
INSERT INTO `user_activity` VALUES (28, 1, 148, 'PROGRESS', '8', '2025-11-26 20:45:25');
INSERT INTO `user_activity` VALUES (29, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 20:45:28');
INSERT INTO `user_activity` VALUES (30, 1, 148, 'PROGRESS', '7', '2025-11-26 20:45:28');
INSERT INTO `user_activity` VALUES (31, 1, 148, 'PROGRESS', '8', '2025-11-26 20:45:31');
INSERT INTO `user_activity` VALUES (32, 1, 148, 'PROGRESS', '9', '2025-11-26 20:45:38');
INSERT INTO `user_activity` VALUES (33, 1, 148, 'STATUS', 'WATCHING', '2025-11-26 20:45:38');
INSERT INTO `user_activity` VALUES (34, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 20:46:46');
INSERT INTO `user_activity` VALUES (35, 1, 148, 'STATUS', 'WATCHING', '2025-11-26 20:46:49');
INSERT INTO `user_activity` VALUES (36, 1, 148, 'PROGRESS', '12', '2025-11-26 20:46:49');
INSERT INTO `user_activity` VALUES (37, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 20:49:09');
INSERT INTO `user_activity` VALUES (38, 1, 148, 'STATUS', 'WATCHING', '2025-11-26 20:49:16');
INSERT INTO `user_activity` VALUES (39, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 20:49:23');
INSERT INTO `user_activity` VALUES (40, 1, 148, 'STATUS', 'WATCHING', '2025-11-26 20:49:24');
INSERT INTO `user_activity` VALUES (41, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 20:56:37');
INSERT INTO `user_activity` VALUES (42, 1, 148, 'PROGRESS', '8', '2025-11-26 20:56:37');
INSERT INTO `user_activity` VALUES (43, 1, 148, 'PROGRESS', '5', '2025-11-26 20:56:39');
INSERT INTO `user_activity` VALUES (44, 1, 148, 'STATUS', 'WATCHING', '2025-11-26 20:56:41');
INSERT INTO `user_activity` VALUES (45, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 20:56:49');
INSERT INTO `user_activity` VALUES (46, 1, 148, 'PROGRESS', '9', '2025-11-26 20:56:49');
INSERT INTO `user_activity` VALUES (47, 1, 148, 'STATUS', 'WATCHING', '2025-11-26 20:56:50');
INSERT INTO `user_activity` VALUES (48, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 20:56:56');
INSERT INTO `user_activity` VALUES (49, 1, 148, 'PROGRESS', '8', '2025-11-26 20:56:56');
INSERT INTO `user_activity` VALUES (50, 1, 148, 'PROGRESS', '10', '2025-11-26 20:56:58');
INSERT INTO `user_activity` VALUES (51, 1, 148, 'PROGRESS', '13', '2025-11-26 20:57:00');
INSERT INTO `user_activity` VALUES (52, 1, 148, 'PROGRESS', '7', '2025-11-26 20:57:02');
INSERT INTO `user_activity` VALUES (53, 1, 148, 'PROGRESS', '5', '2025-11-26 21:00:05');
INSERT INTO `user_activity` VALUES (54, 1, 148, 'STATUS', 'WATCHING', '2025-11-26 21:00:05');
INSERT INTO `user_activity` VALUES (55, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 21:00:16');
INSERT INTO `user_activity` VALUES (56, 1, 148, 'PROGRESS', '6', '2025-11-26 21:00:16');
INSERT INTO `user_activity` VALUES (57, 1, 148, 'PROGRESS', '7', '2025-11-26 21:07:18');
INSERT INTO `user_activity` VALUES (58, 1, 148, 'STATUS', 'WATCHING', '2025-11-26 21:07:21');
INSERT INTO `user_activity` VALUES (59, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 21:07:28');
INSERT INTO `user_activity` VALUES (60, 1, 148, 'PROGRESS', '6', '2025-11-26 21:07:28');
INSERT INTO `user_activity` VALUES (61, 1, 148, 'PROGRESS', '7', '2025-11-26 21:07:31');
INSERT INTO `user_activity` VALUES (62, 1, 148, 'STATUS', 'WATCHING', '2025-11-26 21:07:33');
INSERT INTO `user_activity` VALUES (63, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 21:07:34');
INSERT INTO `user_activity` VALUES (64, 1, 148, 'PROGRESS', '6', '2025-11-26 21:07:34');
INSERT INTO `user_activity` VALUES (65, 1, 148, 'STATUS', 'WATCHING', '2025-11-26 21:12:57');
INSERT INTO `user_activity` VALUES (66, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 21:13:55');
INSERT INTO `user_activity` VALUES (67, 1, 148, 'PROGRESS', '7', '2025-11-26 21:13:55');
INSERT INTO `user_activity` VALUES (68, 1, 148, 'STATUS', 'WATCHING', '2025-11-26 21:13:56');
INSERT INTO `user_activity` VALUES (69, 1, 148, 'PROGRESS', '8', '2025-11-26 21:13:56');
INSERT INTO `user_activity` VALUES (70, 1, 148, 'PROGRESS', '9', '2025-11-26 21:13:57');
INSERT INTO `user_activity` VALUES (71, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 21:19:32');
INSERT INTO `user_activity` VALUES (72, 1, 148, 'PROGRESS', '6', '2025-11-26 21:19:32');
INSERT INTO `user_activity` VALUES (73, 1, 148, 'PROGRESS', '8', '2025-11-26 21:19:34');
INSERT INTO `user_activity` VALUES (74, 1, 148, 'STATUS', 'WATCHING', '2025-11-26 21:19:55');
INSERT INTO `user_activity` VALUES (75, 1, 148, 'PROGRESS', '9', '2025-11-26 21:19:55');
INSERT INTO `user_activity` VALUES (76, 1, 148, 'STATUS', 'WATCHED', '2025-11-26 21:19:57');
INSERT INTO `user_activity` VALUES (77, 1, 147, 'STATUS', 'WATCHING', '2025-11-26 21:44:16');
INSERT INTO `user_activity` VALUES (78, 1, 147, 'PROGRESS', '1', '2025-11-26 21:44:16');
INSERT INTO `user_activity` VALUES (79, 1, 147, 'RATING', '1', '2025-11-26 21:44:16');
INSERT INTO `user_activity` VALUES (80, 1, 147, 'COMMENT', '11', '2025-11-26 21:44:16');
INSERT INTO `user_activity` VALUES (81, 1, 144, 'STATUS', 'WATCHED', '2025-11-26 23:36:06');
INSERT INTO `user_activity` VALUES (82, 1, 144, 'PROGRESS', '13', '2025-11-26 23:36:06');
INSERT INTO `user_activity` VALUES (83, 1, 144, 'COMMENT', '神中神', '2025-11-26 23:36:06');
INSERT INTO `user_activity` VALUES (84, 1, 144, 'STATUS', 'WATCHING', '2025-11-26 23:36:08');
INSERT INTO `user_activity` VALUES (85, 1, 144, 'RATING', '10', '2025-11-26 23:36:12');
INSERT INTO `user_activity` VALUES (86, 1, 144, 'STATUS', 'WATCHING', '2025-11-26 23:38:28');
INSERT INTO `user_activity` VALUES (87, 1, 144, 'PROGRESS', '0', '2025-11-26 23:38:28');
INSERT INTO `user_activity` VALUES (88, 1, 144, 'RATING', '5', '2025-11-26 23:38:28');
INSERT INTO `user_activity` VALUES (89, 1, 144, 'COMMENT', 'test', '2025-11-26 23:38:28');
INSERT INTO `user_activity` VALUES (90, 1, 144, 'PROGRESS', '14', '2025-11-26 23:39:59');
INSERT INTO `user_activity` VALUES (91, 1, 144, 'RATING', '10', '2025-11-26 23:40:06');
INSERT INTO `user_activity` VALUES (92, 1, 144, 'COMMENT', 'test11', '2025-11-26 23:40:06');
INSERT INTO `user_activity` VALUES (93, 1, 144, 'STATUS', 'WATCHED', '2025-11-27 09:42:30');
INSERT INTO `user_activity` VALUES (94, 1, 144, 'PROGRESS', '9', '2025-11-27 09:42:30');
INSERT INTO `user_activity` VALUES (95, 1, 145, 'STATUS', 'WATCHED', '2025-11-27 17:51:11');
INSERT INTO `user_activity` VALUES (96, 1, 145, 'PROGRESS', '12', '2025-11-27 17:51:11');
INSERT INTO `user_activity` VALUES (97, 1, 145, 'RATING', '10', '2025-11-27 17:51:11');
INSERT INTO `user_activity` VALUES (98, 1, 145, 'COMMENT', 'test22222', '2025-11-27 17:51:11');
INSERT INTO `user_activity` VALUES (99, 1, 145, 'PROGRESS', '24', '2025-11-27 17:51:38');
INSERT INTO `user_activity` VALUES (100, 1, 145, 'PROGRESS', '21', '2025-11-27 17:51:41');
INSERT INTO `user_activity` VALUES (101, 1, 145, 'PROGRESS', '22', '2025-11-27 17:51:43');
INSERT INTO `user_activity` VALUES (102, 1, 145, 'STATUS', 'WATCHING', '2025-11-27 17:51:45');
INSERT INTO `user_activity` VALUES (103, 1, 145, 'PROGRESS', '23', '2025-11-27 17:51:45');
INSERT INTO `user_activity` VALUES (104, 1, 145, 'STATUS', 'WATCHED', '2025-11-27 17:51:55');
INSERT INTO `user_activity` VALUES (105, 1, 145, 'PROGRESS', '22', '2025-11-27 17:51:55');
INSERT INTO `user_activity` VALUES (106, 1, 145, 'STATUS', 'WATCHING', '2025-11-27 17:52:02');
INSERT INTO `user_activity` VALUES (107, 1, 145, 'STATUS', 'WATCHED', '2025-11-27 17:52:07');
INSERT INTO `user_activity` VALUES (108, 1, 145, 'PROGRESS', '21', '2025-11-27 17:52:07');
INSERT INTO `user_activity` VALUES (109, 1, 144, 'STATUS', 'WATCHED', '2025-11-27 22:25:47');
INSERT INTO `user_activity` VALUES (110, 1, 144, 'PROGRESS', '9', '2025-11-27 22:25:47');
INSERT INTO `user_activity` VALUES (111, 1, 144, 'RATING', '9', '2025-11-27 22:25:47');
INSERT INTO `user_activity` VALUES (112, 1, 144, 'COMMENT', 'test11', '2025-11-27 22:25:47');
INSERT INTO `user_activity` VALUES (116, 1, 139, 'STATUS', 'WATCHED', '2025-11-27 23:28:52');
INSERT INTO `user_activity` VALUES (117, 1, 139, 'PROGRESS', '0', '2025-11-27 23:28:52');
INSERT INTO `user_activity` VALUES (118, 1, 139, 'RATING', '8', '2025-11-27 23:28:52');
INSERT INTO `user_activity` VALUES (119, 1, 139, 'COMMENT', '不赖', '2025-11-27 23:28:52');
INSERT INTO `user_activity` VALUES (120, 1, 139, 'PROGRESS', '7', '2025-11-28 09:59:45');
INSERT INTO `user_activity` VALUES (127, 5, 145, 'STATUS', 'WATCHING', '2025-11-28 21:04:46');
INSERT INTO `user_activity` VALUES (128, 5, 144, 'STATUS', 'WATCHING', '2025-11-28 22:34:59');
INSERT INTO `user_activity` VALUES (129, 5, 145, 'STATUS', 'WATCHED', '2025-11-28 22:39:15');
INSERT INTO `user_activity` VALUES (130, 5, 145, 'PROGRESS', '12', '2025-11-28 22:39:15');
INSERT INTO `user_activity` VALUES (131, 5, 145, 'RATING', '10', '2025-11-28 22:39:15');
INSERT INTO `user_activity` VALUES (132, 5, 145, 'COMMENT', 'test', '2025-11-28 22:39:15');
INSERT INTO `user_activity` VALUES (137, 1, 145, 'STATUS', 'WATCHING', '2025-11-30 14:22:02');
INSERT INTO `user_activity` VALUES (138, 1, 145, 'PROGRESS', '21', '2025-11-30 14:22:02');
INSERT INTO `user_activity` VALUES (139, 1, 145, 'RATING', '10', '2025-11-30 14:22:02');
INSERT INTO `user_activity` VALUES (140, 1, 145, 'COMMENT', 'test22222', '2025-11-30 14:22:02');
INSERT INTO `user_activity` VALUES (141, 1, 145, 'STATUS', 'WATCHED', '2025-11-30 14:22:03');
INSERT INTO `user_activity` VALUES (142, 1, 145, 'PROGRESS', '20', '2025-11-30 14:22:03');
INSERT INTO `user_activity` VALUES (143, 1, 145, 'STATUS', 'WATCHING', '2025-11-30 14:22:04');
INSERT INTO `user_activity` VALUES (144, 1, 145, 'STATUS', 'WATCHED', '2025-11-30 14:22:05');
INSERT INTO `user_activity` VALUES (145, 56, 98, 'STATUS', 'WATCHED', '2025-11-30 16:35:01');
INSERT INTO `user_activity` VALUES (146, 56, 98, 'PROGRESS', '26', '2025-11-30 16:35:01');
INSERT INTO `user_activity` VALUES (147, 56, 98, 'RATING', '9', '2025-11-30 16:35:01');
INSERT INTO `user_activity` VALUES (148, 56, 98, 'COMMENT', '年度神作', '2025-11-30 16:35:01');
INSERT INTO `user_activity` VALUES (149, 57, 99, 'STATUS', 'WATCHING', '2025-11-30 16:35:02');
INSERT INTO `user_activity` VALUES (150, 57, 99, 'PROGRESS', '10', '2025-11-30 16:35:02');
INSERT INTO `user_activity` VALUES (151, 57, 99, 'RATING', '8', '2025-11-30 16:35:02');
INSERT INTO `user_activity` VALUES (152, 57, 99, 'COMMENT', '值得期待', '2025-11-30 16:35:02');
INSERT INTO `user_activity` VALUES (153, 58, 100, 'STATUS', 'WATCHED', '2025-11-30 16:35:03');
INSERT INTO `user_activity` VALUES (154, 58, 100, 'PROGRESS', '16', '2025-11-30 16:35:03');
INSERT INTO `user_activity` VALUES (155, 58, 100, 'RATING', '10', '2025-11-30 16:35:03');
INSERT INTO `user_activity` VALUES (156, 58, 100, 'COMMENT', '献出心脏！', '2025-11-30 16:35:03');
INSERT INTO `user_activity` VALUES (157, 59, 101, 'STATUS', 'DROPPED', '2025-11-30 16:35:04');
INSERT INTO `user_activity` VALUES (158, 59, 101, 'PROGRESS', '1', '2025-11-30 16:35:04');
INSERT INTO `user_activity` VALUES (159, 59, 101, 'RATING', NULL, '2025-11-30 16:35:04');
INSERT INTO `user_activity` VALUES (160, 60, 102, 'STATUS', 'WATCHING', '2025-11-30 16:35:05');
INSERT INTO `user_activity` VALUES (161, 60, 102, 'PROGRESS', '5', '2025-11-30 16:35:05');
INSERT INTO `user_activity` VALUES (162, 60, 102, 'RATING', '7', '2025-11-30 16:35:05');
INSERT INTO `user_activity` VALUES (163, 60, 102, 'COMMENT', '日常系很放松', '2025-11-30 16:35:05');
INSERT INTO `user_activity` VALUES (164, 61, 103, 'STATUS', 'WATCHED', '2025-11-30 16:35:06');
INSERT INTO `user_activity` VALUES (165, 61, 103, 'PROGRESS', '12', '2025-11-30 16:35:06');
INSERT INTO `user_activity` VALUES (166, 61, 103, 'RATING', '8', '2025-11-30 16:35:06');
INSERT INTO `user_activity` VALUES (167, 61, 103, 'COMMENT', '打戏超赞', '2025-11-30 16:35:06');
INSERT INTO `user_activity` VALUES (168, 62, 104, 'STATUS', 'WATCHED', '2025-11-30 16:35:07');
INSERT INTO `user_activity` VALUES (169, 62, 104, 'PROGRESS', '12', '2025-11-30 16:35:07');
INSERT INTO `user_activity` VALUES (170, 62, 104, 'RATING', '10', '2025-11-30 16:35:07');
INSERT INTO `user_activity` VALUES (171, 62, 104, 'COMMENT', '孤独摇滚YYDS', '2025-11-30 16:35:07');
INSERT INTO `user_activity` VALUES (172, 63, 105, 'STATUS', 'WATCHING', '2025-11-30 16:35:08');
INSERT INTO `user_activity` VALUES (173, 63, 105, 'PROGRESS', '3', '2025-11-30 16:35:08');
INSERT INTO `user_activity` VALUES (174, 63, 105, 'RATING', '9', '2025-11-30 16:35:08');
INSERT INTO `user_activity` VALUES (175, 63, 105, 'COMMENT', '原创佳作', '2025-11-30 16:35:08');
INSERT INTO `user_activity` VALUES (176, 64, 106, 'STATUS', 'WATCHED', '2025-11-30 16:35:09');
INSERT INTO `user_activity` VALUES (177, 64, 106, 'PROGRESS', '11', '2025-11-30 16:35:09');
INSERT INTO `user_activity` VALUES (178, 64, 106, 'RATING', '7', '2025-11-30 16:35:09');
INSERT INTO `user_activity` VALUES (179, 64, 106, 'COMMENT', '题材新颖', '2025-11-30 16:35:09');
INSERT INTO `user_activity` VALUES (180, 65, 107, 'STATUS', 'WATCHING', '2025-11-30 16:35:10');
INSERT INTO `user_activity` VALUES (181, 65, 107, 'PROGRESS', '8', '2025-11-30 16:35:10');
INSERT INTO `user_activity` VALUES (182, 65, 107, 'RATING', '9', '2025-11-30 16:35:10');
INSERT INTO `user_activity` VALUES (183, 65, 107, 'COMMENT', '治愈系神作', '2025-11-30 16:35:10');
INSERT INTO `user_activity` VALUES (184, 66, 108, 'STATUS', 'WATCHED', '2025-11-30 16:35:11');
INSERT INTO `user_activity` VALUES (185, 66, 108, 'PROGRESS', '13', '2025-11-30 16:35:11');
INSERT INTO `user_activity` VALUES (186, 66, 108, 'RATING', '8', '2025-11-30 16:35:11');
INSERT INTO `user_activity` VALUES (187, 66, 108, 'COMMENT', '水墨画风很独特', '2025-11-30 16:35:11');
INSERT INTO `user_activity` VALUES (188, 67, 109, 'STATUS', 'DROPPED', '2025-11-30 16:35:12');
INSERT INTO `user_activity` VALUES (189, 67, 109, 'PROGRESS', '5', '2025-11-30 16:35:12');
INSERT INTO `user_activity` VALUES (190, 67, 109, 'RATING', '6', '2025-11-30 16:35:12');
INSERT INTO `user_activity` VALUES (191, 67, 109, 'COMMENT', '看不懂维京历史', '2025-11-30 16:35:12');
INSERT INTO `user_activity` VALUES (192, 68, 110, 'STATUS', 'WATCHING', '2025-11-30 16:35:13');
INSERT INTO `user_activity` VALUES (193, 68, 110, 'PROGRESS', '15', '2025-11-30 16:35:13');
INSERT INTO `user_activity` VALUES (194, 68, 110, 'RATING', '9', '2025-11-30 16:35:13');
INSERT INTO `user_activity` VALUES (195, 68, 110, 'COMMENT', '女主智商在线', '2025-11-30 16:35:13');
INSERT INTO `user_activity` VALUES (196, 69, 111, 'STATUS', 'WATCHED', '2025-11-30 16:35:14');
INSERT INTO `user_activity` VALUES (197, 69, 111, 'PROGRESS', '23', '2025-11-30 16:35:14');
INSERT INTO `user_activity` VALUES (198, 69, 111, 'RATING', '10', '2025-11-30 16:35:14');
INSERT INTO `user_activity` VALUES (199, 70, 112, 'STATUS', 'WATCHING', '2025-11-30 16:35:15');
INSERT INTO `user_activity` VALUES (200, 70, 112, 'PROGRESS', '4', '2025-11-30 16:35:15');
INSERT INTO `user_activity` VALUES (201, 70, 112, 'RATING', '7', '2025-11-30 16:35:15');
INSERT INTO `user_activity` VALUES (202, 70, 112, 'COMMENT', '异世界穿越', '2025-11-30 16:35:15');
INSERT INTO `user_activity` VALUES (203, 63, 98, 'STATUS', 'WATCHED', '2025-11-30 17:15:01');
INSERT INTO `user_activity` VALUES (204, 64, 98, 'STATUS', 'WATCHED', '2025-11-30 17:15:02');
INSERT INTO `user_activity` VALUES (205, 65, 98, 'STATUS', 'WATCHED', '2025-11-30 17:15:03');
INSERT INTO `user_activity` VALUES (206, 66, 98, 'STATUS', 'WATCHED', '2025-11-30 17:15:04');
INSERT INTO `user_activity` VALUES (207, 67, 98, 'STATUS', 'WATCHED', '2025-11-30 17:15:05');
INSERT INTO `user_activity` VALUES (208, 68, 98, 'STATUS', 'WATCHED', '2025-11-30 17:15:06');
INSERT INTO `user_activity` VALUES (209, 69, 98, 'STATUS', 'WATCHED', '2025-11-30 17:15:07');
INSERT INTO `user_activity` VALUES (210, 63, 99, 'STATUS', 'WATCHED', '2025-11-30 17:15:10');
INSERT INTO `user_activity` VALUES (211, 64, 99, 'STATUS', 'WATCHED', '2025-11-30 17:15:11');
INSERT INTO `user_activity` VALUES (212, 65, 99, 'STATUS', 'WATCHED', '2025-11-30 17:15:12');
INSERT INTO `user_activity` VALUES (213, 66, 99, 'STATUS', 'WATCHED', '2025-11-30 17:15:13');
INSERT INTO `user_activity` VALUES (214, 67, 99, 'STATUS', 'WATCHED', '2025-11-30 17:15:14');
INSERT INTO `user_activity` VALUES (215, 68, 99, 'STATUS', 'WATCHED', '2025-11-30 17:15:15');
INSERT INTO `user_activity` VALUES (216, 69, 99, 'STATUS', 'WATCHED', '2025-11-30 17:15:16');
INSERT INTO `user_activity` VALUES (217, 56, 145, 'STATUS', 'WATCHED', '2025-11-30 17:15:20');
INSERT INTO `user_activity` VALUES (218, 57, 145, 'STATUS', 'WATCHED', '2025-11-30 17:15:21');
INSERT INTO `user_activity` VALUES (219, 58, 145, 'STATUS', 'WATCHED', '2025-11-30 17:15:22');
INSERT INTO `user_activity` VALUES (220, 59, 145, 'STATUS', 'WATCHED', '2025-11-30 17:15:23');
INSERT INTO `user_activity` VALUES (221, 60, 145, 'STATUS', 'WATCHED', '2025-11-30 17:15:24');
INSERT INTO `user_activity` VALUES (222, 61, 145, 'STATUS', 'WATCHED', '2025-11-30 17:15:25');
INSERT INTO `user_activity` VALUES (223, 62, 145, 'STATUS', 'WATCHED', '2025-11-30 17:15:26');
INSERT INTO `user_activity` VALUES (224, 1, 160, 'STATUS', 'WATCHING', '2025-12-04 15:06:40');
INSERT INTO `user_activity` VALUES (225, 1, 160, 'PROGRESS', '2', '2025-12-04 15:06:40');
INSERT INTO `user_activity` VALUES (226, 1, 160, 'RATING', '8', '2025-12-04 15:06:40');
INSERT INTO `user_activity` VALUES (227, 1, 160, 'COMMENT', 'test', '2025-12-04 15:06:40');
INSERT INTO `user_activity` VALUES (228, 5, 145, 'PROGRESS', '18', '2025-12-04 16:20:47');
INSERT INTO `user_activity` VALUES (229, 5, 145, 'PROGRESS', '20', '2025-12-04 16:20:50');
INSERT INTO `user_activity` VALUES (230, 5, 145, 'PROGRESS', '11', '2025-12-04 16:20:52');
INSERT INTO `user_activity` VALUES (231, 5, 144, 'STATUS', 'WATCHED', '2025-12-05 15:50:39');
INSERT INTO `user_activity` VALUES (232, 5, 144, 'PROGRESS', '14', '2025-12-05 15:50:39');
INSERT INTO `user_activity` VALUES (234, 5, 144, 'RATING', '5', '2025-12-05 16:06:25');
INSERT INTO `user_activity` VALUES (235, 5, 144, 'COMMENT', '111', '2025-12-05 16:06:25');
INSERT INTO `user_activity` VALUES (250, 1, 156, 'STATUS', 'WATCHING', '2025-12-09 12:25:39');
INSERT INTO `user_activity` VALUES (251, 1, 156, 'PROGRESS', '0', '2025-12-09 12:25:39');
INSERT INTO `user_activity` VALUES (252, 1, 156, 'RATING', '3', '2025-12-09 12:25:39');
INSERT INTO `user_activity` VALUES (255, 1, 156, 'RATING', '8', '2025-12-21 18:06:56');
INSERT INTO `user_activity` VALUES (256, 5, 160, 'STATUS', 'WATCHED', '2025-12-21 18:50:46');
INSERT INTO `user_activity` VALUES (257, 5, 160, 'PROGRESS', '1', '2025-12-21 18:50:46');
INSERT INTO `user_activity` VALUES (258, 5, 160, 'RATING', '8', '2025-12-21 18:50:46');
INSERT INTO `user_activity` VALUES (259, 5, 160, 'COMMENT', '还行', '2025-12-21 18:50:57');
INSERT INTO `user_activity` VALUES (260, 1, 148, 'STATUS', 'DROPPED', '2025-12-21 19:07:27');
INSERT INTO `user_activity` VALUES (261, 1, 148, 'PROGRESS', '9', '2025-12-21 19:07:27');
INSERT INTO `user_activity` VALUES (262, 1, 148, 'RATING', '6', '2025-12-21 19:07:27');
INSERT INTO `user_activity` VALUES (263, 1, 148, 'COMMENT', '还行，不算难看', '2025-12-21 19:07:27');
INSERT INTO `user_activity` VALUES (268, 1, 166, 'STATUS', 'WATCHING', '2025-12-23 18:52:46');
INSERT INTO `user_activity` VALUES (270, 1, 166, 'RATING', '5', '2025-12-23 18:52:46');
INSERT INTO `user_activity` VALUES (271, 1, 166, 'COMMENT', '1312', '2025-12-23 18:52:46');
INSERT INTO `user_activity` VALUES (272, 1, 145, 'STATUS', 'WATCHED', '2025-12-23 18:55:44');
INSERT INTO `user_activity` VALUES (273, 1, 145, 'PROGRESS', '24', '2025-12-23 18:55:44');
INSERT INTO `user_activity` VALUES (274, 1, 145, 'RATING', '10', '2025-12-23 18:55:44');
INSERT INTO `user_activity` VALUES (275, 1, 145, 'COMMENT', 'test22222', '2025-12-23 18:55:44');
INSERT INTO `user_activity` VALUES (276, 1, 145, 'PROGRESS', '13', '2025-12-23 18:55:45');
INSERT INTO `user_activity` VALUES (277, 1, 145, 'STATUS', 'WATCHING', '2025-12-23 18:55:47');
INSERT INTO `user_activity` VALUES (278, 1, 145, 'PROGRESS', '26', '2025-12-23 18:55:47');
INSERT INTO `user_activity` VALUES (279, 1, 145, 'STATUS', 'WATCHED', '2025-12-23 20:21:25');
INSERT INTO `user_activity` VALUES (280, 1, 145, 'PROGRESS', '16', '2025-12-23 20:21:25');
INSERT INTO `user_activity` VALUES (285, 1, 157, 'STATUS', 'WATCHED', '2025-12-24 21:15:41');
INSERT INTO `user_activity` VALUES (286, 1, 157, 'PROGRESS', '9', '2025-12-24 21:15:41');
INSERT INTO `user_activity` VALUES (287, 1, 157, 'RATING', '10', '2025-12-24 21:15:41');
INSERT INTO `user_activity` VALUES (288, 1, 157, 'COMMENT', 'test', '2025-12-24 21:15:41');
INSERT INTO `user_activity` VALUES (289, 1, 157, 'STATUS', 'WATCHING', '2025-12-24 21:15:48');
INSERT INTO `user_activity` VALUES (290, 1, 157, 'STATUS', 'WATCHED', '2025-12-24 21:15:52');
INSERT INTO `user_activity` VALUES (291, 1, 157, 'PROGRESS', '1', '2025-12-24 21:15:52');
INSERT INTO `user_activity` VALUES (292, 1, 145, 'STATUS', 'WATCHED', '2025-12-24 21:15:59');
INSERT INTO `user_activity` VALUES (293, 1, 145, 'PROGRESS', '5', '2025-12-24 21:15:59');
INSERT INTO `user_activity` VALUES (294, 1, 145, 'RATING', '10', '2025-12-24 21:15:59');
INSERT INTO `user_activity` VALUES (295, 1, 145, 'COMMENT', 'test22222', '2025-12-24 21:15:59');
INSERT INTO `user_activity` VALUES (296, 1, 145, 'PROGRESS', '16', '2025-12-24 21:16:01');
INSERT INTO `user_activity` VALUES (297, 1, 145, 'STATUS', 'WATCHING', '2025-12-24 21:16:02');
INSERT INTO `user_activity` VALUES (298, 1, 145, 'PROGRESS', '17', '2025-12-24 21:16:02');
INSERT INTO `user_activity` VALUES (299, 1, 145, 'RATING', '9', '2025-12-24 21:16:21');
INSERT INTO `user_activity` VALUES (300, 1, 166, 'STATUS', 'WATCHING', '2025-12-24 21:54:32');
INSERT INTO `user_activity` VALUES (301, 1, 166, 'PROGRESS', '3', '2025-12-24 21:54:32');
INSERT INTO `user_activity` VALUES (302, 1, 166, 'RATING', '6', '2025-12-24 21:54:32');
INSERT INTO `user_activity` VALUES (303, 1, 166, 'COMMENT', '1312', '2025-12-24 21:54:32');
INSERT INTO `user_activity` VALUES (304, 1, 166, 'RATING', '7', '2025-12-24 21:55:12');
INSERT INTO `user_activity` VALUES (307, 1, 145, 'RATING', '10', '2025-12-24 22:49:19');
INSERT INTO `user_activity` VALUES (308, 1, 145, 'STATUS', 'WATCHED', '2025-12-25 15:51:21');
INSERT INTO `user_activity` VALUES (309, 1, 145, 'PROGRESS', '18', '2025-12-25 15:51:21');
INSERT INTO `user_activity` VALUES (310, 1, 145, 'STATUS', 'WATCHING', '2025-12-25 15:51:32');
INSERT INTO `user_activity` VALUES (311, 1, 145, 'PROGRESS', '19', '2025-12-25 15:51:32');
INSERT INTO `user_activity` VALUES (312, 1, 145, 'PROGRESS', '20', '2025-12-25 16:03:22');
INSERT INTO `user_activity` VALUES (313, 1, 166, 'STATUS', 'WATCHED', '2025-12-25 17:53:30');
INSERT INTO `user_activity` VALUES (314, 1, 166, 'PROGRESS', '2', '2025-12-25 17:53:30');
INSERT INTO `user_activity` VALUES (315, 1, 166, 'RATING', '2', '2025-12-25 17:53:30');
INSERT INTO `user_activity` VALUES (316, 1, 166, 'COMMENT', '111', '2025-12-25 17:53:30');
INSERT INTO `user_activity` VALUES (317, 1, 145, 'STATUS', 'WATCHED', '2025-12-25 17:55:11');
INSERT INTO `user_activity` VALUES (318, 1, 145, 'PROGRESS', '15', '2025-12-25 17:55:11');
INSERT INTO `user_activity` VALUES (319, 1, 145, 'RATING', '10', '2025-12-25 17:55:11');
INSERT INTO `user_activity` VALUES (320, 1, 145, 'COMMENT', 'test22222', '2025-12-25 17:55:11');
INSERT INTO `user_activity` VALUES (321, 1, 145, 'PROGRESS', '12', '2025-12-25 17:58:31');
INSERT INTO `user_activity` VALUES (322, 1, 145, 'PROGRESS', '11', '2025-12-25 17:58:40');
INSERT INTO `user_activity` VALUES (323, 1, 166, 'STATUS', 'WATCHED', '2026-04-07 17:18:57');
INSERT INTO `user_activity` VALUES (325, 1, 166, 'RATING', '2', '2026-04-07 17:18:57');
INSERT INTO `user_activity` VALUES (326, 1, 166, 'COMMENT', '111', '2026-04-07 17:18:57');
INSERT INTO `user_activity` VALUES (327, 1, 166, 'PROGRESS', '0', '2026-04-07 17:38:40');
INSERT INTO `user_activity` VALUES (328, 1, 148, 'STATUS', 'DROPPED', '2026-04-07 17:39:46');
INSERT INTO `user_activity` VALUES (329, 1, 148, 'PROGRESS', '0', '2026-04-07 17:39:46');
INSERT INTO `user_activity` VALUES (330, 1, 148, 'RATING', '6', '2026-04-07 17:39:46');
INSERT INTO `user_activity` VALUES (331, 1, 148, 'COMMENT', '还行，不算难看', '2026-04-07 17:39:46');
INSERT INTO `user_activity` VALUES (332, 60, 145, 'STATUS', 'WATCHED', '2026-04-08 16:23:41');
INSERT INTO `user_activity` VALUES (333, 60, 145, 'PROGRESS', '7', '2026-04-08 16:23:41');
INSERT INTO `user_activity` VALUES (334, 60, 145, 'STATUS', 'WATCHING', '2026-04-08 16:23:45');
INSERT INTO `user_activity` VALUES (335, 60, 145, 'PROGRESS', '9', '2026-04-08 16:23:45');
INSERT INTO `user_activity` VALUES (336, 60, 145, 'PROGRESS', '10', '2026-04-08 16:24:03');
INSERT INTO `user_activity` VALUES (337, 60, 148, 'STATUS', 'WATCHING', '2026-04-08 16:32:21');
INSERT INTO `user_activity` VALUES (338, 60, 148, 'PROGRESS', '5', '2026-04-08 16:32:21');
INSERT INTO `user_activity` VALUES (339, 60, 148, 'COMMENT', '', '2026-04-08 16:32:21');
INSERT INTO `user_activity` VALUES (340, 1, 145, 'STATUS', 'WATCHED', '2026-04-08 20:36:07');
INSERT INTO `user_activity` VALUES (341, 1, 145, 'PROGRESS', '28', '2026-04-08 20:36:07');
INSERT INTO `user_activity` VALUES (342, 1, 145, 'RATING', '10', '2026-04-08 20:36:07');
INSERT INTO `user_activity` VALUES (343, 1, 145, 'COMMENT', 'test22222', '2026-04-08 20:36:07');
INSERT INTO `user_activity` VALUES (344, 1, 139, 'STATUS', 'WATCHING', '2026-04-08 20:44:22');
INSERT INTO `user_activity` VALUES (345, 1, 139, 'PROGRESS', '8', '2026-04-08 20:44:22');
INSERT INTO `user_activity` VALUES (346, 1, 139, 'RATING', '8', '2026-04-08 20:44:22');
INSERT INTO `user_activity` VALUES (347, 1, 139, 'COMMENT', '不赖', '2026-04-08 20:44:22');
INSERT INTO `user_activity` VALUES (349, 1, 139, 'RATING', '9', '2026-04-08 20:49:14');
INSERT INTO `user_activity` VALUES (350, 1, 139, 'STATUS', 'WATCHED', '2026-04-08 20:49:32');
INSERT INTO `user_activity` VALUES (351, 1, 139, 'COMMENT', '挺好', '2026-04-08 20:50:50');
INSERT INTO `user_activity` VALUES (352, 1, 145, 'STATUS', 'WATCHING', '2026-04-09 20:37:21');
INSERT INTO `user_activity` VALUES (353, 1, 145, 'STATUS', 'WATCHED', '2026-04-09 20:37:27');
INSERT INTO `user_activity` VALUES (354, 1, 145, 'PROGRESS', '26', '2026-04-09 20:37:27');
INSERT INTO `user_activity` VALUES (355, 1, 145, 'STATUS', 'WATCHING', '2026-05-06 01:30:16');
INSERT INTO `user_activity` VALUES (356, 1, 145, 'PROGRESS', '27', '2026-05-06 01:30:16');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password_hash` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码哈希',
  `nickname` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像路径',
  `role` enum('USER','ADMIN') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'USER' COMMENT '角色',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '状态',
  `created_at` datetime(0) NOT NULL COMMENT '创建时间',
  `signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个性签名',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '$2a$10$G09dtFNDnQz.es0.ghuIFOogweVFAKh.m/Y0Quofx27e4zfOIJ2pC', '牧濑红莉栖', '/uploads/avatars/1.png', 'ADMIN', 1, '2025-11-24 23:51:03', '签名修改测试');
INSERT INTO `users` VALUES (5, 'test', '$2a$10$P0lJwamTcVmjg4B4GDbXrOdNsXd9uA/OXRjeO3pheWipWqG0RGX3i', 'test', '/uploads/avatars/5.jpg', 'USER', 1, '2025-11-25 00:27:56', '11111');
INSERT INTO `users` VALUES (56, 'u1', '$2a$10$8NaF3ntQb9YYgno5/5rcXu.uM4.cdOcAwAaOaGMDiwXYeFL3.aM9S', '柠檬精本精', '/uploads/avatars/default.jpg', 'USER', 1, '2025-11-30 16:24:05', NULL);
INSERT INTO `users` VALUES (57, 'u2', '$2a$10$0ioHTrOxLg1ffPhMUws.aOnIA0bLbiw5T6WTNQYN.h7kuaNyDJUs2', '路人甲', '/uploads/avatars/default.jpg', 'USER', 1, '2025-11-30 16:25:04', NULL);
INSERT INTO `users` VALUES (58, 'u3', '$2a$10$7nunw3U2NVkiXKCnOB.pGewJFsQYaQnMzZPhtACD/.4B6QRH6AXMC', '白月光战士', '/uploads/avatars/default.jpg', 'USER', 1, '2025-11-30 16:25:42', NULL);
INSERT INTO `users` VALUES (59, 'u4', '$2a$10$KeVsJLWL1l5PAknxBNzlLuYMd0cvY8qIGIIl4bDX5oHAvvDEldNJ2', '摸鱼怪', '/uploads/avatars/default.jpg', 'USER', 1, '2025-11-30 16:26:14', NULL);
INSERT INTO `users` VALUES (60, 'u5', '$2a$10$MHsl5GOFSCA7mj9.4hJV.e82k.3NyhZm5fa87HhSWkOWWHhOKPH2.', '二楼已抢', '/uploads/avatars/default.jpg', 'USER', 1, '2025-11-30 16:26:29', NULL);
INSERT INTO `users` VALUES (61, 'u6', '$2a$10$ejJJWe4WN.cLEsnxFm9QfO0FV1K4lwLPbHkGydenxhaJiBxx6VYNy', '电波系少女', '/uploads/avatars/default.jpg', 'USER', 1, '2025-11-30 16:26:44', NULL);
INSERT INTO `users` VALUES (62, 'u7', '$2a$10$m29EDnQIXuFwmmm462I5r.W6kPFXVmssXe5lt8nnwXLUUdATpJu9u', '十年老二次元', '/uploads/avatars/default.jpg', 'USER', 1, '2025-11-30 16:27:15', NULL);
INSERT INTO `users` VALUES (63, 'u8', '$2a$10$1beUg//VkF6K8B6i65dVHuYa8RLah8XTqMvhfxwu0XRIZkBp9X7M2', '巨人厨', '/uploads/avatars/default.jpg', 'USER', 1, '2025-11-30 16:27:29', NULL);
INSERT INTO `users` VALUES (64, 'u9', '$2a$10$vQrXzaxinfAgQAWblrEOIu5IupoHnkNr7xxpMrrq.aRhvzACJ3m2m', '贫穷狗', '/uploads/avatars/default.jpg', 'USER', 1, '2025-11-30 16:27:42', NULL);
INSERT INTO `users` VALUES (65, 'u10', '$2a$10$A3rBvCfJhIRDn78nwirnm.9UmrlLMhnZHz3e7RpKHDxb4I61SzhaS', '今晚不熬夜了', '/uploads/avatars/default.jpg', 'USER', 1, '2025-11-30 16:27:54', NULL);
INSERT INTO `users` VALUES (66, 'u11', '$2a$10$vYQIcRTdVZ77rhbFq0mG9OJzzpsHN9kbtQY2CntS9Qrtpc3QgYfgq', '瓜子吃瓜群众', '/uploads/avatars/default.jpg', 'USER', 1, '2025-11-30 16:28:13', NULL);
INSERT INTO `users` VALUES (67, 'u12', '$2a$10$b6Q90wY2eFV2iv7bkYHvN.sRZx1HQkE7mF6vAo99Vx4EAEoCs1nHy', '谜之微笑', '/uploads/avatars/default.jpg', 'USER', 1, '2025-11-30 16:28:27', NULL);
INSERT INTO `users` VALUES (68, 'u13', '$2a$10$YBYjeq8g2twBBaYTxLFn/u28X94ld3vRnGIev7ljt69psWITMMIMm', '爽文爱好者', '/uploads/avatars/default.jpg', 'USER', 1, '2025-11-30 16:28:38', NULL);
INSERT INTO `users` VALUES (69, 'u14', '$2a$10$IVcry4E/BhWzKJX6gCLr.uD40Sdl.B6Ct2vk.kEDiM21iXwkG7nz6', '罗小黑的猫薄荷', '/uploads/avatars/default.jpg', 'USER', 1, '2025-11-30 16:28:52', NULL);
INSERT INTO `users` VALUES (70, 'u15', '$2a$10$KcK0EreIskX9f8G4DilydekMQWG0GW7wABoaQS6TUOa2UVkiIZlE2', 'Coser在赶制', '/uploads/avatars/default.jpg', 'USER', 1, '2025-11-30 16:29:03', NULL);
INSERT INTO `users` VALUES (72, 'test2', '$2a$10$tRBbyUhvnlpDqV3d3uLz8eYL2D74pnNMFawQgPBwlfggZEMbvmgBm', '测试用户2', '/uploads/avatars/default.jpg', 'USER', 1, '2025-12-02 10:57:49', NULL);

SET FOREIGN_KEY_CHECKS = 1;
