//
//  ChatsViewController.m
//  ChatKitExample
//
//  Created by zhangchong on 2021/10/25.
//

#import "ChatsViewController.h"
#import <AgoraChat/AgoraChat.h>
#import <Masonry/Masonry.h>
#import "ChatViewController.h"

@interface ChatsViewController () <EaseConversationsViewControllerDelegate>
{
    EaseConversationsViewController *_easeConvsVC;
}
@property (nonatomic, strong) UIButton *logoutBtn;
@end

@implementation ChatsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self _setupSubviews];
    // Do any additional setup after loading the view.
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
}

- (void)_setupSubviews
{
    self.view.backgroundColor = [UIColor whiteColor];
    
    self.logoutBtn = [[UIButton alloc]init];
    self.logoutBtn.backgroundColor = [UIColor redColor];
    [self.logoutBtn setTitle:@"Log out" forState:UIControlStateNormal];
    [self.logoutBtn addTarget:self action:@selector(logout) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.logoutBtn];
    [self.logoutBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.bottom.equalTo(self.view);
        make.height.equalTo(@50);
    }];
    
    EaseConversationViewModel *viewMdeol = [[EaseConversationViewModel alloc] init];
    viewMdeol.canRefresh = NO;
    viewMdeol.avatarType = Circular;

    _easeConvsVC = [[EaseConversationsViewController alloc] initWithModel:viewMdeol];
    _easeConvsVC.delegate = self;
    _easeConvsVC.view.backgroundColor = [UIColor whiteColor];
    [self addChildViewController:_easeConvsVC];
    [self.view addSubview:_easeConvsVC.view];
    [_easeConvsVC.view mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.right.equalTo(self.view);
        make.bottom.equalTo(self.logoutBtn.mas_top).offset(-20);
    }];
}

- (void)easeTableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    EaseConversationModel *model = _easeConvsVC.dataAry[indexPath.row];
    ChatViewController *controller = [[ChatViewController alloc]initWithConversationId:model.easeId conversationType:model.type];
    [self .navigationController pushViewController:controller animated:YES];
}

- (void)logout
{
    [AgoraChatClient.sharedClient logout:YES completion:^(AgoraChatError *aError) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"loginStateChange" object:@NO];
    }];
}

@end
