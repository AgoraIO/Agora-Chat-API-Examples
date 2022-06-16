# 发送语音消息

本文介绍如何录音并使用 Agora Chat 发送语音消息。

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

### 1.实现用户界面和资源文件

为了帮助你快速实现并理解相关功能，本文通过最简方式，在一个 ViewController 里实现以下操作：

- 登陆 Agora Chat
- 录音并发送语音消息

在项目的 AudioMessageViewController.m 文件里添加如下相关代码：

1. 导入头文件

```objective-c
#import "AudioMessageViewController.h"
#import <Masonry/Masonry.h>
#import <AgoraChat/AgoraChat.h>
#import "AgoraAudioRecordUtil.h"
#import "EMHttpRequest.h"
```
2. 实现相关代理并声明属性

```objective-c
@interface AudioMessageViewController ()<UIScrollViewDelegate>

@property (nonatomic, strong) UITextField *nameField;
@property (nonatomic, strong) UITextField *pswdField;
@property (nonatomic, strong) UIButton *loginBtn;
@property (nonatomic, strong) UITextField *conversationIdField;
@property (nonatomic, strong) UIButton *recordBtn;

@property (nonatomic, strong) UIView *bottomLine;
@property (nonatomic, strong) UITextView *resultView;
@property (nonatomic, strong) NSDateFormatter *formatter;

@end
```

3. 页面以及 SDK 初始化


```objective-c
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTapTableViewAction:)];
    [self.view addGestureRecognizer:tap];
    [self initSdk];
    [self _setupSubviews];
}

- (void)dealloc {
    [[AgoraChatClient sharedClient] logout:YES];
}

- (void)initSdk
{
    AgoraChatOptions *options = [AgoraChatOptions optionsWithAppkey:@"41117440#383391"];
    options.enableConsoleLog = YES;
    [[AgoraChatClient sharedClient] initializeSDKWithOptions:options];
    [self printLog:@"AgoraChat SDK Init Complete!"];
}
```

4. 加载页面元素

