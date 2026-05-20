# 🎓 Stone AI Exam System

> AI 驱动的智能化考试测评系统

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen?style=flat-square&logo=spring)
![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.15-blue?style=flat-square)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue?style=flat-square&logo=mysql)
![Knife4j](https://img.shields.io/badge/Knife4j-4.4.0-blue?style=flat-square&logo=knife4j)
![X-File-Storage](https://img.shields.io/badge/X--File--Storage-2.3.0-blue?style=flat-square)
![Aliyun OSS](https://img.shields.io/badge/Aliyun%20OSS-3.17.4-blue?style=flat-square)
![Redis](https://img.shields.io/badge/Redis-7.0+-red?style=flat-square)

**状态**: 🚧 开发中

</div>

---

## 📖 项目简介

Stone AI Exam 是一款基于 Spring Boot 3.5 和 AI 技术打造的现代化在线考试系统。系统致力于通过 AI 能力实现智能组卷、自动阅卷、学习分析等核心功能，为教育机构和企业培训提供高效、智能的考试解决方案。

> ⚠️ 本项目仍在积极开发中，功能持续完善中...

## 🛠️ 技术栈

### 后端技术

| 技术             | 版本     | 说明       |
| -------------- | ------ | -------- |
| Java           | 21     | 开发语言     |
| Spring Boot    | 3.5.14 | 核心框架     |
| MyBatis-Plus   | 3.5.15 | ORM 框架   |
| MySQL          | 8.0+   | 关系型数据库   |
| Knife4j        | 4.4.0  | API 文档工具 |
| X-File-Storage | 2.3.0  | 文件存储框架   |
| Aliyun OSS SDK | 3.17.4 | 阿里云对象存储  |
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

## 📦 快速开始

### 运行步骤

- **克隆项目**

  ```bash
  git clone <repository-url>
  cd stone-ai-exam
  ```

- **配置数据库**

  修改 `src/main/resources/application.yaml` 中的数据库配置

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
GET    /api/banners/list       全部轮播图（后台管理用）
GET    /api/banners/active     已启用的轮播图（前台展示用）
GET    /api/banners/{id}       详情
POST   /api/banners/add        新增
PUT    /api/banners/update     更新
PUT    /api/banners/switch/{id} 启用/禁用
DELETE /api/banners/delete/{id} 删除
POST   /api/banners/upload-image  上传图片（独立接口，先上传拿到URL再提交表单）
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
GET    /api/notices/list       全部公告（后台管理用）
GET    /api/notices/active     所有已启用公告（按优先级+时间排序）
GET    /api/notices/latest     最新 N 条已启用公告
GET    /api/notices/{id}       详情
POST   /api/notices/add        新增
PUT    /api/notices/update     更新
PUT    /api/notices/switch/{id} 启用/禁用
DELETE /api/notices/delete/{id} 删除
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
GET    /api/categories          平级列表（每个分类带题目数量）
GET    /api/categories/tree     树形结构（父分类含子分类，数量汇总）
POST   /api/categories          新增
PUT    /api/categories          更新
DELETE /api/categories/{id}     删除
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
GET    /api/questions/list       分页 + 多条件（分类/难度/类型/关键词搜索）
GET    /api/questions/{id}       题目详情（三表信息完整返回）
POST   /api/questions            新增（三表联插，选择题自动生成答案）
PUT    /api/questions/{id}       更新（选择题先删选项再插，属全量替换）
DELETE /api/questions/{id}       删除（试卷引用校验 → 三表级联逻辑删除）
GET    /api/questions/popular    热门题目（Redis ZSet 排行，不足补最新）
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

## 🗂️ 项目文件结构

```
stone-ai-exam
└── src/main/java/com/stone/aiexam/
    ├── common/
    │   ├── Result.java              # 统一响应体 {code, message, data}
    │   └── StoneConstant.java       # 常量（Redis Key等）
    ├── config/
    │   └── RedisConfig.java         # Redis 序列化配置
    ├── controller/
    │   ├── BannerController.java    # 轮播图管理
    │   ├── NoticeController.java    # 公告管理
    │   ├── CategoryController.java  # 题目分类管理
    │   └── QuestionController.java  # 题库管理
    ├── dto/
    │   └── QuestionQueryDTO.java    # 题目多条件查询参数
    ├── entity/
    │   ├── BaseEntity.java          # 基类（id/时间/逻辑删除）
    │   ├── Banner.java
    │   ├── Notice.java
    │   ├── Category.java
    │   ├── Question.java            # 题目主表 + @TableField 字段
    │   ├── QuestionAnswer.java      # 答案表
    │   ├── QuestionChoice.java      # 选项表
    │   └── PaperQuestion.java       # 试卷-题目关联表
    ├── exception/
    │   ├── BusinessException.java   # 业务异常
    │   └── GlobalExceptionHandler.java
    ├── mapper/
    │   ├── BannerMapper.java
    │   ├── NoticeMapper.java
    │   ├── CategoryMapper.java
    │   ├── QuestionMapper.java
    │   ├── QuestionAnswerMapper.java
    │   ├── QuestionChoiceMapper.java
    │   └── PaperQuestionMapper.java
    └── service/
        ├── impl/
        │   ├── FileServiceImpl.java
        │   ├── BannerServiceImpl.java
        │   ├── NoticeServiceImpl.java
        │   ├── CategoryServiceImpl.java
        │   └── QuestionServiceImpl.java
        ├── FileService.java
        ├── BannerService.java
        ├── NoticeService.java
        ├── CategoryService.java
        └── QuestionService.java
```