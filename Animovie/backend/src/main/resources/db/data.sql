-- MySQL dump 10.13  Distrib 8.0.12, for Win64 (x86_64)
--
-- Host: localhost    Database: bgm
-- ------------------------------------------------------
-- Server version	9.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8mb4 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `collections`
--

/*!40000 ALTER TABLE `collections` DISABLE KEYS */;
INSERT INTO `collections` VALUES (1,1,148,'WATCHED',9,6,'还行，不算难看','2025-11-26 21:19:57'),(4,1,144,'WATCHED',9,9,'test11','2025-11-27 22:25:47'),(5,1,145,'WATCHED',21,10,'test22222','2025-11-27 17:52:07'),(9,1,139,'WATCHING',7,8,'不赖','2025-11-28 09:59:48'),(10,5,136,'WATCHED',12,8,'好看','2025-11-28 21:01:40'),(12,5,145,'WATCHED',12,10,'test','2025-11-28 22:39:15');
/*!40000 ALTER TABLE `collections` ENABLE KEYS */;

--
-- Dumping data for table `comment_likes`
--

/*!40000 ALTER TABLE `comment_likes` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment_likes` ENABLE KEYS */;

--
-- Dumping data for table `comments`
--

/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;

--
-- Dumping data for table `subject_comment_likes`
--

/*!40000 ALTER TABLE `subject_comment_likes` DISABLE KEYS */;
INSERT INTO `subject_comment_likes` VALUES (4,3,1,'2025-11-27 17:51:32'),(15,3,5,'2025-11-28 22:58:47');
/*!40000 ALTER TABLE `subject_comment_likes` ENABLE KEYS */;

--
-- Dumping data for table `subject_comments`
--

/*!40000 ALTER TABLE `subject_comments` DISABLE KEYS */;
INSERT INTO `subject_comments` VALUES (1,144,1,'评论test',0,'2025-11-26 23:40:28'),(2,144,1,'test2',0,'2025-11-26 23:44:35'),(3,145,1,'测试测试222',2,'2025-11-27 17:51:31'),(5,145,5,'111',0,'2025-11-29 15:45:45');
/*!40000 ALTER TABLE `subject_comments` ENABLE KEYS */;

--
-- Dumping data for table `subjects`
--

