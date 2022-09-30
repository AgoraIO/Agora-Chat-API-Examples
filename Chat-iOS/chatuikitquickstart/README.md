
# Build a Conversation Page with the Agora Chat iOS UIKit 

This page describes how to rapidly build a conversation page by applying the Agora Chat iOS UIKit in your project.

## Message sending and receiving process

The process of logging in to Agora Chat is as follows:

1. The client registers an account via the App Server.
2. The client obtains the user token via the App Server and logs in to the Agora Chat with the user ID and user token.

The process of sending and receiving a one-to-one message is as follows:

1. Client A sends a message to the Agora Chat server.
2. The Agora Chat server sends the message to client B. Client B receives the message.

## Prerequisites

- iOS 11 and above
- A valid developer account of Agora Chat 

## Integration Procedure

### 1. Create a chat-uikit project

Create a Single View app in Xcode on the iOS platform by doing the following:

1. Set `Product Name` to `chatuikitquickstart`.
2. Set `Organization Identifier` to `agorachat`.
3. Select `Storyboard` for `User Interface`.
4. Select `Objective-C` for `Language`.

### 2. Integrate the chat-uikit

You can integrate the chat-uikit using either of the following methods. 

#### **Method 1: Integrate the the iOS chat-uikit by using a pod**

1. Install CocoaPods if you have not. For details, see [Getting Started with CocoaPods](https://guides.cocoapods.org/using/getting-started.html#getting-started).
2. In the Terminal, navigate to the project root directory and run the `pod init` command. Then the text file `Podfile` will be generated in the project folder.
3. Open the `Podfile` file and add the SDK to the file. Remember to replace `chatuikitquickstart` with the target name of your project.

```objective-c
platform :ios, '11.0'

# Import CocoaPods sources
source 'https://github.com/CocoaPods/Specs.git'

target 'chatuikitquickstart' do
    pod 'chat-uikit'
  	pod 'Masonry'
end
```

4. Run the `cd` command in the Terminal to switch to the directory where the `Podfile` file is located. Then run the following command to integrate the SDK.

```objective-c
pod install
```

5. After the pod installation is complete, the message `Pod installation complete!` will be displayed in the Terminal. At this time, the `xcworkspace` file will be generated in the project folder. You can open this new file to run the project.


#### **Method 2: Integrate chat-uikit using source code**

Download URL: https://github.com/AgoraIO-Usecase/AgoraChat-UIKit-ios.git

1. Download the source code of the latest chat-uikit. 
2. In the `Podfile` file, add dependencies of chat-uikit. The chat-uikit path should point to the directory where chat-uikit.podspec resides.

   ```objective-c
   pod 'chat-uikit',  :path => "../AgoraChat-UIKit-ios"
   ```

3. Run the `cd` command in the Terminal to switch to the directory where the `Podfile` file is located. Then run the `pod install` command to install local source code of chat-uikit.

   ```objective-c
   pod install
   ```

4. After pod installation is complete, the message `Pod installation complete!` will be displayed in the Terminal. At this time, the `xcworkspace` file will be generated in the project folder. You can open this new file to run the project.

### 3. Add permissions

Add related permissions in the `info.plist` project:

```xml
Privacy - Photo Library Usage Description //Album permissions.
Privacy - Microphone Usage Description //Microphone permissions.
Privacy - Camera Usage Description // Camera permissions.
App Transport Security Settings -> Allow Arbitrary Loads //Enable the network service.
```

### 4. Implement the chat UI

To help you easily implement and understand related functions, this page presents you how to integrate the chat UI of chat-uikit in `ChatViewController` and implement the following functions:

- Automatically load and present historical messages.
- Receive and present messages on the conversation page.
- Send text messages, image messages, file messages, voice messages, and video messages.

#### 4.1 Initialize the UIKit 

Add the following code to the `SceneDelegate.m` file to initialize the chat-uikit.

```objective-c
//Import the header files.
#import <chat-uikit/EaseChatKit.h>
#import "AgoraLoginViewController.h" //Login page.
#import <AgoraChat/AgoraChat.h> //Agora Chat SDK.
```

In this example, you can use the default App Key (41117440#383391). In your development environment, you need to register and user your [App Key](https://docs.agora.io/cn/AgoraPlatform/sign_in_and_sign_up).

```objective-c
//Initialize the chat-uikit.
- (void)scene:(UIScene *)scene willConnectToSession:(UISceneSession *)session options:(UISceneConnectionOptions *)connectionOptions {
    // Use this method to optionally configure and attach the UIWindow `window` to the provided UIWindowScene `scene`.
    // If using a storyboard, the `window` property will automatically be initialized and attached to the scene.
    // This delegate does not imply the connecting scene or session are new (see `application:configurationForConnectingSceneSession` instead).
    
    AgoraChatOptions *options = [AgoraChatOptions optionsWithAppkey:@"41117440#383391"];
    options.enableConsoleLog = YES;
    options.usingHttpsOnly = YES;
    options.enableDeliveryAck = YES;
    options.isAutoLogin = NO;
  	// Initialize the chat-uikit.
    [EaseChatKitManager initWithAgoraChatOptions:options];
    
    UIWindowScene *windowScene = (UIWindowScene *)scene;
    self.window = [[UIWindow alloc] initWithWindowScene:windowScene];
    self.window.frame = windowScene.coordinateSpace.bounds;
    // The login page.
    self.window.rootViewController = [[AgoraLoginViewController alloc]init];
    [self.window makeKeyAndVisible];
}
```

#### 4.2 Log in to the AgoraChat SDK

Before loading the conversation page, you must log in to the Agora Chat SDK. You can implement the login page yourself or by reference to `AgoraLoginViewController.m` in the `chatuikitquickstart` project.

`chatuikitquickstart` project URL: [quickStart](https://github.com/easemob/chat-api-examples/tree/main/Chat-iOS)

To implement the the login logic, do the following:

1. Create `Cocoa Touch Class` named `AgoraChatHttpRequest`.

2. Add the method definitions in the `AgoraChatHttpRequest.h` file (copy the whole content in the file): [AgoraChatHttpRequest.h](https://github.com/easemob/chat-api-examples/blob/main/Chat-iOS/chatuikitquickstart/chatuikitquickstart/AgoraChatHttpRequest.h). 

3.  Add the method implementations in the `AgoraChatHttpRequest.m` file (copy the whole content in the file): [AgoraChatHttpRequest.m](https://github.com/easemob/chat-api-examples/blob/main/Chat-iOS/chatuikitquickstart/chatuikitquickstart/AgoraChatHttpRequest.m).

4. Create the `Cocoa Touch Class` file named `AgoraLoginViewController` and import the header file to request the App Server to `AgoraLoginViewController.m`.

   ```objective-c
   #import "AgoraChatHttpRequest.h" //Tool class for implementing requests to the App Server.
   #import <AgoraChat/AgoraChat.h> //Agora Chat SDK。
   ```

5. Call the following code in `AgoraLoginViewController.m` to register an app server account.

   ```objective-c
   //Register an account via the app server.  
   - (void)doSignUp {
   [[AgoraChatHttpRequest sharedManager] registerToApperServer:@"Register ID" pwd:@"Register Password" completion:^(NSInteger statusCode, NSString * _Nonnull response) {
           dispatch_async(dispatch_get_main_queue(),^{
               if (response != nil) {
                   NSData *responseData = [response dataUsingEncoding:NSUTF8StringEncoding];
                   NSDictionary *responsedict = [NSJSONSerialization JSONObjectWithData:responseData options:0 error:nil];
                   if (responsedict != nil) {
                       NSString *result = [responsedict objectForKey:@"code"];
                       if ([result isEqualToString:@"RES_OK"]) {
                           //注册成功，可进行登录。After the account is created, you can log in to the chat app.
                       }
                   }
               }
           });
       }];
   }
   ```

6. Call the following code in `AgoraLoginViewController.m` to log in to the app server. Upon a successful login, the conversation page `ViewController` appears. For the logic of the `ViewController` conversation page, see <a href="#jump">4.3 Load the conversation page</a>.

   Import the header file of the conversation page:

   ```objective-c
   #import "ViewController.h"
   ```

   Following is the login logic:

   ```objective-c
   - (void)doSignIn {
       //Log in to the app server.
   [[AgoraChatHttpRequest sharedManager] loginToAppServer:@"ID" pwd:@"Password" completion:^(NSInteger statusCode, NSString * _Nonnull response) {
           dispatch_async(dispatch_get_main_queue(), ^{
               if (response && response.length > 0 && statusCode) {
                   NSData *responseData = [response dataUsingEncoding:NSUTF8StringEncoding];
                   NSDictionary *responsedict = [NSJSONSerialization JSONObjectWithData:responseData options:0 error:nil];
                   NSString *token = [responsedict objectForKey:@"accessToken"];
                   NSString *loginName = [responsedict objectForKey:@"chatUserName"];
                   if (token && token.length > 0) {
                       //Log in to the Agora Chat SDK.
                       [[AgoraChatClient sharedClient] loginWithUsername:[loginName lowercaseString] agoraToken:token completion:^(NSString *aUsername, AgoraChatError *aError) {
                           if (!aError) {
                             //Upon the successful login to the Agora Chat SDK, `ViewController` appears.
                             ViewController *chatsVC = [[ViewController alloc] init];
       											chatsVC.modalPresentationStyle = 0;
       											[self.navigationController pushViewController:chatsVC animated:YES];
                           }
                       }];
                   }
               } 
       		});
       }];
   }
   ```

#### <span id="jump"> 4.3 Load the conversation page</span>  

Following are definitions of attributes of the `ViewController.m` file:

```objective-c
#define kIsBangsScreen ({\
    BOOL isBangsScreen = NO; \
    if (@available(iOS 11.0, *)) { \
    UIWindow *window = [[UIApplication sharedApplication].windows firstObject]; \
    isBangsScreen = window.safeAreaInsets.bottom > 0; \
    } \
    isBangsScreen; \
})

#define AgoraChatVIEWTOPMARGIN (kIsBangsScreen ? 34.f : 0.f)

#import "ViewController.h"
#import <Masonry/Masonry.h>
#import <AgoraChat/AgoraChat.h>
#import <chat-uikit/EaseChatKit.h>

@interface ViewController ()<EaseChatViewControllerDelegate, UITextFieldDelegate>
@property (nonatomic, strong) EaseConversationModel *conversationModel;
@property (nonatomic, strong) AgoraChatConversation *conversation;
@property (nonatomic, strong) EaseChatViewController *chatController;
@property (nonatomic, strong) UITextField *conversationIdField;
@property (nonatomic, strong) UIButton *chatBtn;
@property (nonatomic, strong) UIButton *logoutBtn;

@end
```

Load UI controls in the `viewDidLoad` method in `ViewController.m`:

```objective-c
- (void)viewDidLoad {
    [super viewDidLoad];
    [self _setupChatSubviews];
}

- (void)viewWillAppear:(BOOL)animated
{
    self.navigationController.navigationBarHidden = YES;
}

- (void)_setupChatSubviews
{
    self.conversationIdField = [[UITextField alloc] init];
    self.conversationIdField.backgroundColor = [UIColor systemGrayColor];
    self.conversationIdField.delegate = self;
    self.conversationIdField.borderStyle = UITextBorderStyleNone;
    NSAttributedString *convAttrStr = [[NSAttributedString alloc] initWithString:@"single chat ID" attributes:@{NSForegroundColorAttributeName:[UIColor whiteColor]}];
    self.conversationIdField.attributedPlaceholder = convAttrStr;
    self.conversationIdField.font = [UIFont systemFontOfSize:17];
    self.conversationIdField.textColor = [UIColor whiteColor];
    self.conversationIdField.returnKeyType = UIReturnKeyDone;
    self.conversationIdField.layer.cornerRadius = 5;
    self.conversationIdField.layer.borderWidth = 1;
    self.conversationIdField.layer.borderColor = [UIColor lightGrayColor].CGColor;
    [self.view addSubview:self.conversationIdField];
    [self.conversationIdField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.view).offset(30);
        make.top.equalTo(self.view).offset(30 + AgoraChatVIEWTOPMARGIN);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@320);
    }];
    
    self.chatBtn = [[UIButton alloc] init];
    self.chatBtn.clipsToBounds = YES;
    self.chatBtn.layer.cornerRadius = 5;
    self.chatBtn.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
    self.chatBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [self.chatBtn setTitle:@"chat" forState:UIControlStateNormal];
    [self.chatBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.chatBtn addTarget:self action:@selector(chatAction) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.chatBtn];
    [self.chatBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.view).offset(30);
        make.top.equalTo(self.conversationIdField.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@150);
    }];
    
    self.logoutBtn = [[UIButton alloc]init];
    self.logoutBtn.backgroundColor = [UIColor redColor];
    [self.logoutBtn setTitle:@"Log out" forState:UIControlStateNormal];
    [self.logoutBtn addTarget:self action:@selector(logout) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.logoutBtn];
    [self.logoutBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.bottom.equalTo(self.view);
        make.height.equalTo(@50);
    }];
    
    self.view.backgroundColor = [UIColor colorWithRed:242/255.0 green:242/255.0 blue:242/255.0 alpha:1.0];
}
```

Type the conversation ID in the input box of `ViewController.m` of the conversation page and click the `Chat` button to show `EaseChatViewController`:

```objective-c
- (void)chatAction
{
    [self.view endEditing:YES];
    if (self.conversationIdField.text.length <= 0) {
        self.conversationIdField.placeholder = @"input single chat ID !";
        return;
    }
    
    [_chatController.view removeFromSuperview];
    [self.view endEditing:YES];
    
    if (!AgoraChatClient.sharedClient.isLoggedIn) {
        return;
    }
    
    _conversation = [AgoraChatClient.sharedClient.chatManager getConversation:self.conversationIdField.text type:AgoraChatConversationTypeChat createIfNotExist:YES];
    _conversationModel = [[EaseConversationModel alloc]initWithConversation:_conversation];
    
    EaseChatViewModel *viewModel = [[EaseChatViewModel alloc]init];
    _chatController = [EaseChatViewController initWithConversationId:self.conversationIdField.text
                                                conversationType:AgoraChatConversationTypeChat
                                                    chatViewModel:viewModel];
    
    _chatController.view.layer.borderWidth = 1;
    _chatController.view.layer.borderColor = [UIColor grayColor].CGColor;
    [self.view addSubview:_chatController.view];
    [_chatController.view mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.chatBtn.mas_bottom).offset(20);
        make.left.right.equalTo(self.view);
        make.bottom.equalTo(self.logoutBtn.mas_top);
    }];
}

```

Click the `Log out` button on `ViewController.m` of the conversation page to log out of the app server. On the login page that appears, you can log in with another user ID.

```objective-c
- (void)logout
{
    [AgoraChatClient.sharedClient logout:YES completion:^(AgoraChatError *aError) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"loginStateChange" object:@NO];
    }];
}
```

### 5. Compile and Run the Project

Use Xcode to compile and run the project on the simulator or physical device. After the project runs successfully, the following functions can be implemented:

- The conversation page presents historical messages.
- The conversation page receives and presents messages.
- Send messages on the conversation page, including text messages, image messages, video messages, attachment messages, voice messages, and emoji messages.

### 6. Run the chatuikitquickstart Project

chatuikitquickstart is a simple integration example of the chat-uikit, which only contains the conversation page of the chat-uikit.

Code download URL of the chatuikitquickstart: https://github.com/easemob/chat-api-examples/tree/main/Chat-iOS

Run the chatuikitquickstart:

1. Run the `cd` command in the Terminal to navigate to the Chat-iOS directory and run the following command to integrate the SDK.

   ```objective-c
   pod install
   ```

2. After the installation is complete, the message `Pod installation complete!` will be shown in the Terminal. You can open the `Chat-iOS.xcworkspace` file to select and run the `chatuikitquickstart` project.

### 7. Customize the chat UI

chat-uikit uses default UI styles. Following is a custom configuration example of UI styles of chat-uikit.

* Example of default styles:

To customize the chat UI, you only need to create an EaseChatViewModel instance and then pass it as a parameter to the constructor of the chat page EaseChatViewController.

```objective-c
EaseChatViewModel *viewModel = [[EaseChatViewModel alloc]init]; // Default styles.
EaseChatViewController *chatController = [EaseChatViewController initWithConversationId:@"Conversation ID" conversationType:AgoraChatConversationTypeChat chatViewModel:viewModel];
```

The following figure is an example of a chat page with default styles:

![defaultStyle](https://github.com/easemob/chat-api-examples/blob/main/Chat-iOS/chatuikitquickstart/defaultStyle.jpeg)

* Configuration example of a chat page with custom styles:

Create an EaseChatViewModel instance, modify configurable style parameters of this instance, and pass this instance to the constructor of EaseChatViewController.

```objective-c
EaseChatViewModel *viewModel = [[EaseChatViewModel alloc]init];
viewModel.chatViewBgColor = [UIColor systemGrayColor];  //The background color of the chat page.
viewModel.inputMenuBgColor = [UIColor systemPinkColor]; //The background color of the input area.
viewModel.sentFontColor = [UIColor redColor];           //The sender's text color.
viewModel.inputMenuStyle = EaseInputMenuStyleNoAudio;   //The menu style of the input area.
viewModel.msgTimeItemFontColor = [UIColor blackColor];  //The font color of the message time.
viewModel.msgTimeItemBgColor = [UIColor greenColor];    //The background color of the message time area.
EaseChatViewController *chatController = [EaseChatViewController initWithConversationId:@"Conversation ID" conversationType:AgoraChatConversationTypeChat chatViewModel:viewModel];
```

The following figure is a configuration example of some custom styles:

![customStyle](https://github.com/easemob/chat-api-examples/blob/main/Chat-iOS/chatuikitquickstart/customStyle.jpeg)

For details on more APIs, see APIs provided by EaseChatViewController and callback APIs in the EaseChatViewControllerDelegate protocol.