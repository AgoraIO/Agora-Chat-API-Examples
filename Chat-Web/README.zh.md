# Agora chat 示例项目

_[English](README.md) | 中文_

## 简介

该仓库包含了使用 Agora Chat SDK 实现单聊的实例项目。

## 项目结构

此项目使用一个单独的 app 实现了多种功能。

| 功能         | 位置                                             |
| ------------ | ------------------------------------------------ |
| 页面内容     | [index.html](./index.html)                       |
| 发送文本消息 | [index.js](./src/index.js)                       |
| 发送语音消息 | [sendAudioMessage.js](./src/sendAudioMessage.js) |
| 录音         | [recordAudio.js](./utils/recordAudio.js)         |
| 获取会话列表 | [conversationList.js](./src/conversationList.js) |
| 获取历史消息 | [conversationList.js](./src/conversationList.js) |

## 如何运行示例项目

### 前提条件

- 有效的 Agora Chat 开发者账号。
- [创建 Agora Chat 项目并获取 AppKey](https://docs-im.easemob.com/im/quickstart/guide/experience) 。
- [npm](https://www.npmjs.com/get-npm)
- SDK 支持 IE9+、FireFox10+、Chrome54+、Safari6+ 之间文本、表情、图片、音频、地址消息相互发送。

### 运行步骤

1. 安装依赖

```bash
  npm install
```

2. 打包项目

```bash
  npm run build
```

3. 启动项目

```bash
  npm run start:dev
```

4. 浏览器打开 https://localhost:9000 运行项目。

一切就绪。你可以自由探索示例项目，体验 Agora Chat SDK 的丰富功能。

## 反馈

如果你有任何问题或建议，可以通过 issue 的形式反馈。

## 参考文档

- [Agora Chat SDK 产品概述](https://docs.agora.io/en/agora-chat/agora_chat_overview?platform=Web)
- [Agora Chat SDK API 参考](https://docs.agora.io/en/api-reference?platform=web)

## 相关资源

- 你可以先参阅 [常见问题](https://docs.agora.io/cn/faq)
- 如果你想了解更多官方示例，可以参考 [官方 SDK 示例](https://github.com/AgoraIO)
- 如果你想了解声网 SDK 在复杂场景下的应用，可以参考 [官方场景案例](https://github.com/AgoraIO-usecase)
- 如果你想了解声网的一些社区开发者维护的项目，可以查看 [社区](https://github.com/AgoraIO-Community)
- 若遇到问题需要开发者帮助，你可以到 [开发者社区](https://rtcdeveloper.com/) 提问
- 如果需要售后技术支持, 你可以在 [Agora Dashboard](https://dashboard.agora.io) 提交工单

## 代码许可

示例项目遵守 MIT 许可证。
