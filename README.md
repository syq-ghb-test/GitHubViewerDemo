# GitHubViewerDemo

一个支持 GitHub OAuth 登录、仓库浏览、Issue 管理、UI 状态友好的 Android GitHub demo。

## 主要功能
- GitHub OAuth 登录（WebView 认证，自动保存 access token）
- 个人信息展示（用户名、头像）
- 仓库列表浏览、搜索、详情页
- Issue 列表、创建 Issue、下拉刷新
- 支持私有仓库、全局 token 自动注入
- UI 友好，支持加载、失败、空态
- 单元测试与 UI 自动化测试

## 技术栈
- Kotlin
- Android Jetpack（ViewModel、LiveData、Lifecycle）
- OkHttp + Gson（网络与数据解析）
- Material Design 组件
- 协程（Coroutines）
- JUnit4、Mockito（单元测试）
- Espresso（UI 自动化测试）

## 快速开始

1. **克隆项目**
   ```bash
   git clone https://github.com/yourname/GitHubViewerDemo.git
   ```
2. **配置 GitHub OAuth**
   - 在 [GitHub Developer Settings](https://github.com/settings/developers) 注册 OAuth App，配置回调 URL。
   - 在 `Constants.kt` 填写你的 `CLIENT_ID` 和 `CLIENT_SECRET`。
3. **运行项目**
   - 用 Android Studio 打开项目，连接设备或模拟器，点击运行。

## 测试说明

### 单元测试
- 位置：`app/src/test/java/`
- 运行：右键测试类或方法，选择"Run"即可。
- 覆盖：API、Repository、ViewModel、Util 等核心逻辑。

### UI 自动化测试
- 位置：`app/src/androidTest/java/`
- 运行：右键测试类，选择"Run"或用命令行 `./gradlew connectedAndroidTest`
- 覆盖：登录、仓库搜索、详情跳转、Issue 创建、下拉刷新等主要流程。

## 目录结构
```
├── app/src/main/java/com/nikidas/demo/githubviewer/
│   ├── data/           # 数据层（API、Repository、Model）
│   ├── ui/             # 界面层（Activity、Fragment、Adapter）
│   ├── util/           # 工具类
│   └── MyApplication.kt
├── app/src/test/java/  # 单元测试
├── app/src/androidTest/java/ # UI自动化测试
├── app/src/main/res/   # 资源文件
└── README.md
```

## 贡献与反馈
- 欢迎 issue、PR 和建议！
- 如遇到 API 403/401、token 问题、仓库权限等常见问题，请优先检查 token 权限和仓库拼写。

## License
MIT 