```objective-c
- (void)_setupSubviews
{
    [self.view addSubview:self.nameField];
    [self.view addSubview:self.pswdField];
    [self.view addSubview:self.loginBtn];
    [self.view addSubview:self.conversationIdField];
    [self.view addSubview:self.recordBtn];
    [self.view addSubview:self.bottomLine];
    [self.view addSubview:self.resultView];
    
    [self.resultView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.left.right.equalTo(self.view);
        make.height.mas_equalTo(@(self.view.bounds.size.height / 2));
    }];
    [self.bottomLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.resultView.mas_top);
        make.left.equalTo(self.view);
        make.right.equalTo(self.view);
        make.height.equalTo(@3);
    }];
    [self.nameField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.view).offset(30);
        make.top.equalTo(self.view).offset(100);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@150);
    }];
    [self.pswdField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.nameField.mas_right).offset(15);
        make.top.equalTo(self.nameField);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@150);
    }];
    [self.loginBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.nameField);
        make.top.equalTo(self.nameField.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@150);
    }];
    [self.conversationIdField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.loginBtn);
        make.top.equalTo(self.loginBtn.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@320);
    }];
    [self.recordBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.conversationIdField);
        make.top.equalTo(self.conversationIdField.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@320);
    }];
}

#pragma mark - getter

- (UITextField *)nameField
{
    if (!_nameField) {
        _nameField = [[UITextField alloc] init];
        _nameField.backgroundColor = [UIColor systemGrayColor];
        _nameField.borderStyle = UITextBorderStyleNone;
        NSAttributedString *attrStr = [[NSAttributedString alloc] initWithString:@"AgoraChat ID" attributes:@{NSForegroundColorAttributeName:[UIColor whiteColor]}];
        _nameField.attributedPlaceholder = attrStr;
        _nameField.returnKeyType = UIReturnKeyDone;
        _nameField.font = [UIFont systemFontOfSize:17];
        _nameField.textColor = [UIColor whiteColor];
        _nameField.layer.cornerRadius = 5;
        _nameField.layer.borderWidth = 1;
        _nameField.layer.borderColor = [UIColor lightGrayColor].CGColor;
    }
    return _nameField;
}

- (UITextField *)pswdField
{
    if (!_pswdField) {
        _pswdField = [[UITextField alloc] init];
        _pswdField.backgroundColor = [UIColor systemGrayColor];
        _pswdField.borderStyle = UITextBorderStyleNone;
        NSAttributedString *psdAttrStr = [[NSAttributedString alloc] initWithString:@"password" attributes:@{NSForegroundColorAttributeName:[UIColor whiteColor]}];
        _pswdField.attributedPlaceholder = psdAttrStr;
        _pswdField.font = [UIFont systemFontOfSize:17];
        _pswdField.textColor = [UIColor whiteColor];
        _pswdField.returnKeyType = UIReturnKeyDone;
        _pswdField.layer.cornerRadius = 5;
        _pswdField.layer.borderWidth = 1;
        _pswdField.layer.borderColor = [UIColor lightGrayColor].CGColor;
    }
    return _pswdField;
}

- (UIButton *)loginBtn
{
    if (!_loginBtn) {
        _loginBtn = [[UIButton alloc] init];
        _loginBtn.clipsToBounds = YES;
        _loginBtn.layer.cornerRadius = 5;
        _loginBtn.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
        _loginBtn.titleLabel.font = [UIFont systemFontOfSize:19];
        [_loginBtn setTitle:@"Sign in" forState:UIControlStateNormal];
        [_loginBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_loginBtn addTarget:self action:@selector(loginAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _loginBtn;
}

- (UITextField *)conversationIdField
{
    if (!_conversationIdField) {
        _conversationIdField = [[UITextField alloc] init];
        _conversationIdField.backgroundColor = [UIColor systemGrayColor];
        _conversationIdField.borderStyle = UITextBorderStyleNone;
        NSAttributedString *convAttrStr = [[NSAttributedString alloc] initWithString:@"single chat ID" attributes:@{NSForegroundColorAttributeName:[UIColor whiteColor]}];
        _conversationIdField.attributedPlaceholder = convAttrStr;
        _conversationIdField.font = [UIFont systemFontOfSize:17];
        _conversationIdField.textColor = [UIColor whiteColor];
        _conversationIdField.returnKeyType = UIReturnKeyDone;
        _conversationIdField.layer.cornerRadius = 5;
        _conversationIdField.layer.borderWidth = 1;
        _conversationIdField.layer.borderColor = [UIColor lightGrayColor].CGColor;
    }
    return _conversationIdField;
}

- (UIButton *)recordBtn
{
    if (!_recordBtn) {
        _recordBtn = [[UIButton alloc] init];
        _recordBtn.clipsToBounds = YES;
        _recordBtn.layer.cornerRadius = 5;
        _recordBtn.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
        _recordBtn.titleLabel.font = [UIFont systemFontOfSize:19];
        _recordBtn.tag = 0;
        [_recordBtn setTitle:@"Start Recording" forState:UIControlStateNormal];
        [_recordBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_recordBtn addTarget:self action:@selector(recordAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _recordBtn;
}

- (UIView *)bottomLine
{
    if (!_bottomLine) {
        _bottomLine = [[UIView alloc] init];
        _bottomLine.backgroundColor = [UIColor whiteColor];
        _bottomLine.alpha = 0.1;
    }
    return _bottomLine;
}

- (UITextView *)resultView
{
    if (!_resultView) {
        _resultView = [[UITextView alloc]init];
        _resultView.backgroundColor = [UIColor whiteColor];
        _resultView.textColor = [UIColor blackColor];
        _resultView.font = [UIFont systemFontOfSize:14.0];
        _resultView.editable = NO;
        _resultView.scrollEnabled = YES;
        _resultView.textAlignment = NSTextAlignmentLeft;
        _resultView.layoutManager.allowsNonContiguousLayout = NO;
    }
    return _resultView;
}

- (NSDateFormatter *)formatter
{
    if (!_formatter) {
        _formatter = [[NSDateFormatter alloc]init];
        [_formatter setDateFormat:@"YYYY-MM-dd HH:mm:ss.SSS"];
    }
    
    return _formatter;
}

- (void)handleTapTableViewAction:(UITapGestureRecognizer *)aTap
{
    if (aTap.state == UIGestureRecognizerStateEnded) {
        [self.view endEditing:YES];
    }
}

//打印 log
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

### 2.实现登录逻辑

1. 在 AudioMessageViewController.m 文件里添加如下代码逻辑

```objective-c
- (void)loginAction
{
    if (AgoraChatClient.sharedClient.isLoggedIn) {
        [[AgoraChatClient sharedClient] logout:YES];
    }
    [self.view endEditing:YES];
    
    NSString *name = [self.nameField.text lowercaseString];
    NSString *pswd = [self.pswdField.text lowercaseString];
    
    if (name.length == 0 || pswd.length == 0) {
        [self printLog:@"username or password is null"];
        return;
    }
    
    [[AgoraChatClient sharedClient] logout:YES];
    __weak typeof(self) weakself = self;
    void (^finishBlock) (NSString *aName, AgoraChatError *aError) = ^(NSString *aName, AgoraChatError *aError) {
        if (!aError) {
            [weakself printLog:[NSString stringWithFormat:@"login agora chat server success ! name : %@",aName]];
            return ;
        }
        
        [weakself printLog:[NSString stringWithFormat:@"login agora chat server fail ! errorDes : %@",aError.errorDescription]];
    };

    [[EMHttpRequest sharedManager] loginToApperServer:[self.nameField.text lowercaseString] pwd:[self.pswdField.text lowercaseString] completion:^(NSInteger statusCode, NSString * _Nonnull response) {
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
```
2. 此外 FetchServerMessageViewController 使用到了访问我们 AppServer 进行登陆的工具类 EMHttpRequest，还需在项目中导入该工具类。

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

### 3.开始录音

```objective-c
- (void)_startRecord
{
    NSString *recordPath = [[self getAudioPath] stringByAppendingFormat:@"/%.0f", [[NSDate date] timeIntervalSince1970] * 1000];
    __weak typeof(self) weakself = self;
    [[AgoraAudioRecordUtil sharedHelper] startRecordWithPath:recordPath completion:^(NSError * _Nonnull error) {
        if (error) {
            [weakself printLog:[NSString stringWithFormat:@"recording fail ! error : %@",error.description]];
        } else {
            [weakself printLog:@"start recording !"];
        }
    }];
}

// 本地存储路径
- (NSString *)getAudioPath
{
    NSString *path = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) firstObject];
    path = [path stringByAppendingPathComponent:@"SampleCodeRecord"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:path]) {
        [[NSFileManager defaultManager] createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:nil];
    }
    
    return path;
}

