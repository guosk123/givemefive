# Give Me Five

生活自助小程序项目，包含后台服务、后台管理端和微信小程序三个模块。

> 本项目全部使用codex完成

## Modules

- `gmf-controller`: Spring Boot 后台服务，负责接口、鉴权、业务逻辑和数据访问。
- `gmf-controller-web`: React + Ant Design 后台管理端。
- `gmf-wxapp`: 微信小程序端。

## Local Start

### Backend

```bash
cd gmf-controller
mvn spring-boot:run
```

服务默认读取 PostgreSQL、JWT、数据加密等配置的环境变量。见 `gmf-controller/src/main/resources/application.yml`。

### Admin Web

```bash
cd gmf-controller-web
npm install
npm run dev
```

### WeChat Mini Program

使用微信开发者工具导入 `gmf-wxapp` 目录。
