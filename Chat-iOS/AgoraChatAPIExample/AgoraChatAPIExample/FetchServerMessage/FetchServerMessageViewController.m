//
//  FetchServerMessageViewController.m
//  AgoraChatAPIExample
//
//  Created by zhangchong on 2022/4/22.
//

#import "FetchServerMessageViewController.h"
#import <Masonry/Masonry.h>
#import <AgoraChat/AgoraChat.h>
#import "EMHttpRequest.h"

@interface FetchServerMessageViewController ()<UIScrollViewDelegate>

@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) UIView *bottomLine;
@property (nonatomic, strong) UITextView *resultView;

@property (nonatomic, strong) UITextField *nameField;
@property (nonatomic, strong) UITextField *pswdField;

@property (nonatomic, strong) UIButton *loginBtn;

@property (nonatomic, strong) UITextField *agoraChatRoamingMesagesField;
@property (nonatomic, strong) UIButton *serverConversationListBtn;
@property (nonatomic, strong) UIButton *remoteRoamingBtn;

@property (nonatomic, strong) NSDateFormatter *formatter;

@end

@implementation FetchServerMessageViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];

    [self initAgoraChatSdk];
    [self _setupSubviews];
    // Do any additional setup after loading the view.
}

- (void)dealloc {
    [[AgoraChatClient sharedClient] logout:YES];
}

- (void)initAgoraChatSdk
{
    AgoraChatOptions *options = [AgoraChatOptions optionsWithAppkey:@"41117440#383391"];
    options.enableConsoleLog = YES;
    [[AgoraChatClient sharedClient] initializeSDKWithOptions:options];
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
        make.height.mas_equalTo(@(self.view.bounds.size.height / 2));
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
    self.nameField.borderStyle = UITextBorderStyleNone;
    NSAttributedString *attrStr = [[NSAttributedString alloc] initWithString:@"AgoraChat ID" attributes:@{NSForegroundColorAttributeName:[UIColor whiteColor]}];
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
        make.top.equalTo(self.scrollView);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@150);
    }];
    
    self.pswdField = [[UITextField alloc] init];
    self.pswdField.backgroundColor = [UIColor systemGrayColor];
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
        make.left.equalTo(self.nameField);
        make.top.equalTo(self.nameField.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@150);
    }];
    
    self.agoraChatRoamingMesagesField = [[UITextField alloc] init];
    self.agoraChatRoamingMesagesField.backgroundColor = [UIColor systemGrayColor];
    self.agoraChatRoamingMesagesField.borderStyle = UITextBorderStyleNone;
    NSAttributedString *historyMessaegAttrStr = [[NSAttributedString alloc] initWithString:@"AgoraChatConversationID" attributes:@{NSForegroundColorAttributeName:[UIColor whiteColor]}];
    self.agoraChatRoamingMesagesField.attributedPlaceholder = historyMessaegAttrStr;
    self.agoraChatRoamingMesagesField.font = [UIFont systemFontOfSize:17];
    self.agoraChatRoamingMesagesField.textColor = [UIColor whiteColor];
    self.agoraChatRoamingMesagesField.returnKeyType = UIReturnKeyDone;
    self.agoraChatRoamingMesagesField.layer.cornerRadius = 5;
    self.agoraChatRoamingMesagesField.layer.borderWidth = 1;
    self.agoraChatRoamingMesagesField.layer.borderColor = [UIColor lightGrayColor].CGColor;
    [self.scrollView addSubview:self.agoraChatRoamingMesagesField];
    [self.agoraChatRoamingMesagesField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.loginBtn);
        make.top.equalTo(self.loginBtn.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@200);
    }];
    
    self.serverConversationListBtn = [[UIButton alloc] init];
    self.serverConversationListBtn.clipsToBounds = YES;
    self.serverConversationListBtn.layer.cornerRadius = 5;
    self.serverConversationListBtn.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
    self.serverConversationListBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [self.serverConversationListBtn setTitle:@"server conversations" forState:UIControlStateNormal];
    [self.serverConversationListBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.serverConversationListBtn addTarget:self action:@selector(serverConversationListAction) forControlEvents:UIControlEventTouchUpInside];
    [self.scrollView addSubview:self.serverConversationListBtn];
    [self.serverConversationListBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.agoraChatRoamingMesagesField);
        make.top.equalTo(self.agoraChatRoamingMesagesField.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@200);
    }];
    
    self.remoteRoamingBtn = [[UIButton alloc] init];
    self.remoteRoamingBtn.clipsToBounds = YES;
    self.remoteRoamingBtn.layer.cornerRadius = 5;
    self.remoteRoamingBtn.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
    self.remoteRoamingBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [self.remoteRoamingBtn setTitle:@"roaming message" forState:UIControlStateNormal];
    [self.remoteRoamingBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.remoteRoamingBtn addTarget:self action:@selector(remoteRoamingAction) forControlEvents:UIControlEventTouchUpInside];
    [self.scrollView addSubview:self.remoteRoamingBtn];
    [self.remoteRoamingBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.serverConversationListBtn);
        make.top.equalTo(self.serverConversationListBtn.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@200);
    }];
}

