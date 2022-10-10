# API Example Web

_English | [中文](README.zh.md)_

## Overview

The repository contains sample projects that implement single Chat using the Agora Chat SDK.

## Project structure

The project uses a single app to combine a variety of functionalities.

| Function         | Location                                             |
| ------------ | ------------------------------------------------ |
| The page content     | [index.html](./index.html)                       |
| Sending text messages | [index.js](./src/index.js)                       |
| Sending a Voice Message | [sendAudioMessage.js](./src/sendAudioMessage.js) |
| The recording         | [recordAudio.js](./utils/recordAudio.js)         |
| Get conversation list | [conversationList.js](./src/conversationList.js) |
| Get historical messages | [conversationList.js](./src/conversationList.js) |

## How to run the sample project

### Prerequisites

- A valid Agora Chat developer account.
- [Create the Agora Chat project and get the AppKey](https://docs-im.easemob.com/im/quickstart/guide/experience) 。
- [npm](https://www.npmjs.com/get-npm)
- SDK supports Internet explorer 9+, FireFox10+, Chrome54+, Safari6+ text, expression, picture, audio, address messages sent to each other.

### Steps to run

1. Install dependencies

```bash
  npm install
```

2. Packaging project

```bash
  npm run build
```

3. Start the project

```bash
  npm run start:dev
```

4. Open your browser to https://localhost:9000 and run the project.

You are all set! Feel free to play with this sample project and explore features of the Agora Chat SDK.

## Feedback

If you have any problems or suggestions regarding the sample projects, feel free to file an issue.

## Reference

- [Agora Chat SDK Product Overview](https://docs.agora.io/en/agora-chat/agora_chat_overview?platform=Web)
- [Agora Chat SDK API Reference](https://docs.agora.io/en/api-reference?platform=web)

## Related resources

- Check our [FAQ](https://docs.agora.io/en/faq) to see if your issue has been recorded.
- Dive into [Agora SDK Samples](https://github.com/AgoraIO) to see more tutorials
- Take a look at [Agora Use Case](https://github.com/AgoraIO-usecase) for more complicated real use case
- Repositories managed by developer communities can be found at [Agora Community](https://github.com/AgoraIO-Community)
- If you encounter problems during integration, feel free to ask questions in [Stack Overflow](https://stackoverflow.com/questions/tagged/agora.io)

## License

The sample projects are under the MIT license.
