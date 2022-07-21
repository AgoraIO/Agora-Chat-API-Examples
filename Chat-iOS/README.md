# <AgoraChat API Example for iOS> *Sample projects for the Agora Chat iOS SDK*


## Overview

This repository contains three sample projects using the Agora Chat iOS SDK.

1.AgoraChatAPIExample,will show you how to using AgoraChatSDK api build a chat Application.

2.AgoraChatAPIExample-Swift,will show you how to using AgoraChatSDK api build a chat Application of Swift programa lanuage.

3.Chatuikitquickstart,will show you how to quickly build a chat project with AgoraChat-UIKit base on AgoraChatSDK.


### AgoraChatAPIExample project structure

1.ApiExample,show you how to initialize SDK and register and login and send message and join a group.

``` 
    initialize SDK
    AgoraChatOptions *options = [AgoraChatOptions optionsWithAppkey:@"41117440#383391"];
    options.enableConsoleLog = YES;
    [[AgoraChatClient sharedClient] initializeSDKWithOptions:options];
    
```

2.FetchServerMessage,show you how to fetch historical messages form sever.

3.ImportMessage,show you how to import a sendbird message.

4.SendAudioMessage,show you how to send a audio message.


| Function                                                                        | Location                                                                                                                                 |
| ------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| ApiExample,how to using SDK api                                                                  | [Api Example](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample/AgoraChatAPIExample/ApiExample/AgoraChatApiExampleViewController.m)                                  |
| FetchServerMessage,how to fetch historical messages from sever                                                                  | [FetchServerMessage](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample/AgoraChatAPIExample/FetchServerMessage/FetchServerMessageViewController.m)                                  |
| SendAudioMessage                                                                  | [SendAudioMessage](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample/AgoraChatAPIExample/SendAudioMessage/AudioMessageViewController.m)                                  |
| ImportMessage                                                                  | [ImportMessage](https://github.com/zjc19891106/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample/AgoraChatAPIExample/ImportMessage/ImportMessageViewController.m)                                  |

### AgoraChatAPIExample-Swift project structure

1.AgoraChatLoginViewController,show you how to register and login with SDK.

``` 
        //initialize SDK
        let options = AgoraChatOptions(appkey: AgoraChatRequest.appKey)
        options.enableConsoleLog = true
        options.isAutoLogin = true
        options.pushKitCertName = "com.easemob.enterprise.demo.ui.voip"
        options.apnsCertName = "ChatDemoDevPush"
        AgoraChatClient.shared().initializeSDK(with: options)
```

2.ViewController,show you function list.

3.AgoraChatConversationsViewController,show you how to load conversations.

4.AgoraChatSendTextViewController,show you how to send a text message.

5.AgoraChatSendImageVideoController,show you how to send a image message.

| Function                                                                        | Location                                                                                                                                 |
| ------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| Register and Login                                                                  | [Register and Login Swift](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample-Swift/AgoraChatAPIExample-Swift/AgoraChatAPIExample-Swift/Controllers/AgoraChatLoginViewController.swift)   |
| ViewController,function list.                                                                  | [EMHttpRequest](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample/AgoraChatAPIExample/EMHttpRequest.m)                                  |
| Load conversations                                                                  | [Local Conversations](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample-Swift/AgoraChatAPIExample-Swift/AgoraChatAPIExample-Swift/Controllers/AgoraChatConversationsViewController.swift)                                  |
| SendTextMessage swift code                                                                  | [SendTextMessage](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample-Swift/AgoraChatAPIExample-Swift/AgoraChatAPIExample-Swift/Controllers/AgoraChatSendTextViewController.swift)                                  |
| SendImageMessage swift code                                                                  | [SendImageMessage](https://github.com/AgoraIO/Agora-Chat-API-Examples/blob/main/Chat-iOS/AgoraChatAPIExample-Swift/AgoraChatAPIExample-Swift/AgoraChatAPIExample-Swift/Controllers/AgoraChatSendImageVideoController.swift)                                  |


### chatuikitquickstart project structure

[chatuikitquickstart](https://github.com/zjc19891106/Agora-Chat-API-Examples/tree/main/Chat-iOS/chatuikitquickstart)

## How to run the sample project

### Prerequisites

*Necessary requirements for the project to run.*

- Xcode 13.0+
- All iOS device supported
- Platform,iOS11 and above

### Steps to run

*Steps from cloning the code to running the project*

1. Navigate to the **iOS** folder and then,choose a folder run following command to install project dependencies:

    ```shell
    $ pod install
    ```
2. Open the generated `folder.xcworkspace` file with Xcode.

3. Build and run the project in your iOS device or simulator.

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
