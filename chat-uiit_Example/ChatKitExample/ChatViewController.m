//
//  ChatViewController.m
//  EaseIM
//
//  Created by zhangchong on 2020/11/27.
//  Copyright © 2020 zhangchong. All rights reserved.
//

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
#import "chat-uikit/EaseChatKit.h"

@interface ChatViewController ()<EaseChatViewControllerDelegate, UITextFieldDelegate>
@property (nonatomic, strong) EaseConversationModel *conversationModel;
@property (nonatomic, strong) AgoraChatConversation *conversation;
@property (nonatomic, strong) EaseChatViewController *chatController;
@property (nonatomic, strong) UITextField *conversationIdField;
@property (nonatomic, strong) UIButton *chatBtn;
@property (nonatomic, strong) UIButton *logoutBtn;

@end

@implementation ChatViewController

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

- (void)logout
{
    [AgoraChatClient.sharedClient logout:YES completion:^(AgoraChatError *aError) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"loginStateChange" object:@NO];
    }];
}

@end
