# AGENTS.md

本文件用于指导在 `givemefive` 仓库内工作的编码代理。执行任务时，以本文件为项目级约束；若用户在当前会话中给出更具体要求，以用户要求为准。

## 工作原则

1. 先理解再编码
   - 修改前先阅读相关模块代码、同层示例、配置和必要文档。
   - 明确假设、风险和成功标准；需求有多种解释时先说明，不要静默选择。
   - 对不清楚的业务含义、字段语义、数据加密边界、登录鉴权流程和微信能力差异，先询问或在回复中标明假设。

2. 简洁优先
   - 只实现用户要求的功能，不添加推测性扩展。
   - 不为单次使用代码抽象接口、工厂或复杂配置。
   - 能用现有工具类、Spring Boot/React/微信小程序原生能力解决时，优先复用。
   - 代码应直接、可读、少分支；新增异常处理应对应真实可能发生的场景。

3. 外科手术式修改
   - 只改和任务直接相关的文件与代码行。
   - 不顺手重构、不统一无关格式、不删除原有死代码。
   - 若自己的修改造成未使用 import、变量、方法或样式，需要清理。
   - 保持当前模块既有命名、目录结构、返回结构、配置方式和校验习惯。

4. 目标驱动和验证
   - Bug 修复优先写或定位能复现问题的验证点。
   - 功能变更至少运行能覆盖修改面的编译、类型检查或测试命令。
   - 不能验证时，要说明原因和剩余风险。

## 技术栈与模块

- `gmf-controller`
  - Spring Boot 3.3.x 后台服务，Java 17，Maven 构建。
  - 数据库使用 PostgreSQL，结构迁移使用 Flyway。
  - 使用 Spring Security，后台管理登录目标为开发者本人微信扫码登录。
  - 小程序用户敏感信息、记账数据、平台账户密码和 key 等入库前必须加密。

- `gmf-controller-web`
  - React 18 + TypeScript + Vite + Ant Design。
  - 作为后台管理入口，承载用户管理、统计报表和系统管理类页面。

- `gmf-wxapp`
  - 微信小程序端，TypeScript 编写。
  - 面向用户提供记账、借款、小商户商品库存整理和平台账户信息管理等能力。

## 常用命令

- 查找文件：`rg --files`
- 查找文本：`rg "pattern" path`
- 后端测试：
  - `cd gmf-controller`
  - `& "D:\apache-maven-3.6.3-bin\apache-maven-3.6.3\bin\mvn.cmd" test`
- 后端运行：
  - `cd gmf-controller`
  - `& "D:\apache-maven-3.6.3-bin\apache-maven-3.6.3\bin\mvn.cmd" spring-boot:run`
- 管理端安装依赖：`cd gmf-controller-web && npm install`
- 管理端构建验证：`cd gmf-controller-web && npm run build`
- 管理端本地运行：`cd gmf-controller-web && npm run dev`
- 小程序类型检查：`cd gmf-wxapp && npm run typecheck`

本项目后端优先使用本机公共 Maven：`D:\apache-maven-3.6.3-bin\apache-maven-3.6.3\bin\mvn.cmd`。不要默认改用项目内 `.m2`，除非用户明确要求。

## 项目结构

- `README.md`
  - 项目简介、模块说明和本地启动入口。

- `gmf-controller/pom.xml`
  - 后端 Maven 配置。
  - 当前主要依赖包含 Spring Boot Web、Security、Validation、Data JPA、Actuator、Flyway、PostgreSQL、H2 测试库和常用工具库。

- `gmf-controller/src/main/java/com/givemefive/gmfcontroller`
  - 后端主包。

- `gmf-controller/src/main/java/com/givemefive/gmfcontroller/GmfControllerApplication.java`
  - Spring Boot 启动入口。

- `gmf-controller/src/main/java/com/givemefive/gmfcontroller/config`
  - 后端配置类。
  - 当前包含安全配置和加密相关配置。

- `gmf-controller/src/main/java/com/givemefive/gmfcontroller/common`
  - 后端公共能力。
  - `util` 下维护通用工具类；新增通用能力前先确认这里是否已有可复用实现。
  - 仅在确有必要时新增工具类，避免把业务逻辑下沉到公共包。

