---

---

# 使用 chat-uikit 快速搭建会话列表和会话页面

本文介绍如何将chat-uikit  应用在您的项目中并快速搭建出会话列表和聊天页面。

## 消息发送与接收流程

登录 Agora Chat 系统包括以下流程：

1. 客户端使用帐号和密码进行注册/登录；
2. AppServer 将客户端用户账号注册到 Chat 服务，返回给客户端账号、登录 token；
3. 客户端使用 AppServer 返回的 【登录 token】 和 【用户账号】 登录到 Chat 服务器。

发送和接收点对点消息包括以下流程：

1. 客户端 A 发送点对点消息到 Chat 服务器。
2. Chat 服务器将消息发送到客户端 B。客户端 B 收到点对点消息。

## 前提条件

 - iOS 11 及以上版本
 - 有效的 Agora Chat 开发者账号

## 操作步骤

### 1.创建 chat-uikit 项目

参考以下步骤在 Xcode 中创建一个 iOS 平台下的 Single View App，项目设置如下：

1. Product Name 设为 `EaseChatKitExample`。

2. Organization Identifier 设为 `agorachat`。
3. User Interface 选择 Storyboard。
4. Language 选择 Objective-C。

### 2.集成 chat-uikit

选择以下任意一种方式将 chat-uikit 集成到你的项目中。本文使用方式 1 进行集成。

#### 方式 1：使用 pod 方式集成 chat-uikit