```

### 4.停止录音并发送语音消息

```objective-c
- (void)_stopRecord
{
    __weak typeof(self) weakself = self;
    [[AgoraAudioRecordUtil sharedHelper] stopRecordWithCompletion:^(NSString * _Nonnull aPath, NSInteger aTimeLength) {
        [weakself printLog:[NSString stringWithFormat:@"record complete ! path : %@, timelength : %ld",aPath,aTimeLength]];
        AgoraChatVoiceMessageBody *body = [[AgoraChatVoiceMessageBody alloc] initWithLocalPath:aPath displayName:@"audio"];
        body.duration = (int)aTimeLength;
        if(body.duration < 1){
            [weakself printLog:@"Speak time is too short !"];
            return;
        }
        [self sendMessageWithBody:body ext:nil];
    }];
}

// 发送语音消息
- (void)sendMessageWithBody:(AgoraChatMessageBody *)aBody
                        ext:(NSDictionary * __nullable)aExt
{
    __weak typeof(self) weakself = self;
    NSString *from = [[AgoraChatClient sharedClient] currentUsername];
    NSString *to = _conversationIdField.text;
    AgoraChatMessage *message = [[AgoraChatMessage alloc] initWithConversationID:to from:from to:to body:aBody ext:aExt];
    message.chatType = AgoraChatTypeChat;
    [[AgoraChatClient sharedClient].chatManager sendMessage:message progress:nil completion:^(AgoraChatMessage *message, AgoraChatError *error) {
        if (error) {
            [weakself printLog:[NSString stringWithFormat:@"send voice message fail ! error : %@",error.errorDescription]];
        } else {
            [weakself printLog:@"send voice message success !"];
        }
    }];
}
```

### 5.录音转码

录音功能我们使用了工具类 AgoraAudioRecordUtil 并配合使用 EMVoiceConvert 1.0.1版本的 pod 仓库来完成。

AgoraAudioRecordUtil.h 主要功能：

```objective-c
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface AgoraAudioRecordUtil : NSObject