- `gmf-controller/src/main/java/com/givemefive/gmfcontroller/web`
  - 通用 Web 接口，目前包含健康检查入口。

- `gmf-controller/src/main/java/com/givemefive/gmfcontroller/platformsecret`
  - 用户平台账户、密码、key 等敏感信息管理。
  - 入库前使用 `KeyEncUtils` 加密，当前加密 key 使用用户 `openid`。

- `gmf-controller/src/main/resources/application.yml`
  - 后端配置文件。
  - 数据库、服务端口、Actuator、数据加密和鉴权相关配置优先通过环境变量覆盖。

- `gmf-controller/src/main/resources/db/migration`
  - Flyway 数据库迁移脚本。
  - 当前规整为两个文件：`V1__system_tables.sql` 系统表，`V2__business_tables.sql` 业务表。
  - 新增表结构优先按系统表/业务表归类；如果迁移已经在真实环境执行过，不要直接改历史版本，需新增迁移或先确认 Flyway 处理方案。

- `gmf-controller/src/test`
  - 后端测试代码。
  - 新增加密、鉴权、数据访问或复杂业务逻辑时优先补测试。

- `gmf-controller-web/src`
  - 管理端源码。
  - `layout` 放后台整体布局，`pages` 放页面级组件。
  - 业务功能扩展优先新增页面和必要组件，不在入口文件堆叠复杂逻辑。

- `gmf-wxapp`
  - 微信小程序源码和配置。
  - `app.json` 维护页面注册和全局窗口配置。
  - `pages` 下按功能页面拆分，目前包含 `home`、`account`、`debt`、`merchant`、`profile`。
  - 不要编辑 `node_modules`。

## 后端开发注意事项

- Java 版本为 17，可以使用稳定语言能力，但优先匹配现有代码风格。
- Controller 只处理请求入参、鉴权上下文和响应转换；业务规则放 Service。
- Repository/Entity 只承载持久化职责，不在 Entity 中放业务流程。
- 用户隐私和敏感字段入库前必须加密；平台账户信息继续使用 `KeyEncUtils`，key 使用用户 `openid`。
- SQL 迁移要兼容 PostgreSQL；测试环境使用 H2 时，注意不要引入 H2 不支持且无替代的语法。
- JPA 配置为 `ddl-auto: validate`，表结构变更必须通过 Flyway 迁移维护。
- 对外接口新增鉴权时，要同步检查 `SecurityConfig`，避免误放开敏感接口。

## 管理端开发注意事项

- 使用 React 函数组件和 TypeScript。
- UI 优先使用 Ant Design 组件和 `@ant-design/icons`。
- 页面应偏后台管理风格：信息清晰、布局稳定、操作明确，不做营销页式展示。
- 新增报表优先使用已有 `@ant-design/plots` 依赖。
- 改动页面或路由后，至少运行 `npm run build` 做类型和构建验证。

## 小程序开发注意事项

- 页面由 `.json`、`.wxml`、`.wxss`、`.ts` 成组维护。
- 新增页面必须同步注册到 `app.json`。
- 使用微信小程序原生 API 时，优先保留清晰的用户授权和失败分支。
- 涉及截图、语音识别、转发给好友等微信能力时，先确认小程序端 API 限制，再实现最小可用流程。
- 改动 TypeScript 后，至少运行 `npm run typecheck`。

## 修改建议流程

1. 定位同类实现
   - 后端先找同包下相近 Controller/Service/Repository/Entity/Test。
   - 管理端先找相近页面、布局和样式。
   - 小程序先找相近页面目录。
   - 验证点：能说明本次改动复用了哪个现有结构。

2. 最小化实现
   - 只添加必要字段、接口、页面、迁移和测试。
   - 验证点：每一处新增都能对应到用户需求。

3. 同步配置和脚本
   - 后端检查 Flyway、配置项、安全配置和测试配置。
   - 前端检查路由、菜单、页面注册和构建脚本。
   - 验证点：运行入口、构建入口和部署入口仍保持一致。

4. 编译或测试
   - 后端优先运行 Maven 测试。
   - 管理端优先运行 `npm run build`。
   - 小程序优先运行 `npm run typecheck`。
   - 验证点：命令通过，或明确说明失败原因。