/*!40000 ALTER TABLE `subjects` DISABLE KEYS */;
INSERT INTO `subjects` VALUES (98,'鬼灭之刃','鬼滅の刃','Demon Slayer','2019-04-06',26,'ufotable','日本','https://kimetsu.com/','TV','热血,奇幻','2025-11-25 21:11:16',NULL),(99,'咒术回战','呪術廻戦','Jujutsu Kaisen','2020-10-03',24,'MAPPA','日本','https://jujutsukaisen.jp/','TV','热血,战斗','2025-11-25 21:11:16',NULL),(100,'进击的巨人 最终季','進撃の巨人 The Final Season','Attack on Titan Final Season','2020-12-07',16,'MAPPA','日本','https://shingeki.tv/final/','TV','热血,剧情','2025-11-25 21:11:16',NULL),(101,'进击的巨人 最终季 完结篇','進撃の巨人 The Final Season 完結編','AoT The Final Chapters','2023-03-04',2,'MAPPA','日本','https://shingeki.tv/final/','TV','热血,剧情','2025-11-25 21:11:16',NULL),(102,'间谍过家家','SPY×FAMILY','Spy x Family','2022-04-09',25,'WIT STUDIO・CloverWorks','日本','https://spy-family.net/','TV','喜剧,家庭','2025-11-25 21:11:16',NULL),(103,'链锯人','チェンソーマン','Chainsaw Man','2022-10-12',12,'MAPPA','日本','https://chainsawman.dog/','TV','热血,奇幻','2025-11-25 21:11:16',NULL),(104,'孤独摇滚！','ぼっち・ざ・ろっく！','Bocchi the Rock!','2022-10-09',12,'CloverWorks','日本','https://bocchi.rocks/','TV','音乐,校园','2025-11-25 21:11:16',NULL),(105,'莉可丽丝','リコリス・リコイル','Lycoris Recoil','2022-07-02',13,'A-1 Pictures','日本','https://lycoris-recoil.com/','TV','动作,原创','2025-11-25 21:11:16',NULL),(106,'我推的孩子','【推しの子】','Oshi no Ko','2023-04-12',11,'動画工房','日本','https://ichigoproduction.com/','TV','偶像,悬疑','2025-11-25 21:11:16',NULL),(107,'葬送的芙莉莲','葬送のフリーレン','Frieren','2023-09-29',28,'Madhouse','日本','https://frieren-anime.jp/','TV','奇幻,冒险','2025-11-25 21:11:16',NULL),(108,'地狱乐','地獄楽','Hell’s Paradise','2023-04-01',13,'MAPPA','日本','https://jigokuraku.com/','TV','奇幻,战斗','2025-11-25 21:11:16',NULL),(109,'冰海战记 第二季','ヴィンランド・サガ SEASON 2','Vinland Saga S2','2023-01-09',24,'MAPPA','日本','https://vinlandsaga.jp/','TV','历史,冒险','2025-11-25 21:11:16',NULL),(110,'药屋少女的呢喃','薬屋のひとりごと','The Apothecary Diaries','2023-10-21',24,'TOHO animation・OLM','日本','https://kusuriyanohitorigoto.jp/','TV','悬疑,古风','2025-11-25 21:11:16',NULL),(111,'咒术回战 第二季','呪術廻戦 第2期','Jujutsu Kaisen S2','2023-07-06',23,'MAPPA','日本','https://jujutsukaisen.jp/','TV','热血,战斗','2025-11-25 21:11:16',NULL),(112,'无职转生 第二季','無職転生 II','Mushoku Tensei S2','2023-07-02',25,'Studio Bind','日本','https://mushokutensei.jp/','TV','奇幻,异世界','2025-11-25 21:11:16',NULL),(113,'国王排名','王様ランキング','Ranking of Kings','2021-10-15',23,'WIT STUDIO','日本','https://osama-ranking.com/','TV','奇幻,成长','2025-11-25 21:11:16',NULL),(114,'86—不存在的战区—','86-エイティシックス-','86','2021-04-10',23,'A-1 Pictures','日本','https://anime-86.com/','TV','科幻,战争','2025-11-25 21:11:16',NULL),(115,'堀与宫村','ホリミヤ','Horimiya','2021-01-10',13,'CloverWorks','日本','https://horimiya-anime.com/','TV','恋爱,校园','2025-11-25 21:11:16',NULL),(116,'重生之道','東京リベンジャーズ','Tokyo Revengers','2021-04-11',24,'LIDENFILMS','日本','https://tokyo-revengers-anime.com/','TV','热血,穿越','2025-11-25 21:11:16',NULL),(117,'古见同学有交流障碍症','古見さんは、コミュ症です。','Komi-san','2021-10-07',12,'OLM','日本','https://komisan-official.com/','TV','校园,喜剧','2025-11-25 21:11:16',NULL),(118,'鬼灭之刃 游郭篇','鬼滅の刃 遊郭編','Demon Slayer Entertainment District','2021-12-05',11,'ufotable','日本','https://kimetsu.com/','TV','热血,奇幻','2025-11-25 21:11:16',NULL),(119,'咒术回战 第0季剧场导入篇电视版','呪術廻戦 0 TV','JJK 0 TV','2022-09-18',1,'MAPPA','日本','https://jujutsukaisen.jp/','TV','热血,战斗','2025-11-25 21:11:16',NULL),(120,'致不灭的你','不滅のあなたへ','To Your Eternity','2021-04-12',20,'Brain’s Base','日本','https://anime-fumetsunoanatae.com/','TV','奇幻,感动','2025-11-25 21:11:16',NULL),(121,'来自深渊 烈日的黄金乡','メイドインアビス 烈日の黄金郷','Made in Abyss S2','2022-07-06',12,'キネマシトラス','日本','https://miabyss.com/','TV','冒险,奇幻','2025-11-25 21:11:16',NULL),(122,'辉夜大小姐想让我告白 第三季','かぐや様は告らせたい -Ultra Romantic-','Kaguya-sama S3','2022-04-09',13,'A-1 Pictures','日本','https://kaguya.love/','TV','喜剧,恋爱','2025-11-25 21:11:16',NULL),(123,'电锯人','チェンソーマン','Chainsaw Man','2022-10-12',12,'MAPPA','日本','https://chainsawman.dog/','TV','热血,奇幻','2025-11-25 21:11:16',NULL),(124,'电锯人2 预留','チェンソーマン2','Chainsaw Man 2','2025-01-01',12,'MAPPA','日本','https://chainsawman.dog/','TV','热血,奇幻','2025-11-25 21:11:16',NULL),(125,'海盗战记 第一季','ヴィンランド・サガ','Vinland Saga S1','2019-07-07',24,'WIT STUDIO','日本','https://vinlandsaga.jp/','TV','历史,冒险','2025-11-25 21:11:16',NULL),(126,'来自深渊','メイドインアビス','Made in Abyss','2017-07-07',13,'キネマシトラス','日本','https://miabyss.com/','TV','冒险,奇幻','2025-11-25 21:11:16',NULL),(127,'紫罗兰永恒花园','ヴァイオレット・エヴァーガーデン','Violet Evergarden','2018-01-10',13,'京都アニメーション','日本','https://violet-evergarden.jp/','TV','治愈,剧情','2025-11-25 21:11:16',NULL),(128,'一拳超人','ワンパンマン','One Punch Man','2015-10-05',12,'Madhouse','日本','https://onepunchman-anime.net/','TV','热血,喜剧','2025-11-25 21:11:16',NULL),(129,'Re：从零开始的异世界生活','Re：ゼロから始める異世界生活','Re:Zero','2016-04-04',25,'WHITE FOX','日本','https://re-zero-anime.jp/','TV','异世界,奇幻','2025-11-25 21:11:16',NULL),(130,'灵能百分百 II','モブサイコ100 II','Mob Psycho 100 II','2019-01-07',13,'Bones','日本','http://mobpsycho100.com/','TV','热血,超能力','2025-11-25 21:11:16',NULL),(132,'石纪元','Dr.STONE','Dr. Stone','2019-07-05',24,'TMS Entertainment','日本','https://dr-stone.jp/','TV','科幻,冒险','2025-11-27 20:04:48','/uploads/covers/石纪元.jpg'),(133,'赛博朋克：边缘行者','Cyberpunk: Edgerunners','Edgerunners','2022-09-13',10,'TRIGGER','日本','https://www.cyberpunk.net/','TV','科幻,动作','2025-11-27 20:02:52','/uploads/covers/赛博朋克：边缘行者.jpg'),(135,'孤独摇滚！','ぼざろ 特別','Bocchi Special','2024-01-01',12,'CloverWorks','日本','https://bocchi.rocks/','TV','音乐,校园','2025-11-27 23:11:15','/uploads/covers/孤独摇滚！.jpg'),(136,'葬送的芙莉莲','葬送のフリーレン','Frieren New','2023-09-29',12,'Madhouse','日本','https://frieren-anime.jp/','TV','奇幻,冒险','2025-11-27 23:10:24','/uploads/covers/葬送的芙莉莲.jpg'),(137,'我推的孩子 第二季','【推しの子】2','Oshi no Ko S2','2023-07-03',12,'動画工房','日本','https://ichigoproduction.com/','TV','偶像,悬疑','2025-11-27 23:10:43','/uploads/covers/我推的孩子_第二季.jpg'),(139,'更衣人偶坠入爱河','その着せ替え人形は恋をする','My Dress-Up Darling','2022-01-09',12,'CloverWorks','日本','https://bisque-doll-anime.com/','TV','恋爱,校园','2025-11-27 23:07:24','/uploads/covers/更衣人偶坠入爱河.jpg'),(140,'更衣人偶坠入爱河 第二季','着せ恋2','My Dress-Up Darling S2','2025-01-01',12,'CloverWorks','日本','https://bisque-doll-anime.com/','TV','恋爱,校园','2025-11-27 20:55:14','/uploads/covers/更衣人偶坠入爱河_第二季.jpg'),(141,'迷宫饭','ダンジョン飯','Delicious in Dungeon','2024-01-04',24,'TRIGGER','日本','https://deliciousindungeon.jp/','TV','奇幻,美食','2025-11-25 22:23:59','/uploads/covers/迷宫饭.jpg'),(142,'怪兽8号','怪獣８号','Kaiju No.8','2024-04-13',12,'Production I.G','日本','https://kaiju-no8.net/','TV','科幻,战斗','2025-11-25 22:23:22','/uploads/covers/怪兽8号.jpg'),(143,'想要成为影之实力者！','陰の実力者になりたくて！','The Eminence in Shadow','2022-10-05',21,'Nexus','日本','https://shadow-garden.jp/','TV','奇幻,搞笑','2025-11-29 20:09:33','/uploads/covers/想要成为影之实力者！.jpg'),(144,'新世纪福音战士','新世紀エヴァンゲリオン','EVA','1994-10-04',24,'Khara','日本','https://evangelion.co.jp/','TV','机甲,心理','2025-11-27 23:10:06','/uploads/covers/新世纪福音战士.jpg'),(145,'命运石之门','シュタインズ・ゲート','STEINS;GATE','2011-04-06',25,'WHITE FOX','日本','https://steinsgate.tv/','TV','科幻,悬疑,游戏改','2025-11-27 23:09:07','/uploads/covers/命运石之门.jpg'),(146,'浪客剑心','るろうに剣心','Rurouni Kenshin','2023-07-06',24,'LIDENFILMS','日本','https://rurouni-kenshin.com/','TV','武侠,经典','2025-11-27 20:55:44','/uploads/covers/浪客剑心.jpg'),(147,'关于我转生变成史莱姆这档事 第三季','転生したらスライムだった件 第3期','Slime S3','2024-04-05',24,'8bit','日本','https://ten-sura.com/','TV','异世界,奇幻','2025-11-25 22:11:45','/uploads/covers/关于我转生变成史莱姆这档事_第三季.jpg'),(148,'盾之勇者成名录 第二季','盾の勇者の成り上がり Season 2','Shield Hero S2','2022-04-06',13,'Kinema Citrus','日本','https://shieldhero-anime.jp/','TV','异世界,战斗','2025-11-25 22:02:57','/uploads/covers/盾之勇者成名录_第二季.jpg');
/*!40000 ALTER TABLE `subjects` ENABLE KEYS */;