- (void)loginAction
{
    [self.view endEditing:YES];
    
    NSString *name = [self.nameField.text lowercaseString];
    NSString *pswd = [self.pswdField.text lowercaseString];

    if (name.length == 0 || pswd.length == 0) {
        [self printLog:@"username or password is null"];
        return;
    }
    
    [self connectAgoraChatServer];
}

- (void)connectAgoraChatServer
{
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

#pragma mark - AgoraChatServerConversationList

- (void)serverConversationListAction
{
    if (![[AgoraChatClient sharedClient] isLoggedIn]) {
        [self printLog:@"login agora chat server !"];
        return;
    }
    
    __weak typeof(self) weakself = self;
    [[AgoraChatClient sharedClient].chatManager getConversationsFromServer:^(NSArray *aCoversations, AgoraChatError *aError) {
        if (!aError) {
            [weakself printLog:[NSString stringWithFormat:@"get server conversation complete ! conversation list count : %lu,", (unsigned long)[aCoversations count]]];
            for (AgoraChatConversation *conversation in aCoversations) {
                [weakself printLog:[NSString stringWithFormat:@"server conversation ! conversation id : %@, conversation type : %d", conversation.conversationId, conversation.type]];
            }
        } else {
            [weakself printLog:[NSString stringWithFormat:@"get server conversation fail ! error code : %d, error desc : %@", aError.code, aError.errorDescription]];
        }
    }];
}

#pragma mark - AgoraChatRoamingMessages

- (void)remoteRoamingAction
{
    if (![[AgoraChatClient sharedClient] isLoggedIn]) {
        [self printLog:@"login agora chat server !"];
        return;
    }
    if (self.agoraChatRoamingMesagesField.text == 0) {
        [self printLog:@"input agora chat conversationID !"];
        return;
    }
    __weak typeof(self) weakself = self;
    [[AgoraChatClient sharedClient].chatManager asyncFetchHistoryMessagesFromServer:[self.agoraChatRoamingMesagesField.text lowercaseString] conversationType:AgoraChatConversationTypeChat startMessageId:nil pageSize:50 completion:^(AgoraChatCursorResult *aResult, AgoraChatError *aError) {
        if (!aError) {
            [weakself printLog:[NSString stringWithFormat:@"load remote roaming message complete ! message count : %lu", aResult.list.count]];
            for (AgoraChatMessage *message in aResult.list) {
                [weakself printLog:[NSString stringWithFormat:@"roaming message ! message id : %@ , message type : %d, conversation id : %@, from : %@, to : %@", message.messageId, message.body.type, message.conversationId, message.from, message.to]];
            }
        } else {
            [weakself printLog:[NSString stringWithFormat:@"load remote roaming message fail ! error code : %d, error desc : %@", aError.code, aError.errorDescription]];
        }
    }];
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

- (NSDateFormatter *)formatter
{
    if (!_formatter) {
        _formatter = [[NSDateFormatter alloc]init];
        [_formatter setDateFormat:@"YYYY-MM-dd HH:mm:ss.SSS"];
    }
    
    return _formatter;
}

@end
