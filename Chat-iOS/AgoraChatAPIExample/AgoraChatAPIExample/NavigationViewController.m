//
//  NavigationViewController.m
//  AgoraChatAPIExample
//
//  Created by zhangchong on 2022/4/22.
//

#import "NavigationViewController.h"
#import <Masonry/Masonry.h>
#import <AgoraChat/AgoraChat.h>

#import "AgoraChatApiExampleViewController.h"
#import "ImportMessageViewController.h"
#import "AudioMessageViewController.h"
#import "FetchServerMessageViewController.h"

@interface NavigationViewController ()

@property (nonatomic, strong) UIButton *agoraChatApiExampleBtn;
@property (nonatomic, strong) UIButton *importMessageBtn;
@property (nonatomic, strong) UIButton *serverConversationMessageBtn;
@property (nonatomic, strong) UIButton *audioMessageBtn;

@end

@implementation NavigationViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self initSdk];
    [self _setupSubviews];
}

- (void)initSdk
{
    AgoraChatOptions *options = [AgoraChatOptions optionsWithAppkey:@"41117440#383391"];
    options.enableConsoleLog = YES;
    [[AgoraChatClient sharedClient] initializeSDKWithOptions:options];
}

- (void)_setupSubviews
{
    self.view.backgroundColor = [UIColor whiteColor];
    self.agoraChatApiExampleBtn = [[UIButton alloc] init];
    self.agoraChatApiExampleBtn.clipsToBounds = YES;
    self.agoraChatApiExampleBtn.layer.cornerRadius = 5;
    self.agoraChatApiExampleBtn.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
    self.agoraChatApiExampleBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [self.agoraChatApiExampleBtn setTitle:@"AgorachatApiExample" forState:UIControlStateNormal];
    [self.agoraChatApiExampleBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.agoraChatApiExampleBtn addTarget:self action:@selector(apiExampleAction) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.agoraChatApiExampleBtn];
    [self.agoraChatApiExampleBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.view);
        make.top.equalTo(self.view).offset(100);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@200);
    }];
    
    self.importMessageBtn = [[UIButton alloc] init];
    self.importMessageBtn.clipsToBounds = YES;
    self.importMessageBtn.layer.cornerRadius = 5;
    self.importMessageBtn.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
    self.importMessageBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [self.importMessageBtn setTitle:@"ImportMessage" forState:UIControlStateNormal];
    [self.importMessageBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.importMessageBtn addTarget:self action:@selector(importMessageAction) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.importMessageBtn];
    [self.importMessageBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.view);
        make.top.equalTo(self.agoraChatApiExampleBtn.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@200);
    }];
    
    self.serverConversationMessageBtn = [[UIButton alloc] init];
    self.serverConversationMessageBtn.clipsToBounds = YES;
    self.serverConversationMessageBtn.layer.cornerRadius = 5;
    self.serverConversationMessageBtn.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
    self.serverConversationMessageBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [self.serverConversationMessageBtn setTitle:@"FetchServerMessage" forState:UIControlStateNormal];
    [self.serverConversationMessageBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.serverConversationMessageBtn addTarget:self action:@selector(serverConversationMessageAction) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.serverConversationMessageBtn];
    [self.serverConversationMessageBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.view);
        make.top.equalTo(self.importMessageBtn.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@200);
    }];
    
    self.audioMessageBtn = [[UIButton alloc] init];
    self.audioMessageBtn.clipsToBounds = YES;
    self.audioMessageBtn.layer.cornerRadius = 5;
    self.audioMessageBtn.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
    self.audioMessageBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [self.audioMessageBtn setTitle:@"AudioMessage" forState:UIControlStateNormal];
    [self.audioMessageBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.audioMessageBtn addTarget:self action:@selector(audioMessageAction) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.audioMessageBtn];
    [self.audioMessageBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.view);
        make.top.equalTo(self.serverConversationMessageBtn.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@200);
    }];
}

#pragma mark - Action

- (void)apiExampleAction
{
    AgoraChatApiExampleViewController *apiExampleVc = [[AgoraChatApiExampleViewController alloc]init];
    apiExampleVc.modalPresentationStyle = 0;
    [self.navigationController pushViewController:apiExampleVc animated:YES];
}

- (void)importMessageAction
{
    ImportMessageViewController *importMessageVc = [[ImportMessageViewController alloc]init];
    importMessageVc.modalPresentationStyle = 0;
    [self.navigationController pushViewController:importMessageVc animated:YES];
}

- (void)serverConversationMessageAction
{
    FetchServerMessageViewController *fetchServerMessageVc = [[FetchServerMessageViewController alloc]init];
    fetchServerMessageVc.modalPresentationStyle = 0;
    [self.navigationController pushViewController:fetchServerMessageVc animated:YES];
}

- (void)audioMessageAction
{
    AudioMessageViewController *audioMessageVc = [[AudioMessageViewController alloc]init];
    audioMessageVc.modalPresentationStyle = 0;
    [self.navigationController pushViewController:audioMessageVc animated:YES];
}

@end