+ (instancetype)sharedHelper;

// 开始录音
- (void)startRecordWithPath:(NSString *)aPath
                 completion:(void(^)(NSError *error))aCompletion;
// 停止录音
-(void)stopRecordWithCompletion:(void(^)(NSString *aPath, NSInteger aTimeLength))aCompletion;
// 取消录音
-(void)cancelRecord;

@end

NS_ASSUME_NONNULL_END
```

AgoraAudioRecordUtil.m 功能实现：

```objective-c
#import "AgoraAudioRecordUtil.h"
#import "amrFileCodec.h"
#import <AVFoundation/AVAudioRecorder.h>

static AgoraAudioRecordUtil *recordUtil = nil;
@interface AgoraAudioRecordUtil ()<AVAudioRecorderDelegate>

@property (nonatomic, strong) NSDate *startDate;
@property (nonatomic, strong) NSDate *endDate;

@property (nonatomic, strong) AVAudioRecorder *recorder;
@property (nonatomic, strong) NSDictionary *recordSetting;

@property (nonatomic, copy) void (^recordFinished)(NSString *aPath, NSInteger aTimeLength);

@end

@implementation AgoraAudioRecordUtil

+ (instancetype)sharedHelper
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        recordUtil = [[AgoraAudioRecordUtil alloc] init];
    });
    
    return recordUtil;
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        _recordSetting = @{AVSampleRateKey:@(8000.0), AVFormatIDKey:@(kAudioFormatLinearPCM), AVLinearPCMBitDepthKey:@(16), AVNumberOfChannelsKey:@(1), AVEncoderAudioQualityKey:@(AVAudioQualityHigh)};
        
    }
    
    return self;
}

- (void)dealloc
{
    [self _stopRecord];
}

#pragma mark - Private

// WAV path 转为 AMR path
+ (int)wavPath:(NSString *)aWavPath toAmrPath:(NSString*)aAmrPath
{
    
    if (EM_EncodeWAVEFileToAMRFile([aWavPath cStringUsingEncoding:NSASCIIStringEncoding], [aAmrPath cStringUsingEncoding:NSASCIIStringEncoding], 1, 16))
        return 0;   // success
    
    return 1;   // failed
}

// WAV 格式 转为 AMR 格式
- (BOOL)_convertWAV:(NSString *)aWavPath toAMR:(NSString *)aAmrPath
{
    BOOL ret = NO;
    NSFileManager *fileManager = [NSFileManager defaultManager];
    if ([fileManager fileExistsAtPath:aAmrPath]) {
        ret = YES;
    } else if ([fileManager fileExistsAtPath:aWavPath]) {
        [AgoraAudioRecordUtil wavPath:aWavPath toAmrPath:aAmrPath];
        if ([fileManager fileExistsAtPath:aAmrPath]) {
            ret = YES;
        }
    }
    
    return ret;
}

#pragma mark - AVAudioRecorderDelegate