--
-- Dumping data for table `topics`
--

/*!40000 ALTER TABLE `topics` DISABLE KEYS */;
INSERT INTO `topics` VALUES (3,148,1,'删除话题测试','删除话题测试','OPEN',0,'2025-11-28 17:34:51','2025-11-28 17:34:51'),(5,147,1,'新增话题测试','新增话题测试','OPEN',0,'2025-11-28 17:44:33','2025-11-28 17:44:33'),(7,141,5,'用户发布话题测试','用户发布话题测试','OPEN',0,'2025-11-28 21:02:14','2025-11-28 21:02:14');
/*!40000 ALTER TABLE `topics` ENABLE KEYS */;

--
-- Dumping data for table `user_activity`
--

/*!40000 ALTER TABLE `user_activity` DISABLE KEYS */;
INSERT INTO `user_activity` VALUES (1,1,148,'STATUS','WATCHING','2025-11-25 23:24:19'),(2,1,148,'PROGRESS','12','2025-11-25 23:24:19'),(3,1,148,'RATING','6','2025-11-25 23:24:19'),(4,1,148,'COMMENT','还行，不算难看','2025-11-25 23:24:19'),(5,1,148,'STATUS','WATCHED','2025-11-26 19:38:37'),(6,1,148,'PROGRESS','9','2025-11-26 19:38:37'),(7,1,148,'PROGRESS','8','2025-11-26 20:39:31'),(8,1,148,'STATUS','DROPPED','2025-11-26 20:42:00'),(9,1,148,'PROGRESS','13','2025-11-26 20:42:00'),(10,1,148,'STATUS','WATCHED','2025-11-26 20:42:05'),(11,1,148,'PROGRESS','9','2025-11-26 20:42:05'),(12,1,148,'STATUS','WATCHING','2025-11-26 20:42:05'),(13,1,148,'STATUS','WATCHED','2025-11-26 20:42:06'),(14,1,148,'STATUS','DROPPED','2025-11-26 20:43:22'),(15,1,148,'PROGRESS','13','2025-11-26 20:43:22'),(16,1,148,'PROGRESS','9','2025-11-26 20:43:24'),(17,1,148,'STATUS','WATCHED','2025-11-26 20:43:26'),(18,1,148,'PROGRESS','8','2025-11-26 20:43:26'),(19,1,148,'STATUS','DROPPED','2025-11-26 20:43:28'),(20,1,148,'PROGRESS','10','2025-11-26 20:43:28'),(21,1,148,'STATUS','WATCHED','2025-11-26 20:45:12'),(22,1,148,'PROGRESS','9','2025-11-26 20:45:12'),(23,1,148,'STATUS','WATCHING','2025-11-26 20:45:14'),(24,1,148,'PROGRESS','11','2025-11-26 20:45:14'),(25,1,148,'STATUS','WATCHED','2025-11-26 20:45:17'),(26,1,148,'PROGRESS','9','2025-11-26 20:45:17'),(27,1,148,'STATUS','WATCHING','2025-11-26 20:45:20'),(28,1,148,'PROGRESS','8','2025-11-26 20:45:25'),(29,1,148,'STATUS','WATCHED','2025-11-26 20:45:28'),(30,1,148,'PROGRESS','7','2025-11-26 20:45:28'),(31,1,148,'PROGRESS','8','2025-11-26 20:45:31'),(32,1,148,'PROGRESS','9','2025-11-26 20:45:38'),(33,1,148,'STATUS','WATCHING','2025-11-26 20:45:38'),(34,1,148,'STATUS','WATCHED','2025-11-26 20:46:46'),(35,1,148,'STATUS','WATCHING','2025-11-26 20:46:49'),(36,1,148,'PROGRESS','12','2025-11-26 20:46:49'),(37,1,148,'STATUS','WATCHED','2025-11-26 20:49:09'),(38,1,148,'STATUS','WATCHING','2025-11-26 20:49:16'),(39,1,148,'STATUS','WATCHED','2025-11-26 20:49:23'),(40,1,148,'STATUS','WATCHING','2025-11-26 20:49:24'),(41,1,148,'STATUS','WATCHED','2025-11-26 20:56:37'),(42,1,148,'PROGRESS','8','2025-11-26 20:56:37'),(43,1,148,'PROGRESS','5','2025-11-26 20:56:39'),(44,1,148,'STATUS','WATCHING','2025-11-26 20:56:41'),(45,1,148,'STATUS','WATCHED','2025-11-26 20:56:49'),(46,1,148,'PROGRESS','9','2025-11-26 20:56:49'),(47,1,148,'STATUS','WATCHING','2025-11-26 20:56:50'),(48,1,148,'STATUS','WATCHED','2025-11-26 20:56:56'),(49,1,148,'PROGRESS','8','2025-11-26 20:56:56'),(50,1,148,'PROGRESS','10','2025-11-26 20:56:58'),(51,1,148,'PROGRESS','13','2025-11-26 20:57:00'),(52,1,148,'PROGRESS','7','2025-11-26 20:57:02'),(53,1,148,'PROGRESS','5','2025-11-26 21:00:05'),(54,1,148,'STATUS','WATCHING','2025-11-26 21:00:05'),(55,1,148,'STATUS','WATCHED','2025-11-26 21:00:16'),(56,1,148,'PROGRESS','6','2025-11-26 21:00:16'),(57,1,148,'PROGRESS','7','2025-11-26 21:07:18'),(58,1,148,'STATUS','WATCHING','2025-11-26 21:07:21'),(59,1,148,'STATUS','WATCHED','2025-11-26 21:07:28'),(60,1,148,'PROGRESS','6','2025-11-26 21:07:28'),(61,1,148,'PROGRESS','7','2025-11-26 21:07:31'),(62,1,148,'STATUS','WATCHING','2025-11-26 21:07:33'),(63,1,148,'STATUS','WATCHED','2025-11-26 21:07:34'),(64,1,148,'PROGRESS','6','2025-11-26 21:07:34'),(65,1,148,'STATUS','WATCHING','2025-11-26 21:12:57'),(66,1,148,'STATUS','WATCHED','2025-11-26 21:13:55'),(67,1,148,'PROGRESS','7','2025-11-26 21:13:55'),(68,1,148,'STATUS','WATCHING','2025-11-26 21:13:56'),(69,1,148,'PROGRESS','8','2025-11-26 21:13:56'),(70,1,148,'PROGRESS','9','2025-11-26 21:13:57'),(71,1,148,'STATUS','WATCHED','2025-11-26 21:19:32'),(72,1,148,'PROGRESS','6','2025-11-26 21:19:32'),(73,1,148,'PROGRESS','8','2025-11-26 21:19:34'),(74,1,148,'STATUS','WATCHING','2025-11-26 21:19:55'),(75,1,148,'PROGRESS','9','2025-11-26 21:19:55'),(76,1,148,'STATUS','WATCHED','2025-11-26 21:19:57'),(77,1,147,'STATUS','WATCHING','2025-11-26 21:44:16'),(78,1,147,'PROGRESS','1','2025-11-26 21:44:16'),(79,1,147,'RATING','1','2025-11-26 21:44:16'),(80,1,147,'COMMENT','11','2025-11-26 21:44:16'),(81,1,144,'STATUS','WATCHED','2025-11-26 23:36:06'),(82,1,144,'PROGRESS','13','2025-11-26 23:36:06'),(83,1,144,'COMMENT','神中神','2025-11-26 23:36:06'),(84,1,144,'STATUS','WATCHING','2025-11-26 23:36:08'),(85,1,144,'RATING','10','2025-11-26 23:36:12'),(86,1,144,'STATUS','WATCHING','2025-11-26 23:38:28'),(87,1,144,'PROGRESS','0','2025-11-26 23:38:28'),(88,1,144,'RATING','5','2025-11-26 23:38:28'),(89,1,144,'COMMENT','test','2025-11-26 23:38:28'),(90,1,144,'PROGRESS','14','2025-11-26 23:39:59'),(91,1,144,'RATING','10','2025-11-26 23:40:06'),(92,1,144,'COMMENT','test11','2025-11-26 23:40:06'),(93,1,144,'STATUS','WATCHED','2025-11-27 09:42:30'),(94,1,144,'PROGRESS','9','2025-11-27 09:42:30'),(95,1,145,'STATUS','WATCHED','2025-11-27 17:51:11'),(96,1,145,'PROGRESS','12','2025-11-27 17:51:11'),(97,1,145,'RATING','10','2025-11-27 17:51:11'),(98,1,145,'COMMENT','test22222','2025-11-27 17:51:11'),(99,1,145,'PROGRESS','24','2025-11-27 17:51:38'),(100,1,145,'PROGRESS','21','2025-11-27 17:51:41'),(101,1,145,'PROGRESS','22','2025-11-27 17:51:43'),(102,1,145,'STATUS','WATCHING','2025-11-27 17:51:45'),(103,1,145,'PROGRESS','23','2025-11-27 17:51:45'),(104,1,145,'STATUS','WATCHED','2025-11-27 17:51:55'),(105,1,145,'PROGRESS','22','2025-11-27 17:51:55'),(106,1,145,'STATUS','WATCHING','2025-11-27 17:52:02'),(107,1,145,'STATUS','WATCHED','2025-11-27 17:52:07'),(108,1,145,'PROGRESS','21','2025-11-27 17:52:07'),(109,1,144,'STATUS','WATCHED','2025-11-27 22:25:47'),(110,1,144,'PROGRESS','9','2025-11-27 22:25:47'),(111,1,144,'RATING','9','2025-11-27 22:25:47'),(112,1,144,'COMMENT','test11','2025-11-27 22:25:47'),(116,1,139,'STATUS','WATCHED','2025-11-27 23:28:52'),(117,1,139,'PROGRESS','0','2025-11-27 23:28:52'),(118,1,139,'RATING','8','2025-11-27 23:28:52'),(119,1,139,'COMMENT','不赖','2025-11-27 23:28:52'),(120,1,139,'PROGRESS','7','2025-11-28 09:59:45'),(122,5,136,'STATUS','WATCHED','2025-11-28 21:01:40'),(123,5,136,'PROGRESS','12','2025-11-28 21:01:40'),(124,5,136,'RATING','8','2025-11-28 21:01:40'),(125,5,136,'COMMENT','好看','2025-11-28 21:01:40'),(127,5,145,'STATUS','WATCHING','2025-11-28 21:04:46'),(128,5,144,'STATUS','WATCHING','2025-11-28 22:34:59'),(129,5,145,'STATUS','WATCHED','2025-11-28 22:39:15'),(130,5,145,'PROGRESS','12','2025-11-28 22:39:15'),(131,5,145,'RATING','10','2025-11-28 22:39:15'),(132,5,145,'COMMENT','test','2025-11-28 22:39:15');
/*!40000 ALTER TABLE `user_activity` ENABLE KEYS */;

