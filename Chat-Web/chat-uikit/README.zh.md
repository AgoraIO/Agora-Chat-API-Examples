
# Agora chat uikit 示例项目

_[English](README.md) | 中文_

## 简介

本页面介绍了如何快速集成 Agora Chat UIkit 来实现单聊。


## 项目结构

此项目使用一个单独的 app 实现了多种功能。

| 功能         | 位置                                             |
| ------------ | ------------------------------------------------ |
| 页面样式     | [App.css](.src/App.css)                       |
| 集成 uikit 页面 | [App.js](./src/App.js)                       |

## 如何运行示例项目

### 运行步骤

1. 配置 AppId
   在运行项目之前，首先需要在 `Agora-Chat-API-Examples/Chat-Web/chat-uikit/src/index.js` 文件中配置你的 Agora Chat AppId：
   
   ```javascript
   <Provider
     initConfig={{
       appId: "your appId", // 替换为你的 Agora Chat SDK App ID
     }}
   >
   ```
   
   将 `"your appId"` 替换为你在 Agora 控制台获取的实际 AppId。

2. 安装依赖

npm
```bash
  npm install
```

yarn
```bash
  yarn
```

3. 启动项目

npm
```bash
  npm run start
```

yarn
```bash
  yarn start
```

4. 浏览器打开 http://localhost:3000/ 运行项目。

一切就绪。你可以自由探索示例项目，体验 Agora Chat UIKIT 的丰富功能。

## 反馈

如果你有任何问题或建议，可以通过 issue 的形式反馈。


## 参考文档

- [Agora Chat UIKIT 快速开始](https://docs.agora.io/en/agora-chat/agora_chat_uikit_web?platform=Web#reference)
- [Agora Chat UIKIT 源代码](https://github.com/AgoraIO-Usecase/AgoraChat-UIKit-web)

## 相关资源

- 你可以先参阅 [常见问题](https://docs.agora.io/cn/faq)
- 如果你想了解更多官方示例，可以参考 [官方 SDK 示例](https://github.com/AgoraIO)
- 如果你想了解声网 SDK 在复杂场景下的应用，可以参考 [官方场景案例](https://github.com/AgoraIO-usecase)
- 如果你想了解声网的一些社区开发者维护的项目，可以查看 [社区](https://github.com/AgoraIO-Community)
- 若遇到问题需要开发者帮助，你可以到 [开发者社区](https://rtcdeveloper.com/) 提问
- 如果需要售后技术支持, 你可以在 [Agora Dashboard](https://dashboard.agora.io) 提交工单

## 代码许可

示例项目遵守 MIT 许可证。
