//
//  AudioMessageViewController.m
//  AgoraChatMessage
//
//  Created by zhangchong on 2022/4/18.
//

#import "AudioMessageViewController.h"
#import <Masonry/Masonry.h>
#import <AgoraChat/AgoraChat.h>
#import "AgoraAudioRecordUtil.h"
#import "EMHttpRequest.h"

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

@implementation AudioMessageViewController

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

#pragma mark - Action

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

- (void)recordAction
{
    if (_conversationIdField.text.length == 0) {
        [self printLog:@"Input conversation ID !"];
        return;
    }
    if (self.recordBtn.tag == 0) {
        [self.recordBtn setTitle:@"Stop Recording" forState:UIControlStateNormal];
        self.recordBtn.tag = 1;
        [self _startRecord];
    } else {
        [self.recordBtn setTitle:@"Start Recording" forState:UIControlStateNormal];
        self.recordBtn.tag = 0;
        [self _stopRecord];
    }
}

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

- (NSString *)getAudioPath
{
    NSString *path = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) firstObject];
    path = [path stringByAppendingPathComponent:@"SampleCodeRecord"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:path]) {
        [[NSFileManager defaultManager] createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:nil];
    }
    
    return path;
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
        
        long allStrCount = self.resultView.text.length; //获取总文字个数
        [weakself.resultView scrollRangeToVisible:NSMakeRange(0, allStrCount)];//把光标位置移到最后
    });
}

@end