--
-- Dumping data for table `users`
--

/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','$2a$10$9vnMGla9vU2pDioAZmEmEOvxUvDRT9T52KQmTgWryz0ASfPbFgL3a','牧濑红莉栖','/uploads/avatars/1.png','ADMIN',1,'2025-11-24 23:51:03','签名修改测试'),(2,'u1','$2a$10$3EmaIRgRGxlCyS5iB0GtrO1kVKRSsd7KkoxIAwyhginAlLB.uvRXG','u1',NULL,'USER',1,'2025-11-24 23:54:06',NULL),(3,'u2','$2a$10$g73qsFTy4NF6ImKzlM6oeO5lG.zspvJ3Z6T6ohq6qgwik7VtLy/B.','u2',NULL,'USER',1,'2025-11-24 23:58:37',NULL),(4,'testuser','$2a$10$H86K4einkqaqi45AqO7Ifun32eQ6ZNVPjMpR4oJkTZQEQrjF/P7kO','tu',NULL,'USER',1,'2025-11-25 00:01:05',NULL),(5,'test','$2a$10$P0lJwamTcVmjg4B4GDbXrOdNsXd9uA/OXRjeO3pheWipWqG0RGX3i','test','/uploads/avatars/5.jpg','USER',1,'2025-11-25 00:27:56','11111');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-29 21:04:54
