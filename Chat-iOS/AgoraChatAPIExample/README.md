# 快速发送 Agora Chat 消息

本文详细介绍如何建立一个简单的项目并使用 Agora Chat SDK 实现消息的发送和加入群组。

## 消息发送与接收流程

登录 Agora Chat 系统包括以下流程：
1. 客户端使用帐号和密码进行注册。
2. 客户端注册成功后，登录到 Chat 服务器。

![登录流程](https://web-cdn.agora.io/docs-files/1636443945728)

发送和接收点对点消息包括以下流程：
1. 客户端 A 发送点对点消息到 Chat 服务器。
2. Chat 服务器将消息发送到客户端 B。客户端 B 收到点对点消息。

## 前提条件
 - iOS 10 及以上版本
 - 有效的 Agora Chat 开发者账号

 ## 操作步骤

### 1.创建 Agora Chat 项目

参考以下步骤在 Xcode 中创建一个 iOS 平台下的 Single View App，项目设置如下：

1. Product Name 设为 AgoraChatExamplet。

2. Organization Identifier 设为 agorachat。
3. User Interface 选择 Storyboard。
4. Language 选择 Objective-C。

### 2.集成 Agora Chat SDK

选择以下任意一种方式将 Agora chat SDK 集成到你的项目中。本文使用方法 1 进行集成。

**方法 1：使用 pod 方式集成SDK**

1. 开始前确保你已安装 Cocoapods。参考 [Getting Started with CocoaPods](https://guides.cocoapods.org/using/getting-started.html#getting-started) 安装说明。
2. 在终端里进入项目根目录，并运行 `pod init` 命令。项目文件夹下会生成一个 `Podfile` 文本文件。
3. 打开 `Podfile` 文件，在podfile文件里添加相关SDK。注意将 `AgoraChatAPIExample` 替换为你的 Target 名称。

```objective-c
platform :ios, '11.0'

# Import CocoaPods sources
source 'https://github.com/CocoaPods/Specs.git'

target 'AgoraChatAPIExample' do
    pod 'AgoraChat' 
  	pod 'Masonry'
end
```
4. 在终端 Terminal cd 到 podfile 文件所在目录，执行如下命令集成 SDK。

```objective-c
pod install
```

5. 成功安装后，Terminal 中会显示 `Pod installation complete!`，此时项目文件夹下会生成一个 `xcworkspace` 文件，打开新生成的 `xcworkspace` 文件运行项目。

**方法 2：手动集成 SDK framework 包**

1. 下载最新版的 [Agora Chat SDK for iOS]([https://download.agora.io/sdk/release/Agora_Chat_SDK_for_iOS_v1.0.1.zip](https://download.agora.io/sdk/release/Agora_Chat_SDK_for_iOS_v1.0.0.zip)) 并解压。
2. 将 SDK 包内的 AgoraChat.framework 加入到项目中，AgoraChat.framework 包含 arm64，armv7，x86_64 指令集。
3. 选中项目的 Target，并在 General -> Frameworks,libraries,and... -> 将AgoraChat.framework 的 “Embed”选项设置为 “Embed & sign”。

### 3.添加权限

在项目 info.plist 中添加相关权限：
```
Privacy - Photo Library Usage Description //相机权限
App Transport Security Settings -> Allow Arbitrary Loads //开启网络服务
```

### 4.实现用户界面和资源文件

为了帮助你快速实现并理解相关功能，本文通过最简方式，在一个 ViewController 里实现以下操作：

- 用户注册
- 登录和退出
- 发送文本消息
- 发送图片
- 加入群组

在项目的 ViewController.m 文件里添加如下相关代码：

1. 导入头文件

```objective-c
#import <Masonry/Masonry.h>
#import "EMHttpRequest.h"
#import <AgoraChat/AgoraChat.h>
#import <AgoraChat/AgoraChatOptions+PrivateDeploy.h>
#import <Photos/Photos.h>
#import <MobileCoreServices/MobileCoreServices.h>
#import <AssetsLibrary/AssetsLibrary.h>
```
2. 实现相关代理并声明属性

```objective-c
@interface ViewController ()<UITextFieldDelegate, UIScrollViewDelegate, AgoraChatClientDelegate, AgoraChatManagerDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate>

@property (nonatomic, strong) UITextField *nameField; 
@property (nonatomic, strong) UITextField *pswdField;

@property (nonatomic, strong) UIButton *loginBtn;
@property (nonatomic, strong) UIButton *registerBtn;
@property (nonatomic, strong) UIButton *logoutBtn;

@property (nonatomic, strong) UITextField *conversationIdField;
@property (nonatomic, strong) UITextView *msgField;

@property (nonatomic, strong) UIButton *chatBtn;
@property (nonatomic, strong) UIButton *imgBtn;

@property (nonatomic, strong) UITextField *groupField;
@property (nonatomic, strong) UIButton *joinGroupBtn;

@property (nonatomic, strong) UITextField *chatroomField;
@property (nonatomic, strong) UIButton *joinChatroomBtn;

@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) UIView *bottomLine;
@property (nonatomic, strong) UITextView *resultView;

@property (nonatomic, strong) UIImagePickerController *imagePicker;
@property (nonatomic, strong) NSDateFormatter *formatter;

@end
```

3. 页面以及 SDK 初始化

   需要在 "appkey" 处填入自己创建的 appkey。

```objective-c
- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTapTableViewAction:)];
    [self.view addGestureRecognizer:tap];
    [self initSdk];
    [self _setupSubviews];
    // Do any additional setup after loading the view.
}

- (void)initSdk
{
    AgoraChatOptions *options = [AgoraChatOptions optionsWithAppkey:@"41117440#383391"];
    options.enableConsoleLog = YES;
    [[AgoraChatClient sharedClient] initializeSDKWithOptions:options];

    [[AgoraChatClient sharedClient] addDelegate:self delegateQueue:nil];
    [[AgoraChatClient sharedClient].chatManager addDelegate:self delegateQueue:nil];
}
```

4. 加载页面元素

```objective-c
- (void)handleTapTableViewAction:(UITapGestureRecognizer *)aTap
{
    if (aTap.state == UIGestureRecognizerStateEnded) {
        [self.view endEditing:YES];
    }
}

-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)_setupSubviews
{
    self.scrollView = [[UIScrollView alloc]init];
    self.scrollView.accessibilityActivationPoint = CGPointMake(0, 0);
    self.scrollView.backgroundColor = [UIColor whiteColor];
    self.scrollView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    self.scrollView.scrollsToTop = YES;
    self.scrollView.bounces = NO;
    self.scrollView.delegate = self;
    [self.view addSubview:self.scrollView];
    [self.scrollView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.right.equalTo(self.view);
        make.height.mas_equalTo(@(self.view.bounds.size.height / 3 * 2));
    }];
    
    self.bottomLine = [[UIView alloc] init];
    _bottomLine.backgroundColor = [UIColor blackColor];
    _bottomLine.alpha = 0.1;
    [self.view addSubview:self.bottomLine];
    [self.bottomLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.scrollView.mas_bottom);
        make.left.equalTo(self.view);
        make.right.equalTo(self.view);
        make.height.equalTo(@3);
    }];
    
    self.resultView = [[UITextView alloc]init];
    self.resultView.backgroundColor = [UIColor whiteColor];
    self.resultView.textColor = [UIColor blackColor];
    self.resultView.font = [UIFont systemFontOfSize:14.0];
    self.resultView.editable = NO;
    self.resultView.scrollEnabled = YES;
    self.resultView.textAlignment = NSTextAlignmentLeft;
    self.resultView.layoutManager.allowsNonContiguousLayout = NO;
    [self.view addSubview:self.resultView];
    [self.resultView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.bottomLine.mas_bottom);
        make.left.right.bottom.equalTo(self.view);
    }];
    
    self.nameField = [[UITextField alloc] init];
    self.nameField.backgroundColor = [UIColor systemGrayColor];
    self.nameField.delegate = self;
    self.nameField.borderStyle = UITextBorderStyleNone;
    NSAttributedString *attrStr = [[NSAttributedString alloc] initWithString:@"user ID" attributes:@{NSForegroundColorAttributeName:[UIColor whiteColor]}];
    self.nameField.attributedPlaceholder = attrStr;
    self.nameField.returnKeyType = UIReturnKeyDone;
    self.nameField.font = [UIFont systemFontOfSize:17];
    self.nameField.textColor = [UIColor whiteColor];
    self.nameField.layer.cornerRadius = 5;
    self.nameField.layer.borderWidth = 1;
    self.nameField.layer.borderColor = [UIColor lightGrayColor].CGColor;
    [self.scrollView addSubview:self.nameField];
    [self.nameField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.scrollView).offset(30);
        make.top.equalTo(self.scrollView).offset(50);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@150);
    }];
    
    self.pswdField = [[UITextField alloc] init];
    self.pswdField.backgroundColor = [UIColor systemGrayColor];
    self.pswdField.delegate = self;
    self.pswdField.borderStyle = UITextBorderStyleNone;
    NSAttributedString *psdAttrStr = [[NSAttributedString alloc] initWithString:@"password" attributes:@{NSForegroundColorAttributeName:[UIColor whiteColor]}];
    self.pswdField.attributedPlaceholder = psdAttrStr;
    self.pswdField.font = [UIFont systemFontOfSize:17];
    self.pswdField.textColor = [UIColor whiteColor];
    self.pswdField.returnKeyType = UIReturnKeyDone;
    self.pswdField.layer.cornerRadius = 5;
    self.pswdField.layer.borderWidth = 1;
    self.pswdField.layer.borderColor = [UIColor lightGrayColor].CGColor;
    [self.scrollView addSubview:self.pswdField];
    [self.pswdField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.nameField.mas_right).offset(15);
        make.top.equalTo(self.nameField);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@150);
    }];
    
    self.registerBtn = [[UIButton alloc] init];
    self.registerBtn.clipsToBounds = YES;
    self.registerBtn.layer.cornerRadius = 5;
    self.registerBtn.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
    self.registerBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [self.registerBtn setTitle:@"Sign up" forState:UIControlStateNormal];
    [self.registerBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.registerBtn addTarget:self action:@selector(registerAction) forControlEvents:UIControlEventTouchUpInside];
    [self.scrollView addSubview:self.registerBtn];
    [self.registerBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.nameField);
        make.top.equalTo(self.nameField.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@150);
    }];
    
    self.loginBtn = [[UIButton alloc] init];
    self.loginBtn.clipsToBounds = YES;
    self.loginBtn.layer.cornerRadius = 5;
    self.loginBtn.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
    self.loginBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [self.loginBtn setTitle:@"Sign in" forState:UIControlStateNormal];
    [self.loginBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.loginBtn addTarget:self action:@selector(loginAction) forControlEvents:UIControlEventTouchUpInside];
    [self.scrollView addSubview:self.loginBtn];
    [self.loginBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.registerBtn);
        make.top.equalTo(self.registerBtn.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@150);
    }];
    
    self.logoutBtn = [[UIButton alloc] init];
    self.logoutBtn.clipsToBounds = YES;
    self.logoutBtn.layer.cornerRadius = 5;
    self.logoutBtn.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
    self.logoutBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [self.logoutBtn setTitle:@"Sign out" forState:UIControlStateNormal];
    [self.logoutBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.logoutBtn addTarget:self action:@selector(logoutAction) forControlEvents:UIControlEventTouchUpInside];
    [self.scrollView addSubview:self.logoutBtn];
    [self.logoutBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.loginBtn.mas_right).offset(15);
        make.top.equalTo(self.loginBtn);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@150);
    }];
    
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
    [self.scrollView addSubview:self.conversationIdField];
    [self.conversationIdField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.scrollView).offset(30);
        make.top.equalTo(self.logoutBtn.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@320);
    }];
    
    self.msgField = [[UITextView alloc] init];
    //self.msgField.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
    self.msgField.backgroundColor = [UIColor systemGrayColor];
    self.msgField.text = @"message content";
    self.msgField.textColor = [UIColor whiteColor];
    self.msgField.font = [UIFont systemFontOfSize:17];
    self.msgField.returnKeyType = UIReturnKeyDone;
    self.msgField.layer.cornerRadius = 5;
    self.msgField.layer.borderWidth = 1;
    self.msgField.layer.borderColor = [UIColor lightGrayColor].CGColor;
    self.msgField.scrollEnabled = YES;
    self.msgField.textAlignment = NSTextAlignmentLeft;
    [self.scrollView addSubview:self.msgField];
    [self.msgField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.scrollView).offset(30);
        make.top.equalTo(self.conversationIdField.mas_bottom).offset(20);
        make.height.mas_equalTo(@150);
        make.width.mas_equalTo(@320);
    }];
    
    self.chatBtn = [[UIButton alloc] init];
    self.chatBtn.clipsToBounds = YES;
    self.chatBtn.layer.cornerRadius = 5;
    self.chatBtn.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
    self.chatBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [self.chatBtn setTitle:@"send" forState:UIControlStateNormal];
    [self.chatBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.chatBtn addTarget:self action:@selector(chatAction) forControlEvents:UIControlEventTouchUpInside];
    [self.scrollView addSubview:self.chatBtn];
    [self.chatBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.scrollView).offset(30);
        make.top.equalTo(self.msgField.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@150);
    }];
    
    self.imgBtn = [[UIButton alloc] init];
    self.imgBtn.clipsToBounds = YES;
    self.imgBtn.layer.cornerRadius = 5;
    self.imgBtn.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
    self.imgBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [self.imgBtn setTitle:@"send image" forState:UIControlStateNormal];
    [self.imgBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.imgBtn addTarget:self action:@selector(sendImageMsg) forControlEvents:UIControlEventTouchUpInside];
    [self.scrollView addSubview:self.imgBtn];
    [self.imgBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.scrollView).offset(30);
        make.top.equalTo(self.chatBtn.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@150);
    }];
    
    self.groupField = [[UITextField alloc] init];
    self.groupField.backgroundColor = [UIColor systemGrayColor];
    self.groupField.delegate = self;
    self.groupField.borderStyle = UITextBorderStyleNone;
    NSAttributedString *groupAttrStr = [[NSAttributedString alloc] initWithString:@"group ID" attributes:@{NSForegroundColorAttributeName:[UIColor whiteColor]}];
    self.groupField.attributedPlaceholder = groupAttrStr;
    self.groupField.font = [UIFont systemFontOfSize:17];
    self.groupField.textColor = [UIColor whiteColor];
    self.groupField.returnKeyType = UIReturnKeyDone;
    self.groupField.layer.cornerRadius = 5;
    self.groupField.layer.borderWidth = 1;
    self.groupField.layer.borderColor = [UIColor lightGrayColor].CGColor;
    [self.scrollView addSubview:self.groupField];
    [self.groupField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.scrollView).offset(30);
        make.top.equalTo(self.imgBtn.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@320);
    }];
    
    self.joinGroupBtn = [[UIButton alloc] init];
    self.joinGroupBtn.clipsToBounds = YES;
    self.joinGroupBtn.layer.cornerRadius = 5;
    self.joinGroupBtn.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
    self.joinGroupBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [self.joinGroupBtn setTitle:@"join group" forState:UIControlStateNormal];
    [self.joinGroupBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.joinGroupBtn addTarget:self action:@selector(joinGroup) forControlEvents:UIControlEventTouchUpInside];
    [self.scrollView addSubview:self.joinGroupBtn];
    [self.joinGroupBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.groupField);
        make.top.equalTo(self.groupField.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@150);
        make.bottom.equalTo(self.scrollView).offset(-20);
    }];
}
```

### 5.实现注册登录消息收发与加入群组等逻辑

1. 在 ViewController 文件里添加如下代码逻辑

```objective-c
- (void)registerAction
{
    [self.view endEditing:YES];

    NSString *name = [self.nameField.text lowercaseString];
    NSString *pswd = [self.pswdField.text lowercaseString];
    
    if ([name length] == 0 || [pswd length] == 0) {
        [self printLog:@"username or password is null"];
        return;
    }
    
    __weak typeof(self) weakself = self;
    //register unify token user
    [[EMHttpRequest sharedManager] registerToApperServer:name pwd:pswd completion:^(NSInteger statusCode, NSString * _Nonnull response) {
        dispatch_async(dispatch_get_main_queue(),^{
            NSString *alertStr = @"login.signup.fail";
            if (response != nil) {
                NSData *responseData = [response dataUsingEncoding:NSUTF8StringEncoding];
                NSDictionary *responsedict = [NSJSONSerialization JSONObjectWithData:responseData options:0 error:nil];
                if (responsedict != nil) {
                    NSString *result = [responsedict objectForKey:@"code"];
                    if ([result isEqualToString:@"RES_OK"]) {
                        alertStr = NSLocalizedString(@"login.signup.success", @"Sign up success");
                    }
                } else {
                    alertStr = NSLocalizedString(@"login.signup.failure", @"Sign up failure");
                }
            } else {
                alertStr = NSLocalizedString(@"login.signup.failure", @"Sign up failure");
            }
            [weakself printLog:alertStr];
        });
    }];
    
}

- (void)loginAction
{
    if (AgoraChatClient.sharedClient.isLoggedIn) {
        [self logoutAction];
    }
    [self.view endEditing:YES];
    
    NSString *name = [self.nameField.text lowercaseString];
    NSString *pswd = [self.pswdField.text lowercaseString];
    
    if (name.length == 0 || pswd.length == 0) {
        [self printLog:@"username or password is null"];
        return;
    }
    
    __weak typeof(self) weakself = self;
    void (^finishBlock) (NSString *aName, AgoraChatError *aError) = ^(NSString *aName, AgoraChatError *aError) {
        if (!aError) {
            [weakself printLog:[NSString stringWithFormat:@"login success ! name : %@",aName]];
            return ;
        }
        
        [weakself printLog:[NSString stringWithFormat:@"login fail ! errorDes : %@",aError.errorDescription]];
    };

    [[EMHttpRequest sharedManager] loginToApperServer:name pwd:pswd completion:^(NSInteger statusCode, NSString * _Nonnull response) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (response && response.length > 0) {
                NSData *responseData = [response dataUsingEncoding:NSUTF8StringEncoding];
                NSDictionary *responsedict = [NSJSONSerialization JSONObjectWithData:responseData options:0 error:nil];
                NSString *token = [responsedict objectForKey:@"accessToken"];
                NSString *loginName = [responsedict objectForKey:@"chatUserName"];
                if (token && token.length > 0) {
                    [weakself printLog:@"login appserver success !"];
                    [[AgoraChatClient sharedClient] loginWithUsername:loginName agoraToken:token completion:finishBlock];
                } else {
                    [weakself printLog:@"parseing token fail !"];
                }
            } else {
                [weakself printLog:@"login appserver fail !"];
            }
        });
    }];
}

- (void)logoutAction
{
    AgoraChatError *error = [[AgoraChatClient sharedClient] logout:YES];
    [self printLog:[NSString stringWithFormat:@"logout result : %@",!error ? @"success !" : error.errorDescription]];
}

- (void)_sendMessageWithBody:(AgoraChatMessageBody *)body
                        ext:(NSDictionary * __nullable)aExt
{
    NSString *from = [[AgoraChatClient sharedClient] currentUsername];
    NSString *to = self.conversationIdField.text;
    if (to.length == 0) {
        [self printLog:@"conversationId is null !"];
        return;
    }
    
    AgoraChatMessage *message = [[AgoraChatMessage alloc] initWithConversationID:to from:from to:to body:body ext:nil];
    message.chatType = AgoraChatTypeChat;
    
    __weak typeof(self) weakself = self;
    [[AgoraChatClient sharedClient].chatManager sendMessage:message progress:nil completion:^(AgoraChatMessage *message, AgoraChatError *error) {
        if (!error) {
            if (message.body.type == AgoraChatMessageBodyTypeText) {
                AgoraChatTextMessageBody *body = (AgoraChatTextMessageBody *)message.body;
                [weakself printLog:[NSString stringWithFormat:@"send message success：%@",body.text]];
            } else {
                [weakself printLog:[NSString stringWithFormat:@"send message success ! messageType : %@",[weakself getBodyType:message.body.type]]];
            }
        } else {
            [weakself printLog:[NSString stringWithFormat:@"send message fail ! errDesc : %@",error.errorDescription]];
        }
    }];
}

- (void)chatAction
{
    [self.view endEditing:YES];
    if (!AgoraChatClient.sharedClient.isLoggedIn) {
        [self printLog:@"not loggin"];
        return;
    }
    AgoraChatMessageBody *body = [[AgoraChatTextMessageBody alloc] initWithText:self.msgField.text];
    [self _sendMessageWithBody:body ext:nil];
}

- (void)sendImageMsg
{
    PHAuthorizationStatus permissions = -1;
    if (@available(iOS 14, *)) {
        permissions = PHAuthorizationStatusLimited;
    }
    [PHPhotoLibrary requestAuthorization:^(PHAuthorizationStatus status) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (status == permissions) {
                self.imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
                self.imagePicker.mediaTypes = @[(NSString *)kUTTypeImage];
                [self presentViewController:self.imagePicker animated:YES completion:nil];
            }
            if (status == PHAuthorizationStatusAuthorized) {
                self.imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
                self.imagePicker.mediaTypes = @[(NSString *)kUTTypeImage];
                [self presentViewController:self.imagePicker animated:YES completion:nil];
            }
            if (status == PHAuthorizationStatusDenied) {
                [self printLog:@"Access to albums is not allowed"];
            }
            if (status == PHAuthorizationStatusRestricted) {
                [self printLog:@"Access to the album is not authorized"];
            }
        });
    }];
}

#pragma mark - UIImagePickerControllerDelegate

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    NSURL *url = info[UIImagePickerControllerReferenceURL];
    if (url == nil) {
        UIImage *orgImage = info[UIImagePickerControllerOriginalImage];
        NSData *data = UIImageJPEGRepresentation(orgImage, 1);
        [self _sendImageDataAction:data];
    } else {
        if ([[UIDevice currentDevice].systemVersion doubleValue] >= 9.0f) {
            PHFetchResult *result = [PHAsset fetchAssetsWithALAssetURLs:@[url] options:nil];
            [result enumerateObjectsUsingBlock:^(PHAsset *asset , NSUInteger idx, BOOL *stop){
                if (asset) {
                    [[PHImageManager defaultManager] requestImageDataForAsset:asset options:nil resultHandler:^(NSData *data, NSString *uti, UIImageOrientation orientation, NSDictionary *dic){
                        if (data != nil) {
                            [self _sendImageDataAction:data];
                        } else {
                            [self printLog:@"Picture is too big, please select another picture !"];
                        }
                    }];
                }
            }];
        } else {
            ALAssetsLibrary *alasset = [[ALAssetsLibrary alloc] init];
            [alasset assetForURL:url resultBlock:^(ALAsset *asset) {
                if (asset) {
                    ALAssetRepresentation* assetRepresentation = [asset defaultRepresentation];
                    Byte *buffer = (Byte*)malloc((size_t)[assetRepresentation size]);
                    NSUInteger bufferSize = [assetRepresentation getBytes:buffer fromOffset:0.0 length:(NSUInteger)[assetRepresentation size] error:nil];
                    NSData *fileData = [NSData dataWithBytesNoCopy:buffer length:bufferSize freeWhenDone:YES];
                    [self _sendImageDataAction:fileData];
                }
            } failureBlock:NULL];
        }
    }
    
    [picker dismissViewControllerAnimated:YES completion:nil];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    [self.imagePicker dismissViewControllerAnimated:YES completion:nil];
}

- (void)_sendImageDataAction:(NSData *)aImageData
{
    AgoraChatImageMessageBody *body = [[AgoraChatImageMessageBody alloc] initWithData:aImageData displayName:@"image"];
    [self _sendMessageWithBody:body ext:nil];
}

- (UIImagePickerController *)imagePicker
{
    if (_imagePicker == nil) {
        _imagePicker = [[UIImagePickerController alloc] init];
        _imagePicker.modalPresentationStyle = UIModalPresentationOverFullScreen;
        _imagePicker.delegate = self;
    }
    
    return _imagePicker;
}

- (void)joinGroup
{
    NSString *groupId = self.groupField.text;
    if (groupId.length == 0) {
        groupId = @"xxxxxxxxxxxxxxxxxx";
    }
    
    __weak typeof(self) weakself = self;
    [[AgoraChatClient sharedClient].groupManager joinPublicGroup:groupId completion:^(AgoraChatGroup *aGroup, AgoraChatError *aError) {
        if (!aError) {
            [weakself printLog:[NSString stringWithFormat:@"join group success ! groupID : %@",aGroup.groupId]];
        } else {
            [weakself printLog:[NSString stringWithFormat:@"join group fail ! errDesc : %@",aError.errorDescription]];
        }
    }];
}

//receive message
- (void)messagesDidReceive:(NSArray *)aMessages
{
    __weak typeof(self) weakself = self;
    for (int i = 0; i < [aMessages count]; i++) {
        AgoraChatMessage *msg = aMessages[i];
        if(msg.body.type == AgoraChatMessageBodyTypeText) {
            AgoraChatTextMessageBody *body = (AgoraChatTextMessageBody*)msg.body;
            [weakself printLog:[NSString stringWithFormat:@"receive a AgoraChatMessageBodyTypeText message :%@, from : %@",body.text,msg.from]];
        } else {
            [weakself printLog:[NSString stringWithFormat:@"receive a %@ message, from : %@",[weakself getBodyType:msg.body.type],msg.from]];
        }
    }
}

- (NSString *)getBodyType:(AgoraChatMessageBodyType)bodyType
{
    NSString *type = @"";
    switch (bodyType) {
        case AgoraChatMessageBodyTypeImage:
            type = @"AgoraChatMessageBodyTypeImage";
            break;
        case AgoraChatMessageBodyTypeVideo:
            type = @"AgoraChatMessageBodyTypeVideo";
            break;
        case AgoraChatMessageBodyTypeLocation:
            type = @"AgoraChatMessageBodyTypeLocation";
            break;
        case AgoraChatMessageBodyTypeVoice:
            type = @"AgoraChatMessageBodyTypeVoice";
            break;
        case AgoraChatMessageBodyTypeFile:
            type = @"AgoraChatMessageBodyTypeFile";
            break;
        case AgoraChatMessageBodyTypeCmd:
            type = @"AgoraChatMessageBodyTypeCmd";
            break;
        case AgoraChatMessageBodyTypeCustom:
            type = @"AgoraChatMessageBodyTypeCustom";
            break;
        default:
            break;
    }
    return type;
}

- (void)tokenWillExpire:(int)aErrorCode
{
    [self printLog:[NSString stringWithFormat:@"token %@", aErrorCode == AgoraChatErrorTokeWillExpire ? @"TokeWillExpire" : @"TokeExpire"]];
    if (aErrorCode == AgoraChatErrorTokeWillExpire) {
        [self printLog:[NSString stringWithFormat:@"========= token expire rennew token ! code : %d",aErrorCode]];
        NSString *name = [self.nameField.text lowercaseString];
        NSString *pswd = [self.pswdField.text lowercaseString];
        [[EMHttpRequest sharedManager] loginToApperServer:name pwd:pswd completion:^(NSInteger statusCode, NSString * _Nonnull response) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if (response && response.length > 0) {
                    NSData *responseData = [response dataUsingEncoding:NSUTF8StringEncoding];
                    NSDictionary *responsedict = [NSJSONSerialization JSONObjectWithData:responseData options:0 error:nil];
                    NSString *token = [responsedict objectForKey:@"accessToken"];
                    if (token && token.length > 0) {
                        if (aErrorCode == AgoraChatErrorTokeWillExpire) {
                            AgoraChatError *error = [[AgoraChatClient sharedClient] renewToken:token];
                            if (error) {
                                [self printLog:[NSString stringWithFormat:@"renew token fail ！ errDesc : %@",error.errorDescription]];
                            } else {
                                [self printLog:@"renew token success ！"];
                            }
                        }
                    } else {
                        [self printLog:@"parseing token fail !"];
                    }
                } else {
                    [self printLog:@"login appserver fail !"];
                }
            });
        }];
    }
}

- (void)tokenDidExpire:(int)aErrorCode
{
  	[[AgoraChatClient sharedClient] logout:NO];
    [self printLog:[NSString stringWithFormat:@"token %@", aErrorCode == AgoraChatErrorTokeWillExpire ? @"TokeWillExpire" : @"TokeExpire"]];
    if (aErrorCode == AgoraChatErrorTokenExpire || aErrorCode == 401) {
        [self loginAction];
        return;
    }
}

- (void)printLog:(NSString *)log
{
    __weak typeof(self) weakself = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        if (weakself.resultView.text.length > 0) {
            weakself.resultView.text = [weakself.resultView.text stringByAppendingString:@"\r\n"];
        }
        
        NSString *currentTS = [weakself.formatter stringFromDate:[NSDate date]];
        NSString *logPrefix = [NSString stringWithFormat:@"[%@]: ",currentTS];
        NSString *logStr = [NSString stringWithFormat:@"%@%@",logPrefix,log];
        weakself.resultView.text = [weakself.resultView.text stringByAppendingString:logStr];
        
        long allStrCount = self.resultView.text.length; 
        [weakself.resultView scrollRangeToVisible:NSMakeRange(0, allStrCount)];
    });
}

- (NSDateFormatter *)formatter
{
    if (!_formatter) {
        _formatter = [[NSDateFormatter alloc]init];
        [_formatter setDateFormat:@"YYYY-MM-dd HH:mm:ss.SSS"];
    }
    
    return _formatter;
}
```
2. 此外 ViewController 使用到了访问我们 AppServer 进行登陆的工具类 EMHttpRequest，还需在项目中导入该工具类。

- EMHttpRequest.h
```objective-c
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface EMHttpRequest : NSObject

+ (instancetype)sharedManager;

- (void)registerToApperServer:(NSString *)uName
                          pwd:(NSString *)pwd
                   completion:(void (^)(NSInteger statusCode, NSString *response))aCompletionBlock;

- (void)loginToApperServer:(NSString *)uName
                       pwd:(NSString *)pwd
                completion:(void (^)(NSInteger statusCode, NSString *response))aCompletionBlock;

@end

NS_ASSUME_NONNULL_END
```
- EMHttpRequest.m
```objective-c
#import "EMHttpRequest.h"

@interface EMHttpRequest() <NSURLSessionDelegate>
@property (readonly, nonatomic, strong) NSURLSession *session;
@end
@implementation EMHttpRequest

+ (instancetype)sharedManager
{
    static dispatch_once_t onceToken;
    static EMHttpRequest *sharedInstance;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[EMHttpRequest alloc] init];
    });
    
    return sharedInstance;
}

- (instancetype)init
{
    if (self = [super init]) {
        NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
        configuration.timeoutIntervalForRequest = 120;
        _session = [NSURLSession sessionWithConfiguration:configuration
                                                 delegate:self
                                            delegateQueue:[NSOperationQueue mainQueue]];
    }
    return self;
}

- (void)registerToApperServer:(NSString *)uName
                          pwd:(NSString *)pwd
                   completion:(void (^)(NSInteger statusCode, NSString *aUsername))aCompletionBlock
{
    //NSString *hkURl = @"https://hk-test.easemob.com/app/chat/user/register";
    NSURL *url = [NSURL URLWithString:@"https://a41.easemob.com/app/chat/user/register"];
    NSMutableURLRequest *request = [NSMutableURLRequest
                                                requestWithURL:url];
    request.HTTPMethod = @"POST";
    
    NSMutableDictionary *headerDict = [[NSMutableDictionary alloc]init];
    [headerDict setObject:@"application/json" forKey:@"Content-Type"];
    request.allHTTPHeaderFields = headerDict;
    
    NSMutableDictionary *dict = [[NSMutableDictionary alloc]init];
    [dict setObject:uName forKey:@"userAccount"];
    [dict setObject:pwd forKey:@"userPassword"];
    request.HTTPBody = [NSJSONSerialization dataWithJSONObject:dict options:0 error:nil];
    NSURLSessionDataTask *task = [self.session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        NSString *responseData = data ? [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding] : nil;
        if (aCompletionBlock) {
            aCompletionBlock(((NSHTTPURLResponse*)response).statusCode, responseData);
        }
    }];
    [task resume];
}

- (void)loginToApperServer:(NSString *)uName
                       pwd:(NSString *)pwd
                completion:(void (^)(NSInteger statusCode, NSString *response))aCompletionBlock
{
    //NSString *hkURl = @"https://hk-test.easemob.com/app/chat/user/login";
    NSURL *url = [NSURL URLWithString:@"https://a41.easemob.com/app/chat/user/login"];
    NSMutableURLRequest *request = [NSMutableURLRequest
                                                requestWithURL:url];
    request.HTTPMethod = @"POST";
    
    NSMutableDictionary *headerDict = [[NSMutableDictionary alloc]init];
    [headerDict setObject:@"application/json" forKey:@"Content-Type"];
    request.allHTTPHeaderFields = headerDict;
    
    NSMutableDictionary *dict = [[NSMutableDictionary alloc]init];
    [dict setObject:uName forKey:@"userAccount"];
    [dict setObject:pwd forKey:@"userPassword"];
    request.HTTPBody = [NSJSONSerialization dataWithJSONObject:dict options:0 error:nil];
    NSURLSessionDataTask *task = [self.session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        NSString *responseData = data ? [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding] : nil;
        if (aCompletionBlock) {
            aCompletionBlock(((NSHTTPURLResponse*)response).statusCode, responseData);
        }
    }];
    [task resume];
}

- (void)URLSession:(NSURLSession *)session didReceiveChallenge:(NSURLAuthenticationChallenge *)challenge completionHandler:(void (^)(NSURLSessionAuthChallengeDisposition, NSURLCredential * _Nullable))completionHandler
{
    if([challenge.protectionSpace.authenticationMethod isEqualToString:NSURLAuthenticationMethodServerTrust]){
            NSURLCredential *credential = [NSURLCredential credentialForTrust:challenge.protectionSpace.serverTrust];
            if(completionHandler)
                completionHandler(NSURLSessionAuthChallengeUseCredential,credential);
        }
}

@end
```

### 6.编译并运行项目

使用 Xcode 在模拟器或真机上编译并运行项目。运行成功之后，你可以进行以下操作：

- 注册，登录和退出
- 输入会话 ID
- 发送文本消息
- 发送图片消息
- 加入群组

功能执行结果在页面下半部分会有相关日志记录展示。
