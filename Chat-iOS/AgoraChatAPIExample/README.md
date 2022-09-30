# Rapidly Send a Message

This page shows how to set up a simple project and use the Agora Chat SDK to send a message and join a group.

## Message sending and receiving process

The process of logging in to Agora Chat is as follows:

1. The client registers an account via the App Server.
2. The client obtains the user token via the App Server and logs in to the Agora Chat with the user ID and user token.

![Login process](https://web-cdn.agora.io/docs-files/1636443945728)

The process of sending and receiving a one-to-one message is as follows:

1. Client A sends a message to the Agora Chat server.
2. The Agora Chat server sends the message to client B. Client B receives the message.

## Prerequisites

 - iOS 11 and above
 - A valid developer account of Agora Chat 

## Integration procedure

### 1. Create an Agora Chat project

Create a Single View app in Xcode on the iOS platform by doing the following:

1. Set `Product Name` to any name you like.
2. Set `Organization Identifier` to the identifier of your organization.
2. Select `Storyboard` for `User Interface`.
3. Select `Objective-C` for `Language`.

### 2. Integrate the Agora Chat SDK

You can integrate the Agora chat SDK using either of the following methods. Here, the first method is used.

**Method 1: Integrate the Agora chat SDK by using a pod**

1. Install CocoaPods if you have not. For details, see [Getting Started with CocoaPods](https://guides.cocoapods.org/using/getting-started.html#getting-started).
2. In the Terminal, navigate to the project root directory and run the `pod init` command. Then the text file `Podfile` will be generated in the project folder.
3. Open the `Podfile` file and add the SDK to the file. Remember to replace `chatuikitquickstart` with the target name of your project.

```objective-c
platform :ios, '11.0'

# Import CocoaPods sources
source 'https://github.com/CocoaPods/Specs.git'

target 'AgoraChatAPIExample' do
    pod 'AgoraChat' 
  	pod 'Masonry'
end
```

4. Run the `cd` command in the Terminal to switch to the directory where the `Podfile` file is located. Then run the following command to integrate the SDK.

```objective-c
pod install
```

5. After the pod installation is complete, the message `Pod installation complete!` will be displayed in the Terminal. At this time, the `xcworkspace` file will be generated in the project folder. You can open this new file to run the project.

**Method 2: Manually integrate the SDK framework package**

1. Download the latest [Agora Chat SDK for iOS]([https://download.agora.io/sdk/release/Agora_Chat_SDK_for_iOS_v1.0.1.zip](https://download.agora.io/sdk/release/Agora_Chat_SDK_for_iOS_v1.0.0.zip)) and decompress it.

2. Copy AgoraChat.framework in the SDK package to the project folder. AgoraChat.framework contains arm64, armv7, and x86_64 instruction sets.

3. Open Xcode and navigate to **TARGETS > Project Name > General > Frameworks, Libraries, and Embedded Content**.

4. Click **+ > Add Other… > Add Files** to add AgoraChat.framework and set the **Embed** property to **Embed & Sign**. Then the project automatically links to the required system library.

### 3. Add privileges

Add related privileges in the `info.plist` project:

```
Privacy - Photo Library Usage Description //Album privileges.
App Transport Security Settings -> Allow Arbitrary Loads // Enable the network service.
```

### 4. Implement the UI and resource files

This page implements the following functions to help you rapidly implement and understand related functions:

- Register a user account.
- Log in and log out of the chat app.
- Send a text message.
- Send an image message.
- Join a group.

Add the following code in ViewController.m:

1. Import header files.

```objective-c
#import <Masonry/Masonry.h>
#import "AgoraChatHttpRequest.h"
#import <AgoraChat/AgoraChat.h>
#import <AgoraChat/AgoraChatOptions+PrivateDeploy.h>
#import <Photos/Photos.h>
#import <MobileCoreServices/MobileCoreServices.h>
#import <AssetsLibrary/AssetsLibrary.h>
```

2. Implement the delegates and declare attributes.

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

3. Initialize the SDK.

Replace "appkey" with your own App Key.

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

4. Create UI controls.

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

### 5. Implement registration, login, message sending and receiving, and joining groups

1. Add the following code logic to the ViewController file.

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
    [[AgoraChatHttpRequest sharedManager] registerToApperServer:name pwd:pswd completion:^(NSInteger statusCode, NSString * _Nonnull response) {
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

    [[AgoraChatHttpRequest sharedManager] loginToApperServer:name pwd:pswd completion:^(NSInteger statusCode, NSString * _Nonnull response) {
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
        [[AgoraChatHttpRequest sharedManager] loginToApperServer:name pwd:pswd completion:^(NSInteger statusCode, NSString * _Nonnull response) {
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
                        [self printLog:@"parsing token fail !"];
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

2. The ViewController uses the tool class `AgoraChatHttpRequest` that implements the login to the Agora Chat via the App Server. You need to import this tool class to the project.

- AgoraChatHttpRequest.h
```objective-c
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface AgoraChatHttpRequest : NSObject

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
- AgoraChatHttpRequest.m
```objective-c
#import "AgoraChatHttpRequest.h"

@interface AgoraChatHttpRequest() <NSURLSessionDelegate>
@property (readonly, nonatomic, strong) NSURLSession *session;
@end
@implementation AgoraChatHttpRequest

+ (instancetype)sharedManager
{
    static dispatch_once_t onceToken;
    static AgoraChatHttpRequest *sharedInstance;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[AgoraChatHttpRequest alloc] init];
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
    
    NSURL *url = [NSURL URLWithString:@"https://a41.chat.agora.io/app/chat/user/register"];
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
    
    NSURL *url = [NSURL URLWithString:@"https://a41.chat.agora.io/app/chat/user/login"];
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

### 6. Compile and run the project

Use Xcode to compile and run the project on the simulator or physical device. After the project runs successfully, you can perform the following operations:

- Register an account and log in to or log out of Agora Chat.
- Type the conversation ID.
- Send a text message.
- Send an image message.
- Join a group.

The operation result is indicated in the logs shown in the lower part of the page.