1. 开始前确保你已安装 Cocoapods。参考 [Getting Started with CocoaPods](https://guides.cocoapods.org/using/getting-started.html#getting-started) 安装说明。
2. 在终端里进入项目根目录，并运行 `pod init` 命令。项目文件夹下会生成一个 `Podfile` 文本文件。
3. 打开 `Podfile` 文件，在podfile文件里添加相关SDK。注意将 `AgoraChatAPIExample` 替换为你的 Target 名称。

```objective-c
platform :ios, '11.0'

# Import CocoaPods sources
source 'https://github.com/CocoaPods/Specs.git'

target 'EaseChatKitExample' do
    pod 'chat-uikit'
  	pod 'Masonry'
end
```

4. 在终端 Terminal cd 到 podfile 文件所在目录，执行如下命令集成 SDK。

```objective-c
pod install
```

5. 成功安装后，Terminal 中会显示 `Pod installation complete!`，此时项目文件夹下会生成一个 `xcworkspace` 文件，打开新生成的 `xcworkspace` 文件运行项目。

#### 方式 2：源码集成chat-uikit

源码仓库：https://github.com/AgoraIO-Usecase/AgoraChat-UIKit-ios.git

1. 下载最新版的 chat-uikit 源码；

2. 在项目的  `Podfile` 文件中添加 chat-uikit 依赖，路径指向 chat-uikit.podspec 文件所在目录；

   ```objective-c
   pod 'chat-uikit',  :path => "../AgoraChat-UIKit-ios"
   ```


path 指向本地 `chat-uikit.podspec` 文件所在目录。

3. 在终端 Terminal cd 到 podfile 文件所在目录，执行如下命令本地集成 chat-uikit 源码。

   ```objective-c
   pod install
   ```

### 3.添加权限

在项目 info.plist 中添加相关权限：

```xml
Privacy - Photo Library Usage Description //相册权限
Privacy - Microphone Usage Description //麦克风权限
Privacy - Camera Usage Description //相机权限
App Transport Security Settings -> Allow Arbitrary Loads //开启网络服务
```

### 4.实现聊天界面

为了帮助你快速实现并理解相关功能，本文通过最简方式，在项目的聊天页面 ChatViewController 里集成 chat-uikit 的聊天页面，并实现如下功能：

- 自动加载并展示历史消息;
- 会话页面接收消息并展示;
- 发送文本消息、图片消息、文件消息、视频消息、语音消息等。

1. 在项目的SceneDelegate.m 文件里添加如下相关代码进行 chat-uikit 初始化相关功能：

```objective-c
//导入头文件。
#import <chat-uikit/EaseChatKit.h>
#import "ChatViewController.h" //项目会话页面。
#import "AgoraLoginViewController.h" //登录页面。
```

```objective-c
//chat-uikit 初始化。
- (void)scene:(UIScene *)scene willConnectToSession:(UISceneSession *)session options:(UISceneConnectionOptions *)connectionOptions {
    // Use this method to optionally configure and attach the UIWindow `window` to the provided UIWindowScene `scene`.
    // If using a storyboard, the `window` property will automatically be initialized and attached to the scene.
    // This delegate does not imply the connecting scene or session are new (see `application:configurationForConnectingSceneSession` instead).
    
    AgoraChatOptions *options = [AgoraChatOptions optionsWithAppkey:@"61117440#460199"];
    options.enableConsoleLog = YES;
    options.usingHttpsOnly = YES;
    options.enableDeliveryAck = YES;
    options.isAutoLogin = NO;
  	//初始化 chat-uikit
    [EaseChatKitManager initWithAgoraChatOptions:options];
    
    UIWindowScene *windowScene = (UIWindowScene *)scene;
    self.window = [[UIWindow alloc] initWithWindowScene:windowScene];
    self.window.frame = windowScene.coordinateSpace.bounds;
    //登录页面
    self.window.rootViewController = [[AgoraLoginViewController alloc]init];
    [self.window makeKeyAndVisible];
}
```

2. 在加载会话页面之前必须先登录到 AgoraChat SDK，具体的登录页面实现可自行实现或参考 `EaseChatKitExample` -> `AgoraLoginViewController` 登录页面实现。

* 登录页面中注册实现逻辑请参考：https://github.com/easemob/chat-api-examples/blob/d06d34455c360c6dc21cce57c984aafc6dd13da4/iOS%20api-example/README.md?plain=1#L420-L455 
* 登录页面中登录实现逻辑请参考：https://github.com/easemob/chat-api-examples/blob/d06d34455c360c6dc21cce57c984aafc6dd13da4/iOS%20api-example/README.md?plain=1#L457-L500 
* 使用 AppServer 进行注册逻辑请参考：https://github.com/easemob/chat-api-examples/blob/d06d34455c360c6dc21cce57c984aafc6dd13da4/iOS%20api-example/README.md?plain=1#L827-L852
* 登录到 AppServer 时登录逻辑请参考：https://github.com/easemob/chat-api-examples/blob/d06d34455c360c6dc21cce57c984aafc6dd13da4/iOS%20api-example/README.md?plain=1#L854-L879

3. 登录成功之后跳转到项目的会话页面 ChatViewController.m

以下为头文件和对属性进行定义：

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

#import "ChatViewController.h"
#import <Masonry/Masonry.h>
#import "AgoraChat/AgoraChat.h"
#import <chat-uikit/EaseChatKit.h>

@interface ChatViewController ()<EaseChatViewControllerDelegate, UITextFieldDelegate>
@property (nonatomic, strong) EaseConversationModel *conversationModel;
@property (nonatomic, strong) AgoraChatConversation *conversation;
@property (nonatomic, strong) EaseChatViewController *chatController;
@property (nonatomic, strong) UITextField *conversationIdField;
@property (nonatomic, strong) UIButton *chatBtn;
@property (nonatomic, strong) UIButton *logoutBtn;

@end
```

加载页面元素：

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

在会话页面输入会话方 ID，点击 Chat 按钮显示 `EaseChatViewController` 会话页面：

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

会话页面可退出到登录页面更换登陆 ID：

```objective-c
- (void)logout
{
    [AgoraChatClient.sharedClient logout:YES completion:^(AgoraChatError *aError) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"loginStateChange" object:@NO];
    }];
}
```

### 5.编译并运行项目

使用 Xcode 在模拟器或真机上编译并运行项目。运行成功之后，你可以进行以下操作：

- 会话页面展示历史消息；
- 会话页面接收消息并展示；
- 会话页面发送消息，包含文本，图片，视频，附件，相机，语音，表情等消息。

### 6.自定义UI配置

chat-uikit 显示的是默认的UI样式，以下是对 chat-uikit 的样式进行自定义配置示例：

* 默认样式示例：

只需创建 EaseChatViewModel 实例，并作为参数传入聊天页面 EaseChatViewController 的构造方法。

```objective-c
EaseChatViewModel *viewModel = [[EaseChatViewModel alloc]init]; //默认样式
EaseChatViewController *chatController = [EaseChatViewController initWithConversationId:@"会话 ID" conversationType:AgoraChatConversationTypeChat chatViewModel:viewModel];
```

默认样式的聊天页面示例图：

<img src="/Users/zchong/Desktop/defaultStyle.jpeg" alt="defaultStyle" style="zoom:20%;" />

* 自定义样式配置示例：

创建 EaseChatViewModel 实例，修改该实例的可配置样式参数，将实例传入聊天页面 EaseChatViewController 的构造方法。

```objective-c
EaseChatViewModel *viewModel = [[EaseChatViewModel alloc]init];
viewModel.chatViewBgColor = [UIColor systemGrayColor];  //聊天页背景色
viewModel.inputMenuBgColor = [UIColor systemPinkColor]; //输入区背景色
viewModel.sentFontColor = [UIColor redColor];           //发送方文本颜色
viewModel.inputMenuStyle = EaseInputMenuStyleNoAudio;   //输入区菜单样式
viewModel.msgTimeItemFontColor = [UIColor blackColor];  //消息时间字体颜色
viewModel.msgTimeItemBgColor = [UIColor greenColor];    //消息时间区域背景色
EaseChatViewController *chatController = [EaseChatViewController initWithConversationId:@"会话 ID" conversationType:AgoraChatConversationTypeChat chatViewModel:viewModel];
```

部分自定义样式配置示例图：

<img src="/Users/zchong/Desktop/customStyle.jpeg" alt="customStyle" style="zoom:20%;" />

关于更多 API 介绍请参考 EaseChatViewController 提供的 API，以及 EaseChatViewControllerDelegate 协议中的回调方法 API。