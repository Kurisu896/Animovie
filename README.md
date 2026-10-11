# AniMovie：影视作品追踪与交流社区平台

AniMovie 是一个面向动漫、影视作品爱好者的追踪与交流社区平台。本项目为基于 Spring Boot 的毕业设计，实现了作品浏览、追番/观影进度管理、评分与短评、专题讨论、动态时间线、榜单搜索、个人主页及 AI 助手等功能。

## 技术栈

- 前端：Vue 3、Vite、Vue Router、Pinia、Element Plus
- 后端：Java 21、Spring Boot 3.5、MyBatis-Plus、JWT、BCrypt
- 数据库：MySQL 8+
- AI：阿里云 DashScope 兼容接口（可选）

## 功能概览

- 作品库：作品分页、筛选、搜索、详情、封面和评分统计
- 追踪管理：想看/在看/看过/搁置状态、观看进度、评分与短评
- 社区互动：作品讨论、回复、点赞、话题审核与管理
- 动态与排行：个人/全站动态流、热度与评分榜单
- 用户中心：注册登录、JWT 鉴权、头像、昵称、签名与个人主页
- 智能问答：根据站内作品和讨论内容提供 AI 辅助问答（配置密钥后启用）

## 项目结构

```text
.
├── Animovie/
│   ├── frontend/                 # Vue 3 前端
│   └── backend/                  # Spring Boot 后端
└── 数据库/
    └── bgm.sql                   # 完整初始化数据库（表结构与示例数据）
```

## 快速开始

### 1. 初始化数据库

创建数据库并导入完整初始化脚本：

```sql
CREATE DATABASE bgm DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

```bash
mysql -u root -p bgm < 数据库/bgm.sql
```

`Animovie/backend/src/main/resources/db/` 同时保留了与项目配套的结构和数据脚本，便于按需查看或导入。

### 2. 启动后端

在 `Animovie/backend` 目录运行：

```bash
mvn spring-boot:run
```

后端默认地址为 `http://localhost:8080`。数据库连接可通过环境变量配置：

```bash
# PowerShell 示例
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "你的 MySQL 密码"
# 可选：启用 AI 助手
$env:DASHSCOPE_API_KEY = "你的 DashScope API Key"
```

未设置 `DASHSCOPE_API_KEY` 时，其余功能仍可使用，AI 助手不可用。

### 3. 启动前端

在 `Animovie/frontend` 目录运行：

```bash
npm install
npm run dev
```

按 Vite 控制台提示打开浏览器地址（通常为 `http://localhost:5173`）。前端默认请求 `http://localhost:8080`。

## 配置说明

后端配置文件位于 `Animovie/backend/src/main/resources/application.properties`。仓库不会保存真实 API 密钥；请使用 `DASHSCOPE_API_KEY` 环境变量注入密钥。数据库账号和密码也支持通过 `DB_USERNAME`、`DB_PASSWORD` 覆盖默认本地开发配置。

## 说明

- `数据库/bgm.sql` 包含可直接导入的完整演示数据。
- `uploads` 中包含与演示数据对应的头像和作品封面，便于导入数据库后直接查看效果。
- 请勿在提交代码时加入真实密码、令牌或 API 密钥。

## 许可证

本项目用于毕业设计与学习交流。若需用于其他用途，请先联系作者确认。