- (void)audioRecorderDidFinishRecording:(AVAudioRecorder *)recorder
                           successfully:(BOOL)flag
{
    NSInteger timeLength = [[NSDate date] timeIntervalSinceDate:self.startDate];
    NSString *recordPath = [[self.recorder url] path];
    if (self.recordFinished) {
        if (!flag) {
            recordPath = nil;
        }
        // Convert wav to amr
        NSString *amrFilePath = [[recordPath stringByDeletingPathExtension] stringByAppendingPathExtension:@"amr"];
        BOOL ret = [self _convertWAV:recordPath toAMR:amrFilePath];
        if (ret) {
            // Remove the wav
            NSFileManager *fm = [NSFileManager defaultManager];
            [fm removeItemAtPath:recordPath error:nil];
            
            amrFilePath = amrFilePath;
        } else {
            recordPath = nil;
            timeLength = 0;
        }
        
        self.recordFinished(amrFilePath, timeLength);
    }
    self.recorder = nil;
    self.recordFinished = nil;
}

- (void)audioRecorderEncodeErrorDidOccur:(AVAudioRecorder *)recorder
                                   error:(NSError *)error{
    NSLog(@"audioRecorderEncodeErrorDidOccur");
}

#pragma mark - Private

- (void)_stopRecord
{
    _recorder.delegate = nil;
    if (_recorder.recording) {
        [_recorder stop];
    }
    _recorder = nil;
    self.recordFinished = nil;
}

#pragma mark - Public

// 开始录音
- (void)startRecordWithPath:(NSString *)aPath
                 completion:(void(^)(NSError *error))aCompletion
{
    NSError *error = nil;
    do {
        if (self.recorder && self.recorder.isRecording) {
            error = [NSError errorWithDomain:@"正在进行录制" code:-1 userInfo:nil];
            break;
        }
        
        AVAudioSessionRecordPermission permissionStatus = [[AVAudioSession sharedInstance] recordPermission];
        if (permissionStatus == AVAudioSessionRecordPermissionDenied) {
            error = [NSError errorWithDomain:@"未开启麦克风权限" code:-1 userInfo:nil];
            if (aCompletion) {
                aCompletion(error);
            }
            return;
        }
        
        [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayAndRecord withOptions:AVAudioSessionCategoryOptionDuckOthers error:&error];
        if (!error){
            [[AVAudioSession sharedInstance] setActive:YES withOptions:AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation error:&error];
        }
        
        if (error) {
            error = [NSError errorWithDomain:@"AVAudioSession SetCategory失败" code:-1 userInfo:nil];
            break;
        }
        
        NSString *wavPath = [[aPath stringByDeletingPathExtension] stringByAppendingPathExtension:@"wav"];
        NSURL *wavUrl = [[NSURL alloc] initFileURLWithPath:wavPath];
        self.recorder = [[AVAudioRecorder alloc] initWithURL:wavUrl settings:self.recordSetting error:&error];
        if(error || !self.recorder) {
            self.recorder = nil;
            error = [NSError errorWithDomain:@"文件格式转换失败" code:-1 userInfo:nil];
            break;
        }
        
        BOOL ret = [self.recorder prepareToRecord];
        if (ret) {
            self.startDate = [NSDate date];
            self.recorder.meteringEnabled = YES;
            self.recorder.delegate = self;
            ret = [self.recorder record];
        }
        
        if (!ret) {
            [self _stopRecord];
            error = [NSError errorWithDomain:@"准备录制工作失败" code:-1 userInfo:nil];
        }
        
    } while (0);
    
    if (aCompletion) {
        aCompletion(error);
    }
}

// 停止录音
-(void)stopRecordWithCompletion:(void(^)(NSString *aPath, NSInteger aTimeLength))aCompletion
{、
    self.recordFinished = aCompletion;
    [self.recorder stop];
}

// 取消录音
-(void)cancelRecord
{
    [self _stopRecord];
    self.startDate = nil;
    self.endDate = nil;
}

@end
```

### 6.编译并运行项目

使用 Xcode 在模拟器或真机上编译并运行项目。运行成功之后，你可以进行以下操作：

- 登录
- 输入会话 ID
- 录音
- 停止录音
- 发送语音消息

功能执行结果在页面下半部分会有相关日志记录展示。
