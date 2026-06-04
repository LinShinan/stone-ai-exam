# 🎓 Stone AI Exam System

> AI 驱动的智能化考试测评系统

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen?style=flat-square&logo=spring)
![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1.5-brightgreen?style=flat-square)
![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.15-blue?style=flat-square)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue?style=flat-square&logo=mysql)
![Knife4j](https://img.shields.io/badge/Knife4j-4.4.0-blue?style=flat-square&logo=knife4j)
![X-File-Storage](https://img.shields.io/badge/X--File--Storage-2.3.0-blue?style=flat-square)
![Aliyun OSS](https://img.shields.io/badge/Aliyun%20OSS-3.17.4-blue?style=flat-square)
![Apache POI](https://img.shields.io/badge/Apache%20POI-5.4.1-blue?style=flat-square)
![Redis](https://img.shields.io/badge/Redis-7.0+-red?style=flat-square)
![JWT](https://img.shields.io/badge/JJWT-0.12.6-blue?style=flat-square)
![BCrypt](https://img.shields.io/badge/BCrypt-12-green?style=flat-square)

</div>

<div align="center">

| 🔗 后端（本项目） |                              🖥️ 前端                               |
|:---:|:-----------------------------------------------------------------:|
| [stone-ai-exam](https://github.com/LinShinan/stone-ai-exam) | [stone-aiexam-web](https://github.com/LinShinan/stone-aiexam-web) |
| Spring Boot 3.5 后端服务 |                             Vue 前端界面                              |

</div>

---

## 📖 项目简介

Stone AI Exam 是一款基于 Spring Boot 3.5 和 AI 技术打造的现代化在线考试系统。系统致力于通过 AI 能力实现智能组卷、自动阅卷、学习分析等核心功能，为教育机构和企业培训提供高效、智能的考试解决方案。

## 🛠️ 技术栈

### 后端技术

| 技术             | 版本     | 说明       |
| -------------- | ------ | -------- |
| Java           | 21     | 开发语言     |
| Spring Boot    | 3.5.14 | 核心框架     |
| Spring AI      | 1.1.5  | AI 集成框架 |
| MyBatis-Plus   | 3.5.15 | ORM 框架   |
| MySQL          | 8.0+   | 关系型数据库   |
| Knife4j        | 4.4.0  | API 文档工具 |
| X-File-Storage | 2.3.0  | 文件存储框架   |
| Aliyun OSS SDK | 3.17.4 | 阿里云对象存储  |
| Apache POI     | 5.4.1  | Excel 读写 |
| Redis          | 7.0+   | 缓存与排行   |
| Lombok         | -      | 代码简化     |

### 核心特性

- ✅ 基于 Spring Boot 3.5 构建，采用最新技术栈
- ✅ MyBatis-Plus 高效数据访问层
- ✅ Knife4j 在线 API 文档
- ✅ RESTful API 设计规范
- ✅ X-File-Storage 文件上传存储（支持阿里云 OSS）
- ✅ 轮播图管理（Banner CRUD + 图片上传 + 启用/禁用）
- ✅ 公告管理（Notice CRUD + 启用/禁用 + 最新/激活筛选）
- ✅ 题目分类管理（Category CRUD + 树形结构 + 题目数量统计）
- ✅ 题库管理（Question CRUD + 多表关联 + 条件筛选 + Redis 热门排行）
- ✅ 试卷管理（Paper CRUD + 手动组卷 + 智能组卷 + 状态流转）
- ✅ 考试管理（开始考试 → 提交试卷 → AI 自动批阅 + AI 考试总结）
- ✅ AI 题目生成（Spring AI 集成 + 结构化提示词 + 多题型支持）
- ✅ 批量导入题目（Excel 模板下载 + 预览 + 批量导入）
- ✅ AI 简答题批阅（语义分析 + 分级评分 + 反馈与扣分依据）
- ✅ JWT 双角色认证（AdminFilter / StudentFilter 按前缀拦截，Token 单次解析注入请求上下文）
- ✅ BCrypt 密码加密（cost=12，仅引入 spring-security-crypto，不加载完整 Spring Security）
- ✅ 学生注册登录 + 个人中心（查看信息 + 修改密码）
- ✅ 简答题 AI 并行批阅（CompletableFuture + 自定义线程池，N 道题耗时从 N×3s 降至 ~3s）
- ✅ AI 服务降级（声明式重试 3 次 + 指数退避 2s/4s/8s，异常时兜底不中断流程）
- ✅ API 三层分层（公共端 /api/common | 用户端 /api/student | 管理端 /api/admin）

## 📦 快速开始

### 运行步骤

- **克隆项目**

  ```bash
  git clone <repository-url>
  cd stone-ai-exam
  ```

- **配置数据库**

  修改 `src/main/resources/application.yaml` 中的数据库配置

- **配置 AI**

  修改 `src/main/resources/application.yaml` 中的 Spring AI 配置（OpenAI API Key 等）

- **编译运行**

  ```bash
  mvn clean install
  mvn spring-boot:run
  ```

- **访问 API 文档**

  启动成功后访问: http://localhost:8080/doc.html

## 🧱 全局基础设施

在进入具体业务模块之前，先介绍几个贯穿全局的设计决策。

### 统一响应体 `Result<T>`

所有接口返回同一结构 `{ code, message, data }`，前端无需适配不同格式。成功统一走 `Result.success(data)`，失败由全局异常处理器统一捕获并包装，Controller 里不出现 try-catch。

### 全局异常处理器 `GlobalExceptionHandler`

`BusinessException` 是项目唯一的业务异常类，携带一个 message。全局处理器 `@RestControllerAdvice` 拦截后返回 `Result.fail(message)`，HTTP 状态码保持 200（业务失败 ≠ 系统异常），前端直接读 `code` 判断成功与否。

### 实体基类 `BaseEntity`

所有实体继承，统一 `id`（自增主键）、`createTime`、`updateTime`（`@JsonIgnore` 不返回前端）、`isDeleted`（`@TableLogic` 逻辑删除）。MyBatis-Plus 检测到 `@TableLogic` 后自动改写所有 DELETE 为 UPDATE is_deleted=1，SELECT 自动追加 is_deleted=0。

### Redis 序列化配置

`RedisTemplate<String, Object>` 自定义配置：Key 用 `StringRedisSerializer` 保证可读，Value 用 `GenericJackson2JsonRedisSerializer` 支持任意对象 JSON 序列化，存进去的对象带 `@class` 类型信息，拿出来直接强转。

### API 三层分层

本项目按角色将 API 分为三层，URL 前缀区分：

| 端 | 前缀 | 认证 | 说明 |
|---|---|---|---|
| 公共端 | `/api/common/**` | 无需登录 | 轮播图、公告、热门题目、排行榜等 |
| 用户端 | `/api/student/**` | 学生 Token | 考试、个人中心 |
| 管理端 | `/api/admin/**` | 管理员 Token | 所有增删改操作 |
| 认证 | `/api/auth/**` | 无需登录 | 登录接口 |

Knife4j 文档右上角下拉框可按端切换，只看对应分组的接口。

### JWT 认证 + BCrypt 密码加密

**登录流程**：`POST /api/auth/login` 验证用户名 + BCrypt 哈希密码 → `JwtUtil.generateToken()` 签发 Token → 前端存 Token 在请求头 `token` 中传输。

**双 Filter 架构**：

| Filter | 拦截路径 | 校验逻辑 |
|---|---|---|
| `AdminFilter` | `/api/admin/*` | 验证 Token → 校验 role=ADMIN → 放行 |
| `StudentFilter` | `/api/student/*` | 验证 Token → 校验 role=STUDENT → 注入 `request.setAttribute("username", ...)` → 放行 |

**设计要点**：
- `StudentFilter` 将 username 注入 `request` 属性，Controller 层直接取用，无需二次解析 Token
- 学生端详情接口服务端校验 `username == record.studentName`，防止越权查看他人考试记录
- BCrypt cost=12（4096 轮），仅引入 `spring-security-crypto`，不加载完整 Spring Security 框架

**关键文件**：

| 文件 | 职责 |
|---|---|
| `PasswordEncoderConfig` | BCryptPasswordEncoder Bean（cost=12） |
| `JwtProperties` | `@ConfigurationProperties(prefix="jwt")` 管理密钥和过期时间 |
| `JwtUtil` | 基于 jjwt 0.12 实现 Token 签发、解析、校验 |
| `AdminFilter` | 拦截 `/api/admin/*`，无 Token → 401，角色非 ADMIN → 403 |
| `StudentFilter` | 拦截 `/api/student/*`，无 Token → 401，角色非 STUDENT → 403，注入 username |
| `FilterConfig` | 注册双 Filter 到 Filter 链，AdminFilter order=1，StudentFilter order=2 |

---

## 📁 文件存储模块

**职责**：封装 X-File-Storage 框架，统一管理文件上传。

### 选型考虑

X-File-Storage 是国内 Spring 生态下较成熟的文件存储框架，支持本地、阿里云 OSS、MinIO 等多后端一键切换，无需改业务代码。本项目当前接入阿里云 OSS，后续如需切 MinIO 只需改 yaml 配置。

### 实现要点

- **路径按日期分层**：上传时自动生成 `模块名/yyyy/MM/` 路径，避免单目录文件过多
- **图片专用接口**：`uploadImage()` 调用框架的 `.image()` 链式方法，自动限制上传图片尺寸（1000x1000），同时生成 200x200 缩略图，前端列表加载缩略图、详情加载原图
- **调用链**：Controller 接收 MultipartFile → Service 做非空/类型校验 → FileService 上传 OSS → 返回图片 URL → 业务表存 URL

---

## 🖼️ 轮播图管理

**业务场景**：管理首页轮播图，支持后台配置图片、跳转链接、排序，前台按开关状态和排序展示。

### 接口

```
公共端 /api/common/banners
GET    /active                    已启用的轮播图（前台展示用）

管理端 /api/admin/banners
GET    /list                      全部轮播图（后台管理用）
GET    /{id}                      详情
POST   /add                       新增
PUT    /update                    更新
PUT    /switch/{id}               启用/禁用
DELETE /delete/{id}               删除
POST   /upload-image              上传图片（先上传拿到 URL 再提交表单）
```

### 实现要点

- **上传与业务分离**：图片上传独立于 Banner CRUD，用户在管理界面先点上传拿到 OSS 返回的 URL，再填入表单提交。这样业务表专注存 URL 字符串，不与文件上传耦合
- **启用/禁用用 UpdateWrapper**：不走先查后改，直接 `LambdaUpdateWrapper` 构造 `SET is_active=? WHERE id=?`，一次 SQL 搞定，减少数据库往返
- **Sort 排序**：列表查全部，不需要分页，用 `orderByAsc(sortOrder)` 返回

---

## 📢 公告管理

**业务场景**：管理系统公告，前台可以展示最新 N 条已启用的公告，或者全部已启用公告。

### 接口

```
公共端 /api/common/notices
GET    /latest                    最新 N 条已启用公告
GET    /active                    所有已启用公告（按优先级+时间排序）
GET    /{id}                      详情

管理端 /api/admin/notices
GET    /list                      全部公告（后台管理用）
POST   /add                       新增
PUT    /update                    更新
PUT    /switch/{id}               启用/禁用
DELETE /delete/{id}               删除
```

### 实现要点

- **两级排序**：`getActiveNoticeList` 先按 `priority` 降序，再按 `createTime` 降序。高优先级置顶，同优先级新公告在前
- **LIMIT 投放**：`getLatestActiveNoticeList` 用 `.last("LIMIT " + limit)` 限制条数，而不是查全部再截取 —— 100 条只取 5 条的情况下少传输 95 条数据
- **启禁用同轮播图**：`LambdaUpdateWrapper` 直接更新字段，不走查询

---

## 📂 题目分类管理

**业务场景**：题目需要按分类组织（如 Java、数据库、Redis），分类支持两级树形结构（父分类 → 子分类），前台需要看到每个分类下有多少道题。

### 接口

```
公共端 /api/common/categories
GET                              平级列表（每个分类带题目数量）
GET    /tree                     树形结构（父分类含子分类，数量汇总）

管理端 /api/admin/categories
POST                             新增
PUT                              更新
DELETE /{id}                     删除
```

### 数据模型

```
categories 表
┌──────────┬───────────┬──────┐
│ id       │ name      │ ...  │
│ parentId │ 0=一级分类  │ ...  │
│ sort     │ 排序       │ ...  │
└──────────┴───────────┴──────┘

Category 实体
├── @TableField(exist=false) children  子分类列表（内存构建）
└── @TableField(exist=false) count     题目数量（SQL统计）
```

### 实现要点

**1. 题目数量统计：一次 GROUP BY 而非 N 次 COUNT**

```sql
-- 一次 SQL 查出全部分类的题目数
SELECT category_id, COUNT(*) AS count
FROM questions
WHERE is_deleted = 0
GROUP BY category_id
```

Java 层用 `Collectors.toMap` 生成 `Map<categoryId, count>`，遍历分类列表时 O(1) 取值。避免对每个分类单独 `SELECT COUNT(*)` 的 N+1 问题。

**2. 树形结构构建**

两步走：先过滤出 `parentId=0` 的一级分类，再按 `parentId` 分组所有二级分类。遍历一级分类时取对应子列表。题目数量 = 父分类自身数 + 所有子分类数量之和。

**3. 业务校验**

| 操作 | 校验 |
|------|------|
| 新增 | 同一父分类下不能重名 |
| 更新 | 同一父分类下不能重名（排除自身） |
| 删除 | 一级分类禁止删除；分类下有题目时禁止删除，提示具体数量 |

**4. MyBatis-Plus @TableField(exist=false)**

`children` 和 `count` 字段打上 `exist=false`，MP 生成 SQL 时跳过这些字段，不会报 Unknown column 错误。数据在 Service 层通过 Java 逻辑组装，不依赖数据库查询。

---

## 📝 题库管理

**业务场景**：这是系统最核心的模块。题目类型包括选择题（单选/多选）、判断题、简答题，每道题有独立的标准答案和选项（选择题专用）。需要对题目做热度统计和热门排行。

### 数据模型：三表关联

```
Question (主表)          QuestionAnswer (答案表 1:1)    QuestionChoice (选项表 1:N)
┌──────────────┐        ┌──────────────────┐        ┌──────────────────┐
│ id           │        │ id               │        │ id               │
│ title        │───────>│ questionId       │        │ questionId       │
│ type         │        │ answer           │<───────│ content          │
│ multi        │        │ keywords         │        │ isCorrect        │
│ difficulty   │        └──────────────────┘        │ sort             │
│ score        │                                    └──────────────────┘
│ analysis     │
│ categoryId   │
└──────────────┘
```

选择题答案存储格式：后端根据正确选项自动拼接，如 A、C 正确 → `answer = "A,C"`。判断题存 `"true"` / `"false"`。简答题存文字答案，keywords 字段预留用于 AI 评分。

### 接口

```
公共端 /api/common/questions
GET    /list                      分页 + 多条件（分类/难度/类型/关键词搜索）
GET    /popular                   热门题目（Redis ZSet 排行，不足补最新）
GET    /{id}                      题目详情（三表信息完整返回）

管理端 /api/admin/questions
POST                              新增（三表联插，选择题自动生成答案）
PUT    /{id}                      更新（选择题先删选项再插，属全量替换）
DELETE /{id}                      删除（试卷引用校验 → 三表级联逻辑删除）

管理端 /api/admin/questions/batch
GET    /batch/template            下载 Excel 导入模板
POST   /batch/preview-excel       预览 Excel 文件内容
POST   /batch/import              确认并批量导入
POST   /batch/ai-generate         AI 生成题目
```

### 关键技术实现

**1. 批量装配：三表三查 + Map 索引，杜绝 N+1**

列表查询涉及三张表：题目主表、答案表、选项表。如果逐题查答案和选项，100 道题会产生 1 + 100 + 100 = 201 次 SQL。改为批量 in 查询后固定 3 次 SQL：

```
第1次：SELECT * FROM questions WHERE ...          （分页10条）
第2次：SELECT * FROM question_answers WHERE question_id IN (1,2,...,10)
第3次：SELECT * FROM question_choices WHERE question_id IN (1,2,...,10)
```

答案用 `Collectors.toMap(id, answer)` 建索引，选项用 `Collectors.groupingBy(questionId)` 分组。遍历题目列表时 `map.get(questionId)` O(1) 匹配，时间复杂度 O(n)。

这个装配逻辑抽取为 `setAnswerAndChoice()` 私有方法，列表查询和热门查询共用，避免代码重复。

**2. 选择题答案自动生成**

前端传入 `[{A: 正确}, {B: 错误}, {C: 正确}, {D: 错误}]`，后端遍历时用索引转字母（0→A, 1→B, ...），拼接正确选项为 `"A,C"` 存入 answer 字段。这样做的好处是答案格式独立于前端 — 前端删了一个选项，正确答案不需要手动维护。

**3. 热度排行：Redis ZSet + 异步 + 惰性清理**

```
查看题目详情 ──(@Async)──> ZINCRBY question:hot:score <id> 1
                                 ...
热门查询 ──ZREVRANGE 0 N-1──> [68, 82, 81, ...]
         ──不足N个──> 查最新题目补全
```

| 决策点 | 做法 | 原因 |
|--------|------|------|
| 为什么用 ZSet | 天然按 score 排序，`ZREVRANGE` 取 TopN 一条命令 | 比存 List + Java 排序更直接 |
| 为什么异步 | 热度写入不要求即时性，不阻塞用户看题 | `@Async` 开一个线程写入，主线程立即返回 |
| 为什么惰性清理 | 被删题目的 ID 留在 ZSet 里，热门查询时 `idMap.get(id)` 返回 null | 顺手 `ZREM` 删除，不写定时任务 |

**4. 批量查询后顺序保持**

`listByIds([68, 82, 81])` 生成的 SQL `WHERE id IN (68,82,81)` 不保证返回顺序和 IN 列表一致。为了保持 ZSet 返回的有序结果，用 `Map<id, Question>` 做索引，然后按原来的有序 ID 列表逐个取：

```java
// Redis 返回有序ID [68, 82, 81] → 批量查 → Map索引 → 按原序取出
Map<Long, Question> idMap = questions.stream()
    .collect(Collectors.toMap(Question::getId, q -> q));
questionIds.forEach(id -> {
    Question q = idMap.get(id);
    if (q != null) popularQuestions.add(q);
    else zSetOps.remove(KEY, id);  // 惰性清理
});
```

**5. 选择题更新：先删后插 + 逻辑删除主键冲突**

更新选择题选项时，采用"先删后插"策略（全量替换最简单稳妥）。但选项表上有 `@TableLogic` 逻辑删除，旧数据的主键还在表中。直接 insert 的话 MyBatis-Plus 检测到主键不为 null 就走 update 逻辑，但如果该 ID 已被标记删除就会 Duplicate Key。

处理方式：每个要重新插入的选项，把 `id`、`createTime`、`updateTime` 手动置 null，MyBatis-Plus 的 `IdType.AUTO` 才会走自增生成新主键。

**6. 事务控制**

新增、更新、删除都在 `@Transactional` 下操作，涉及三表的 INSERT/DELETE/UPDATE 要么全成功要么全回滚。删除时先查试卷-题目关联表（`paper_question`），有引用则抛异常阻止删除。

**7. 分页查询的 Page 回填模式**

MyBatis-Plus 的 `Page` 是引用类型，Controller new 出来传给 Service，Service 里 `page(page, wrapper)` 把数据和 total 写回同一对象。Controller 拿到后直接 `page.getRecords()` + `page.getTotal()`，不返回新对象，不拆散 Page 结构。这种模式利用了 Java 的引用传递特性，避免了 MVC 层之间定义额外 DTO。

---

## 📋 试卷管理

**业务场景**：试卷是考试的载体，管理员可以手动挑选题目组卷，也可以通过 AI 智能组卷规则自动生成试卷。试卷有完整的生命周期：草稿 → 发布 → 停止。

### 数据模型

```
paper 表                          paper_question 关联表
┌──────────────────┐             ┌──────────────────┐
│ id               │             │ id               │
│ name             │────────────>│ paperId          │
│ description      │             │ questionId       │
│ status           │             │ score            │  ← 题目在试卷中的分值
│ totalScore       │             └──────────────────┘
│ questionCount    │
│ duration         │             Question (题目主表)
└──────────────────┘             ┌──────────────────┐
                                 │ id               │
                                 │ title            │
                                 │ type             │
                                 │ ...              │
                                 │ paperScore       │  ← @TableField(exist=false)
                                 └──────────────────┘
```

关键设计：同一道题在不同试卷中可以有不同的分值，所以 `score` 存在关联表 `paper_question` 中，而不是题目主表中。查询试卷详情时通过多表 JOIN 将分值注入 `Question.paperScore` 字段。

### 接口

```
公共端 /api/common/papers
GET    /list                      已发布试卷列表（前台选卷用）
GET    /{id}                      试卷详情（含题目列表 + 答案 + 选项）

管理端 /api/admin/papers
GET    /list                      试卷列表（全部状态，支持按名称/状态筛选）
GET    /{id}                      试卷详情
POST                              手动创建试卷（传入题目ID和分值映射）
POST   /smart                     智能组卷（传入规则自动选题）
PUT    /{id}                      更新试卷（先删关联再重建）
PATCH  /{id}/status               更新试卷状态（DRAFT → PUBLISHED → STOPPED）
DELETE /{id}                      删除试卷（校验发布状态和考试引用）
```

### 手动组卷流程

```
POST /api/papers  { name, description, duration, questions: { questionId: score, ... } }
```

1. 校验试卷名称唯一性
2. 创建 Paper 记录，status 初始为 DRAFT
3. 根据 `questions` Map 计算 `totalScore` 和 `questionCount`
4. 批量插入 `paper_question` 关联记录

### 智能组卷流程

```
POST /api/papers/smart  { name, description, duration, rules: [...] }
```

每条规则定义：`{ type: "CHOICE", categoryIds: [1,2], count: 10, score: 5 }` — 表示从分类 1、2 中随机抽 10 道选择题，每题 5 分。

实现要点：

1. **逐规则处理**：遍历 rules，每条规则生成对应题型的 SQL 查询
2. **不足降级**：题库数量不足时，取实际数量（`Math.min(available, required)`），并 warn 日志记录
3. **随机选题**：用 `Collections.shuffle()` 打乱查询结果，取前 N 条，保证每次组卷结果不同
4. **双写更新**：所有规则的题目选择完毕后，统一更新 Paper 的 `totalScore` 和 `questionCount`
5. **事务保护**：整个组卷过程在 `@Transactional` 下，任一步失败全部回滚

### 业务约束

| 操作 | 约束 |
|------|------|
| 创建 | 试卷名称不能重复 |
| 更新 | 已发布（PUBLISHED）的试卷不允许更新 |
| 删除 | 已发布不允许删除；被考试记录引用时不允许删除（提示具体条数） |
| 状态流转 | DRAFT → PUBLISHED → STOPPED（单向，不可逆） |

---

## ✍️ 考试管理

**业务场景**：考生选择试卷开始考试，作答过程中提交答案，考试结束后系统自动批阅。整个流程为：开始考试 → 答题 → 提交试卷 → AI 自动批阅 → 查看成绩与总结。

### 数据模型

```
exam_records 表                    answer_record 表
┌──────────────────┐             ┌──────────────────┐
│ id               │             │ id               │
│ examId (试卷ID)   │────────────>│ examRecordId     │
│ studentName      │             │ questionId       │
│ score            │             │ userAnswer       │
│ summary (AI总结) │             │ score            │
│ status           │             │ isCorrect        │
│ startTime        │             │ aiCorrection     │
│ endTime          │             └──────────────────┘
│ windowSwitches   │
└──────────────────┘
```

考试状态流转：`进行中` → `已完成` → `已批阅`

作答正确性标记：`0` 错误、`1` 完全正确、`2` 部分正确（简答题专用）

### 接口

```
公共端 /api/common/exams
GET    /ranking                    考试排行榜

用户端 /api/student/exams
POST   /start                      开始考试
GET    /{id}                       获取考试记录详情（含试卷 + 作答记录）
POST   /{examRecordId}/submit      提交试卷（自动触发批阅）

管理端 /api/admin/exams
GET    /list                       考试记录分页列表
POST   /{examRecordId}/grade       AI 自动批阅（独立调用，用于重新批阅）
DELETE /{id}                       删除考试记录
```

### 开始考试

1. 校验该考生是否已有同试卷的"进行中"记录 → 有则直接返回（幂等，防止重复开考）
2. 无则创建新记录：`status=进行中`，`startTime=当前时间`
3. 使用 Builder 模式构建 ExamRecord 对象

### 提交试卷

```
POST /api/exams/{examRecordId}/submit
Body: [{ questionId: 1, userAnswer: "A" }, { questionId: 2, userAnswer: "true" }, ...]
```

1. 批量保存作答记录（只存 `examRecordId` + `questionId` + `userAnswer`，不含分数）
2. 更新考试记录状态为"已完成"，记录 `endTime`
3. **自动触发 AI 批阅**（调用 `autoGradeExam`）

### AI 自动批阅

批阅逻辑按题目类型分两路：

```
选择题/判断题 ──> 程序比对：用户答案 == 标准答案？
                    ├── 正确 → isCorrect=1, score=满分
                    └── 错误 → isCorrect=0, score=0

简答题 ──────> AI 语义评分：调用 ChatClient
                    ├── 传入：题目 + 标准答案 + 学生答案 + 满分
                    ├── 返回：AiGradingResult { score, feedback, reason }
                    └── 得分 ≥ 满分 → isCorrect=1
                        得分 > 0   → isCorrect=2 (部分正确)
                        得分 = 0   → isCorrect=0
```

判断题答案归一化：前端可能传来 `T`/`F`、`true`/`false`、`正确`/`错误`、`对`/`错` 等多种格式，`transJudgeAnswer()` 方法统一转为 `TRUE`/`FALSE` 后再比对。

批阅完成后调用 AI 生成考试总结，根据得分率给出个性化学习建议。

### AI 并行批阅优化

简答题 AI 批阅是最大性能瓶颈（每次 HTTP 调用 2~5 秒）。将串行改为 `CompletableFuture` 并行后，N 道题耗时从 N×3s 降至 ~3s。

**架构**：

```
                                 ┌── 简答1 → AI API → ~3s
选择/判断 → 本地比对（瞬间）         ├── 简答2 → AI API → ~3s
                                 ├── 简答3 → AI API → ~3s
                                 ├── 简答4 → AI API → ~3s
                                 └── 简答5 → AI API → ~3s
                                          ↑
                        CompletableFuture.runAsync() 同时发出
                                          ↓
                        CompletableFuture.allOf().join() 等全部返回
```

**关键设计**：

| 决策点 | 做法 | 原因 |
|--------|------|------|
| 线程池 | 独立 `aiGradingExecutor`（core=4/max=8/queue=100） | 隔离 AI 调用，不挤占 Tomcat 线程 |
| 并行范围 | 只对简答题并行，选择/判断本地比对 | 无网络 I/O 的题不需要线程开销 |
| 累分时机 | `allOf().join()` 之后统一算总分 | 并行任务返回前 score 还是 0 |
| 异常容错 | 单个简答题异常不中断其他批阅 | 每道题独立 catch，失败那道记 0 分 |

**配置类**：`AsyncConfig`（ThreadPoolTaskExecutor Bean），`ExamServiceImpl` 注入 `Executor aiGradingExecutor`。

### 学生个人中心

| 接口 | 说明 |
|------|------|
| `GET /api/student/user/profile` | 获取个人信息（不含密码） |
| `PUT /api/student/user/password` | 修改密码（验证旧密码 → BCrypt 加密新密码） |

**安全校验**：修改密码需验证旧密码正确 + 新旧密码不能相同 + 新密码 ≥ 6 位。

### 考试记录详情查询

`GET /api/exams/{id}` 返回的 `ExamRecord` 包含：

- **试卷信息**：通过 `paperService.getDetailById()` 加载完整试卷（含题目 + 答案 + 选项）
- **作答记录**：按试卷题目顺序排序（用 `questionOrder` Map 索引保证顺序一致性）
- **AI 批改意见**：简答题的 `aiCorrection` 字段包含 AI 反馈和扣分依据

---

## 🤖 AI 服务

**职责**：封装 Spring AI ChatClient，对外提供三个核心 AI 能力 —— 生成题目、批阅简答题、生成考试总结。

### 架构

```
AiService（单例 Service）
    │
    ├── ChatClient（Spring AI 注入）
    │       │
    │       └── OpenAI 兼容 API（可切换任何模型提供商）
    │
    ├── aiGenerateQuestions()     题目生成
    ├── gradeTextAnswer()         简答题批阅
    └── generateExamSummary()     考试总结
```

### 1. AI 生成题目

**流程**：

```
AiGenerateRequestDTO { topic, types, count, difficulty, categoryId, requirements }
        │
        ▼
buildQuestionPrompt()  构建结构化提示词（含题型/难度/数量/JSON格式要求）
        │
        ▼
chatClient.prompt().user(prompt).call().entity(AiQuestionResponse.class)
        │
        ▼
AiQuestionResponse { questions: List<QuestionImportDTO> }
        │
        ▼
注入 categoryId（由前端传入，AI 不知道内部分类体系）
```

**提示词设计要点**：

- 明确 JSON 输出格式（使用 ````json` 代码块约束 AI 输出）
- 判断题强调答案分布平衡（TRUE/FALSE 各约 50%）
- 根据题目类型区别字段要求（选择题有 choices、判断题有 answer 字段、简答题有 answer 字段）
- 要求每道题都有解析（analysis）

### 2. AI 批阅简答题

**输入**：题目对象（含标准答案）、学生答案、满分值

**提示词策略**：

```
角色设定：你是一名专业的考试阅卷老师
输入信息：题目、标准答案、满分、学生答案
评分标准：
  - 答案完整正确：80-100% 分数
  - 基本正确但不完整：60-80% 分数
  - 部分正确：30-60% 分数
  - 完全错误或未作答：0 分
输出格式：JSON { score: int, feedback: str, reason: str }
```

**容错**：AI 调用失败时返回 `score=0, feedback="AI批改异常，请联系教师"`，确保核心流程不中断。Spring AI 配置声明式重试（3 次 + 指数退避 2s/4s/8s），零代码实现服务降级。

### 3. AI 生成考试总结

**输入**：考生姓名、试卷名称、总分、满分、总题数、正确题数

**输出**：50-150 字的个性化总评，包含：
- 客观评价考试表现
- 指出优势和不足
- 具体学习建议和改进方向
- 鼓励和激励

**容错**：AI 调用失败时返回 `"AI总结生成失败，请查看各题批改详情"`。

---

## 📤 题目批量处理

**业务场景**：提供 Excel 批量导入题目的完整链路，以及 AI 辅助生成题目。适用于教师一次性录入大量题目的场景。

### 接口

```
已合并到管理端 /api/admin/questions/batch 下，见题库管理章节。
```

### Excel 批量导入流程

```
下载模板 → 填写数据 → 上传预览 → 确认导入
```

1. **下载模板**：`ExcelUtil.createQuestionTemplate()` 用 Apache POI 生成含表头和示例数据的 xlsx 文件
2. **上传预览**：上传填好的 Excel，后端解析为 `List<QuestionImportDTO>` 返回前端展示，不做入库
3. **确认导入**：前端确认无误后提交，后端批量写入三表（题目 + 答案 + 选项），返回成功/失败统计

### AI 生成题目

```
POST /api/questions/batch/ai-generate
Body: { topic: "Java多线程", types: "CHOICE,JUDGE", count: 10, difficulty: "MEDIUM", categoryId: 1 }
Response: [{ title, type, choices, answer, analysis, score, difficulty }, ...]
```

AI 生成的题目以 `QuestionImportDTO` 列表返回，前端可以在预览界面编辑、删减后再确认导入。这样 AI 生成和 Excel 导入共用同一条导入管线。

---

## 🗂️ 项目文件结构

```
stone-ai-exam
└── src/main/java/com/stone/aiexam/
    ├── common/
    │   ├── Result.java              # 统一响应体 {code, message, data}
    │   ├── ResultCode.java          # 响应码枚举
    │   └── StoneConstant.java       # 常量（状态/类型/Redis Key）
    ├── config/
    │   ├── RedisConfig.java         # Redis 序列化配置
    │   ├── Knife4jConfig.java       # API 文档 + 分组
    │   ├── MybatisPlusConfig.java   # MyBatis-Plus 配置
    │   ├── JwtProperties.java       # JWT 密钥与过期时间
    │   ├── PasswordEncoderConfig.java # BCrypt 密码编码器
    │   ├── AsyncConfig.java         # AI 批阅线程池
    │   └── FilterConfig.java        # 注册双 Filter
    ├── controller/
    │   ├── AuthController.java      # 登录
    │   ├── common/                   # /api/common/**
    │   │   ├── CommonBannerController.java
    │   │   ├── CommonNoticeController.java
    │   │   ├── CommonQuestionController.java
    │   │   ├── CommonCategoryController.java
    │   │   ├── CommonPaperController.java
    │   │   └── CommonExamController.java
    │   ├── student/                  # /api/student/** — StudentFilter 拦截
    │   │   ├── StudentExamController.java
    │   │   └── StudentUserController.java  # 个人中心（查看信息 + 修改密码）
    │   └── admin/                    # /api/admin/** — Filter 拦截
    │       ├── AdminBannerController.java
    │       ├── AdminNoticeController.java
    │       ├── AdminQuestionController.java
    │       ├── AdminQuestionBatchController.java
    │       ├── AdminPaperController.java
    │       ├── AdminCategoryController.java
    │       └── AdminExamController.java
    ├── dto/
    │   ├── AiGenerateRequestDTO.java # AI生成题目请求
    │   ├── AiGradingResult.java      # AI批阅结果
    │   ├── AiQuestionResponse.java   # AI生成题目响应
    │   ├── ChangePasswordDTO.java    # 修改密码请求
    │   ├── LoginRequestDTO.java      # 登录请求
    │   ├── PaperDTO.java             # 试卷创建/更新
    │   ├── QuestionImportDTO.java    # Excel导入/AI生成题目
    │   ├── QuestionQueryDTO.java     # 题目多条件查询
    │   ├── RegisterDTO.java          # 注册请求
    │   ├── RuleDTO.java              # 智能组卷规则
    │   ├── SmartPaperDTO.java        # 智能组卷请求
    │   ├── StartExamDTO.java         # 开始考试请求
    │   └── SubmitAnswerDTO.java      # 提交答案请求
    ├── entity/
    │   ├── BaseEntity.java          # 基类（id/时间/逻辑删除）
    │   ├── AnswerRecord.java        # 答题记录表
    │   ├── Banner.java
    │   ├── Category.java
    │   ├── ExamRecord.java          # 考试记录表
    │   ├── Notice.java
    │   ├── Paper.java               # 试卷表
    │   ├── PaperQuestion.java       # 试卷-题目关联表
    │   ├── Question.java            # 题目主表
    │   ├── QuestionAnswer.java      # 答案表
    │   ├── QuestionChoice.java      # 选项表
    │   ├── QuestionType.java        # 题目类型枚举
    │   └── User.java                # 用户表
    ├── filter/
    │   ├── AdminFilter.java         # 管理端 JWT 校验
    │   └── StudentFilter.java       # 学生端 JWT 校验 + 注入 username
    ├── exception/
    │   ├── BusinessException.java   # 业务异常
    │   └── GlobalExceptionHandler.java
    ├── mapper/
    │   ├── AnswerRecordMapper.java
    │   ├── BannerMapper.java
    │   ├── CategoryMapper.java
    │   ├── ExamRecordMapper.java
    │   ├── NoticeMapper.java
    │   ├── PaperMapper.java
    │   ├── PaperQuestionMapper.java
    │   ├── QuestionAnswerMapper.java
    │   ├── QuestionChoiceMapper.java
    │   ├── UserMapper.java
    │   └── QuestionMapper.java
    ├── service/
    │   ├── impl/
    │   │   ├── AnswerRecordServiceImpl.java
    │   │   ├── BannerServiceImpl.java
    │   │   ├── CategoryServiceImpl.java
    │   │   ├── ExamServiceImpl.java    # 核心：考试+批阅逻辑
    │   │   ├── FileServiceImpl.java
    │   │   ├── NoticeServiceImpl.java
    │   │   ├── PaperQuestionServiceImpl.java
    │   │   ├── PaperServiceImpl.java   # 核心：组卷逻辑
    │   │   ├── QuestionServiceImpl.java
    │   │   └── UserServiceImpl.java         # 用户（登录/注册/改密）
    │   ├── UserService.java
    │   ├── AiService.java              # AI 服务（题目生成/批阅/总结）
    │   ├── AnswerRecordService.java
    │   ├── BannerService.java
    │   ├── CategoryService.java
    │   ├── ExamService.java
    │   ├── FileService.java
    │   ├── NoticeService.java
    │   ├── PaperQuestionService.java
    │   ├── PaperService.java
    │   └── QuestionService.java
    ├── utils/
    │   ├── ExcelUtil.java          # Excel 模板生成工具
    │   └── JwtUtil.java            # JWT 签发/解析/校验
    └── vo/
        ├── ExamRankingVO.java       # 考试排行榜
        ├── LoginResponseVO.java     # 登录响应
        ├── PageResult.java          # 分页结果
        └── StatsVO.java             # 统计信息
```

---

## 🗃️ 数据库核心表关系

```
users                    ── 用户表
paper                   ── 试卷表
paper_question          ── 试卷-题目关联表（N:N，带分值）
questions               ── 题目主表
question_answers        ── 题目答案表（1:1）
question_choices        ── 题目选项表（1:N）
exam_records            ── 考试记录表
answer_record           ── 答题记录表
categories              ── 题目分类表
banners                 ── 轮播图表
notices                 ── 公告表
```

核心链路：

```
Paper ──(N:N)──> PaperQuestion ──(N:1)──> Question ──(1:1)──> QuestionAnswer
                                              │
                                              └──(1:N)──> QuestionChoice

ExamRecord ──(1:N)──> AnswerRecord ──(N:1)──> Question
    │
    └──(N:1)──> Paper
```