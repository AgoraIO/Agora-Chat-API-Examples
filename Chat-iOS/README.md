# <AgoraChat API Example for iOS> *"Sample projects for the Agora Chat iOS SDK"*


## Overview

This repository contains sample projects using the Agora Chat iOS SDK .


## Project structure

1.The project uses a single app to combine a variety of functionalities.

2.Chat-iOS.xcworkspace contain chatuikitquickstart and AgoraChatAPIExample.

3.AgoraChatAPIExample,how to using normal api.

4.Chatuikitquickstart,how to quickly start a chat project with AgoraChatSDK and AgoraChat-UIKit.

5.AgoraChatAPIExample-Swift that is a AgoraChatAPIExample for Swift Programma Language.

| Function                                                                        | Location                                                                                                                                 |
| ------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| AgoraChatApiExample,how to using normal api                                                                  | [Api Example](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample/AgoraChatAPIExample/ApiExample/AgoraChatApiExampleViewController.m)                                  |
| Get local conversations                                                                  | [Local Conversations](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample-Swift/AgoraChatAPIExample-Swift/AgoraChatAPIExample-Swift/Controllers/AgoraChatConversationsViewController.swift)                                  |
| EMHttpRequest,responsible for authenticating requests                                                                  | [EMHttpRequest](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample/AgoraChatAPIExample/EMHttpRequest.m)                                  |
| FetchServerMessage,how to fetch historical messages from sever                                                                  | [FetchServerMessage](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample/AgoraChatAPIExample/FetchServerMessage/FetchServerMessageViewController.m)                                  |
| SendAudioMessage                                                                  | [SendAudioMessage](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample/AgoraChatAPIExample/SendAudioMessage/AudioMessageViewController.m)                                  |
| SendTextMessage swift code                                                                  | [SendTextMessage](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample-Swift/AgoraChatAPIExample-Swift/AgoraChatAPIExample-Swift/Controllers/AgoraChatSendTextViewController.swift)                                  |
| SendImageMessage swift code                                                                  | [SendImageMessage](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample-Swift/AgoraChatAPIExample-Swift/AgoraChatAPIExample-Swift/Controllers/AgoraChatSendImageVideoController.swift)                                  |
| Register and Login                                                                  | [Register and Login Swift](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample-Swift/AgoraChatAPIExample-Swift/AgoraChatAPIExample-Swift/Controllers/AgoraChatLoginViewController.swift)   |



## How to run the sample project

### Prerequisites

*Necessary requirements for the project to run.*

- Xcode 12.0+
- Physical iOS device (iPhone or iPad)
- iOS simulator supported

### Steps to run

*Steps from cloning the code to running the project*

1. Navigate to the **iOS** folder and run following command to install project dependencies:

    ```shell
    $ pod install
    ```
2. Open the generated `Chat-iOS.xcworkspace` file with Xcode.

3. Build and run the project in your iOS device.

4. *Keep the list until the project is up and running*

You are all set! Feel free to play with this sample project and explore features of the Agora Chat SDK.


## Feedback

If you have any problems or suggestions regarding the sample projects, feel free to file an issue.

## Reference

- [Product Overview](https://docs.agora.io/en/agora-chat/agora_chat_get_started_ios?platform=iOS)
- [API Reference](https://docs-preprod.agora.io/en/agora-chat/agora_chat_overview?platform=iOS)

## Related resources

- Check our [FAQ](https://docs.agora.io/en/faq) to see if your issue has been recorded.
- Dive into [Agora SDK Samples](https://github.com/AgoraIO) to see more tutorials
- Take a look at [Agora Use Case](https://github.com/AgoraIO-usecase) for more complicated real use case
- Repositories managed by developer communities can be found at [Agora Community](https://github.com/AgoraIO-Community)
- If you encounter problems during integration, feel free to ask questions in [Stack Overflow](https://stackoverflow.com/questions/tagged/agora.io)

## License

The sample projects are under the MIT license.
