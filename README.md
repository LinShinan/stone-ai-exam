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

**状态**: 🚧 开发中

</div>

---

## 📖 项目简介

Stone AI Exam 是一款基于 Spring Boot 3.5 和 AI 技术打造的现代化在线考试系统。系统致力于通过 AI 能力实现智能组卷、自动阅卷、学习分析等核心功能，为教育机构和企业培训提供高效、智能的考试解决方案。

> ⚠️ 本项目仍在积极开发中，功能持续完善中...

## 🛠️ 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 开发语言 |
| Spring Boot | 3.5.14 | 核心框架 |
| MyBatis-Plus | 3.5.15 | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Knife4j | 4.4.0 | API 文档工具 |
| X-File-Storage | 2.3.0 | 文件存储框架 |
| Aliyun OSS SDK | 3.17.4 | 阿里云对象存储 |
| Lombok | - | 代码简化 |

### 核心特性

- ✅ 基于 Spring Boot 3.5 构建，采用最新技术栈
- ✅ MyBatis-Plus 高效数据访问层
- ✅ Knife4j 在线 API 文档
- ✅ RESTful API 设计规范
- ✅ X-File-Storage 文件上传存储（支持阿里云 OSS）
- 🔄 AI 智能组卷（开发中）
- 🔄 AI 自动阅卷（开发中）
- 🔄 考生行为分析（开发中）
- 🔄 数据统计与报表（开发中）

## 📦 快速开始

### 运行步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd stone-ai-exam
```

2. **配置数据库**

修改 `src/main/resources/application.yaml` 中的数据库配置

3. **编译运行**
```bash
mvn clean install
mvn spring-boot:run
```

4. **访问 API 文档**

启动成功后访问: http://localhost:8080/doc.html

## 📂 项目结构

开发阶段暂未确定完整项目结构，正在开发中...

## 📝 开发计划

- [ ] 用户管理模块
- [ ] 题库管理模块
- [ ] 试卷管理模块
- [ ] AI 智能组卷
- [ ] 在线考试模块
- [ ] AI 自动阅卷
- [ ] 成绩统计分析
- [ ] 考生行为分析


---