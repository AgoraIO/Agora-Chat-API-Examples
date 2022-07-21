# API Example flutter

_English | [中文](README.zh.md)_

## Overview

This repository contains sample projects using the Agora Chat flutter sdk.

![flutter main](/images/flutter_main.jpeg)

## Project structure

The project uses a single app to combine a variety of functionalities.Each function is loaded as a activity for you to play with.

| Function | Location |
| --- | --- |
| Get Started with Agora Chat | [main.dart](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/chat_flutter/lib/main.dart) |

## How to run the sample project

### Prerequisites

#### iOS

- Flutter 2.10 or later
- Dart 2.16 or later
- macOS
- Xcode 12.4 or later with Xcode Command Line Tools
- CocoaPods
- An iOS simulator or a real iOS device running iOS 10.0 or later

#### Android

Flutter 2.10 or later
Dart 2.16 or later
macOS or Windows
Android Studio 4.0 or later with JDK 1.8 or later
An Android simulator or a real Android device running Android SDK API level 21 or later

### Steps to run


1. Clone this project to local
2. open `chat_flutter`.
3. run `flutter pub get`;
4. If you want to use your own App Key for the experience, you can edit the `chat_flutter/lib/main.dart` file.
   - Replace `APP_KEY` with your App KEY.
   - Replace `APP_SERVER_HOST` with the host of the App Server you built to get Agora Chat Token.
   - Replace `LOGIN_URL` with the address of the App Server you built to get Agora Chat Token.
   - Replace `REGISTER_URL` with the address of the registered Agora Chat user on the App Server you built.

   > See [Enable and Configure Agora Chat Service](https://docs.agora.io/cn/agora-chat/enable_agora_chat?platform=flutter) to learn how to enable and configure Agora Chat Service.

   > Refer to the source code [Chat App Server](https://github.com/AgoraIO/Agora-Chat-API-Examples/tree/main/chat-app-server) to learn how to quickly build an App Server.

5. Make the project and run the app in the simulator or connected physical Android or iOS device.

You are all set! Feel free to play with this sample project and explore features of the Agora Chat Java SDK.

## Feedback

If you have any problems or suggestions regarding the sample projects, feel free to file an issue.

## Reference

- [Agora Chat Overview]((https://docs.agora.io/cn/agora-chat/agora_chat_overview?platform=flutter)
- [API Reference]()

## Related resources

- Check our [FAQ](https://docs.agora.io/en/faq) to see if your issue has been recorded.
- Dive into [Agora SDK Samples](https://github.com/AgoraIO) to see more tutorials
- Take a look at [Agora Use Case](https://github.com/AgoraIO-usecase) for more complicated real use case
- Repositories managed by developer communities can be found at [Agora Community](https://github.com/AgoraIO-Community)
- If you encounter problems during integration, feel free to ask questions in [Stack Overflow](https://stackoverflow.com/questions/tagged/agora.io)

## License

The sample projects are under the MIT license.
