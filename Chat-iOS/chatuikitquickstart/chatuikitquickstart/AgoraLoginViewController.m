//
//  AgoraNewLoginViewController.m
//  login-UI3.0
//
//  Created by liang on 2021/10/18.
//  Copyright © 2021 agora. All rights reserved.
//

#import "AgoraLoginViewController.h"
#import "AgoraChatHttpRequest.h"
#import "UIViewController+ComponentSize.h"
#import "UIColor+EaseUI.h"
#import <Masonry/Masonry.h>
#import <AgoraChat/AgoraChat.h>

#define kLoginButtonHeight 48.0f
#define kAgroaPadding 10.0f

@interface AgoraLoginViewController ()<UITextViewDelegate,UITextFieldDelegate>

@property (nonatomic, strong) UIView *contentView;
@property (nonatomic, strong) UIImageView *logoImageView;
@property (nonatomic, strong) UIImageView* titleImageView;
@property (nonatomic, strong) UIView *hintView;
@property (nonatomic, strong) UILabel *hintTitleLabel;
@property (nonatomic, strong) UITextField *usernameTextField;
@property (nonatomic, strong) UITextField *passwordTextField;
@property (nonatomic, strong) UIButton *loginButton;
@property (nonatomic) BOOL isMaxLimitLength;

@property (nonatomic, strong) UIButton *registerButton;
@property (nonatomic, strong) UIImageView *loadingImageView;
@property (nonatomic, assign) CGFloat loadingAngle;

@property (nonatomic, strong) UIButton *accountTypeBtn;

@end

@implementation AgoraLoginViewController

#pragma mark life cycle
- (instancetype)init {
    self = [super init];
    if (self) {
        _isMaxLimitLength = NO;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self placeAndLayoutSubViews];
}

- (void)viewWillAppear:(BOOL)animated
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyBoardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyBoardWillHide:) name:UIKeyboardWillHideNotification object:nil];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillHideNotification object:nil];
}

- (void)placeAndLayoutSubViews
{
    self.contentView = [[UIView alloc]init];
    self.contentView.backgroundColor = UIColor.whiteColor;
    [self.view addSubview:self.contentView];
    [self.contentView addSubview:self.logoImageView];
    [self.contentView addSubview:self.titleImageView];
    [self.contentView addSubview:self.hintView];
    [self.contentView addSubview:self.usernameTextField];
    [self.contentView addSubview:self.passwordTextField];
    [self.contentView addSubview:self.loginButton];
    [self.contentView addSubview:self.accountTypeBtn];

    [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self.view);
    }];

    [self.logoImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.contentView.mas_top).offset(134.0);
        make.centerX.equalTo(self.contentView);
    }];
    
    [self.titleImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.logoImageView.mas_bottom).offset(20);
        make.centerX.equalTo(self.contentView);
//        make.width.equalTo(@108);
//        make.height.equalTo(@29);
        
    }];
        
    [self.hintView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.titleImageView.mas_bottom).offset(63);
        make.centerX.equalTo(self.contentView);
        make.height.equalTo(@20);
    }];
    self.hintView.hidden = YES;
    
    [self.usernameTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.titleImageView.mas_bottom).offset(95);
        make.left.equalTo(self.contentView).offset(24);
        make.right.equalTo(self.contentView).offset(-24);
        make.height.equalTo(@kLoginButtonHeight);
    }];
    
    [self.passwordTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(_usernameTextField.mas_bottom).offset(20);
        make.left.equalTo(self.usernameTextField);
        make.right.equalTo(self.usernameTextField);
        make.height.equalTo(@kLoginButtonHeight);
    }];
    
    [self.loginButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.passwordTextField.mas_bottom).offset(kAgroaPadding * 2);
        make.left.equalTo(self.usernameTextField);
        make.right.equalTo(self.usernameTextField);
        make.height.equalTo(@kLoginButtonHeight);
    }];
    
    [self.accountTypeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.equalTo(self.loginButton.mas_right);
        make.bottom.equalTo(self.contentView).offset(-30);
        make.width.equalTo(@100);
        make.height.equalTo(@30);
    }];
}

#pragma mark - UITextFieldDelegate

- (NSUInteger)charactorNumberWithEncoding:(NSString *)str
{
    NSUInteger strLength = 0;
    char *p = (char *)[str cStringUsingEncoding:NSUTF8StringEncoding];
    
    NSUInteger lengthOfBytes = [str lengthOfBytesUsingEncoding:NSUTF8StringEncoding];
    for (int i = 0; i < lengthOfBytes; i++) {
        if (*p) {
            p++;
            strLength++;
        }
        else {
            p++;
        }
    }
    return strLength;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [self.contentView endEditing:YES];
    return YES;
}


#pragma mark - private

- (void)switchType {
    if (self.accountTypeBtn.tag == 0) {
        [self.accountTypeBtn setTitle:@"Sign in" forState:UIControlStateNormal];
        [self.loginButton setTitle:@"Sign up" forState:UIControlStateNormal];
        self.accountTypeBtn.tag = 1;
    } else {
        [self.accountTypeBtn setTitle:@"Sign up" forState:UIControlStateNormal];
        [self.loginButton setTitle:@"Sign in" forState:UIControlStateNormal];
        self.accountTypeBtn.tag = 0;
    }
}

- (NSAttributedString *)textFieldAttributeString:(NSString *)content {
    
    NSAttributedString *attrString = [[NSAttributedString alloc] initWithString:content attributes:
        @{NSForegroundColorAttributeName:[UIColor colorWithHexString:@"0x999999"],
          NSFontAttributeName:[UIFont fontWithName:@"PingFang SC" size:14.0]
        }];
    return attrString;
}

- (void)startAnimation {
    CGAffineTransform endAngle = CGAffineTransformMakeRotation(self.loadingAngle * (M_PI /180.0f));

    [UIView animateWithDuration:0.05 delay:0 options:UIViewAnimationOptionCurveLinear animations:^{
        self.loadingImageView.transform = endAngle;
    } completion:^(BOOL finished) {
        self.loadingAngle += 15;
        [self startAnimation];
    }];
}

- (void)updateLoginStateWithStart:(BOOL)start{
    dispatch_async(dispatch_get_main_queue(), ^{
        if (start) {
            [self.loginButton setTitle:@"" forState:UIControlStateNormal];
            self.loadingImageView.hidden = NO;
            [self startAnimation];
            
        }else {
            if (self.accountTypeBtn.tag == 1) {
                [self.loginButton setTitle:@"Sign up" forState:UIControlStateNormal];
            } else {
                [self.loginButton setTitle:@"Sign in" forState:UIControlStateNormal];
            }
            self.loadingImageView.hidden = YES;
        }
    });
}


- (BOOL)_isEmpty
{
    BOOL ret = NO;
    NSString *username = _usernameTextField.text;
    NSString *password = _passwordTextField.text;
    if (username.length == 0 || password.length == 0) {
        ret = YES;
        self.hintView.hidden = NO;
        self.hintTitleLabel.text = NSLocalizedString(@"login.inputNameAndPswd", @"Please enter username and password");
    } else if (_isMaxLimitLength) {
        ret = YES;
        self.hintView.hidden = NO;
        self.hintTitleLabel.text = NSLocalizedString(@"login.inputNameOrPswdTooLength", @"Username or password too length");
    } else {
        NSString *regex = @"^[A-Za-z0-9]+$";
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF MATCHES %@",regex];
        BOOL result = [predicate evaluateWithObject:username];
        if (!result) {
            ret = YES;
            self.hintView.hidden = NO;
            self.hintTitleLabel.text = NSLocalizedString(@"login.inputNameNotCompliance", @"Latin letters and numbers only.");
        }
    }
    
    return ret;
}

- (void)doSignIn {
    if ([self _isEmpty]) {
        return;
    }
    [self.view endEditing:YES];
        
    [self updateLoginStateWithStart:YES];
    self.hintView.hidden = YES;
    self.hintTitleLabel.text = @"";
    
    if (self.accountTypeBtn.tag == 1) {
        [self doSignUp];
        return;
    }
    
    void (^finishBlock) (NSString *aName, AgoraChatError *aError) = ^(NSString *aName, AgoraChatError *aError) {
        dispatch_async(dispatch_get_main_queue(), ^{
            self.hintView.hidden = NO;
        });
        if (!aError) {
            [[NSNotificationCenter defaultCenter] postNotificationName:@"loginStateChange" object:@YES];
            dispatch_async(dispatch_get_main_queue(), ^{
                self.hintTitleLabel.text = @"login.success";
            });
            return ;
        }
        
        NSString *errorDes = NSLocalizedString(@"login.failure", @"login failure");
        switch (aError.code) {
            case AgoraChatErrorServerNotReachable:
                errorDes = NSLocalizedString(@"error.connectServerFail", @"Connect to the server failed!");
                break;
            case AgoraChatErrorNetworkUnavailable:
                errorDes = NSLocalizedString(@"error.connectNetworkFail", @"No network connection!");
                break;
            case AgoraChatErrorServerTimeout:
                errorDes = NSLocalizedString(@"error.connectServerTimeout", @"Connect to the server timed out!");
                break;
            case AgoraChatErrorUserAlreadyExist:
                errorDes = NSLocalizedString(@"login.taken", @"Username taken");
                break;
            default:
                errorDes = NSLocalizedString(@"login.failure", @"login failure");
                break;
        }
        
        dispatch_async(dispatch_get_main_queue(), ^{
            self.hintTitleLabel.text = errorDes;
        });
    };
    
    //unify token login
    [[AgoraChatHttpRequest sharedManager] loginToApperServer:[_usernameTextField.text lowercaseString] pwd:_passwordTextField.text completion:^(NSInteger statusCode, NSString * _Nonnull response) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self updateLoginStateWithStart:NO];
            NSString *alertStr = nil;
            if (response && response.length > 0 && statusCode) {
                NSData *responseData = [response dataUsingEncoding:NSUTF8StringEncoding];
                NSDictionary *responsedict = [NSJSONSerialization JSONObjectWithData:responseData options:0 error:nil];
                NSString *token = [responsedict objectForKey:@"accessToken"];
                NSString *loginName = [responsedict objectForKey:@"chatUserName"];
                if (token && token.length > 0) {
                    [[AgoraChatClient sharedClient] loginWithUsername:[loginName lowercaseString] token:token completion:^(NSString *aUsername, AgoraChatError *aError) {
                        finishBlock(aUsername, aError);
                    }];
                    return;
                } else {
                    alertStr = NSLocalizedString(@"login analysis token failure", @"analysis token failure");
                }
            } else {
                alertStr = NSLocalizedString(@"login appserver failure", @"Sign in appserver failure");
            }
            
            self.hintView.hidden = NO;
            self.hintTitleLabel.text = alertStr;
        });
    }];
    
}

- (void)doSignUp {
    //register unify token user
    [[AgoraChatHttpRequest sharedManager] registerToApperServer:[_usernameTextField.text lowercaseString] pwd:[_passwordTextField.text lowercaseString] completion:^(NSInteger statusCode, NSString * _Nonnull response) {
        dispatch_async(dispatch_get_main_queue(),^{
            NSString *alertStr = nil;
            if (response != nil) {
                NSData *responseData = [response dataUsingEncoding:NSUTF8StringEncoding];
                NSDictionary *responsedict = [NSJSONSerialization JSONObjectWithData:responseData options:0 error:nil];
                if (responsedict != nil) {
                    NSString *result = [responsedict objectForKey:@"code"];
                    if ([result isEqualToString:@"RES_OK"]) {
                        alertStr = NSLocalizedString(@"login.signup.success", @"Sign up success");
                    } else {
                        alertStr = result;
                    }
                } else {
                    alertStr = NSLocalizedString(@"login.signup.failure", @"Sign up failure");
                }
            } else {
                alertStr = NSLocalizedString(@"login.signup.failure", @"Sign up failure");
            }
            [self updateLoginStateWithStart:NO];
            
            self.hintView.hidden = NO;
            self.hintTitleLabel.text = alertStr;
        });
    }];
}

#pragma mark - notification

#pragma mark - KeyBoard

- (void)keyBoardWillShow:(NSNotification *)note
{
    // 获取用户信息
    NSDictionary *userInfo = [NSDictionary dictionaryWithDictionary:note.userInfo];
    // 获取键盘高度
    CGRect keyBoardBounds  = [[userInfo objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    CGFloat keyBoardHeight = keyBoardBounds.size.height;
    
    CGFloat offset = 0;
    if (self.contentView.frame.size.height - keyBoardHeight <= CGRectGetMaxY(self.loginButton.frame)) {
        offset = CGRectGetMaxY(self.loginButton.frame) - (self.contentView.frame.size.height - keyBoardHeight);
    } else {
        return;
    }
    
    void (^animation)(void) = ^void(void) {
        [self.logoImageView mas_updateConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.contentView.mas_top).offset(134.0 - offset - 20);
        }];
    };
    [self keyBoardWillShow:note animations:animation completion:nil];
}

- (void)keyBoardWillHide:(NSNotification *)note
{
    void (^animation)(void) = ^void(void) {
        [self.logoImageView mas_updateConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.contentView.mas_top).offset(134.0);
        }];
    };
    [self keyBoardWillHide:note animations:animation completion:nil];
}

#pragma mark getter and setter
- (UIImageView *)logoImageView {
    if (_logoImageView == nil) {
        _logoImageView = [[UIImageView alloc] init];
        _logoImageView.contentMode = UIViewContentModeScaleAspectFill;
        _logoImageView.image = [UIImage imageNamed:@"login_logo"];
    }
    return _logoImageView;
}

- (UIImageView *)titleImageView {
    if (_titleImageView == nil) {
        _titleImageView = [[UIImageView alloc] init];
        _titleImageView.contentMode = UIViewContentModeScaleAspectFill;
        _titleImageView.image = [UIImage imageNamed:@"login_agoraChat"];
    }
    return _titleImageView;
}

- (UIView *)hintView
{
    if (!_hintView) {
        _hintView = [[UIView alloc]init];
        
        [_hintView addSubview:self.hintTitleLabel];
        [self.hintTitleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(_hintView);
            make.centerX.equalTo(_hintView.mas_centerX).offset(11);
            make.height.equalTo(@20);
        }];
        
        UIImageView *failImg = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"loginFail"]];
        [_hintView addSubview:failImg];
        [failImg mas_makeConstraints:^(MASConstraintMaker *make) {
            make.width.height.equalTo(@18);
            make.centerY.equalTo(_hintView);
            make.right.equalTo(self.hintTitleLabel.mas_left).offset(-4);
        }];
    }
    return  _hintView;
}

- (UILabel *)hintTitleLabel
{
    if (!_hintTitleLabel) {
        _hintTitleLabel = [[UILabel alloc]init];
        _hintTitleLabel.textColor = [UIColor blackColor];
        _hintTitleLabel.font = [UIFont fontWithName:@"PingFang SC" size:14.0];
    }
    return _hintTitleLabel;
}

- (UITextField *)usernameTextField {
    if (_usernameTextField == nil) {
        _usernameTextField = [[UITextField alloc] init];
        _usernameTextField.backgroundColor = [UIColor colorWithHexString:@"0xF2F2F2"];
        _usernameTextField.delegate = self;
        _usernameTextField.borderStyle = UITextBorderStyleNone;
        _usernameTextField.attributedPlaceholder = [self textFieldAttributeString:@"AgoraID"];
        
        _usernameTextField.returnKeyType = UIReturnKeyDone;
        _usernameTextField.font = [UIFont fontWithName:@"PingFang SC" size:14.0];
        _usernameTextField.textColor = [UIColor colorWithHexString:@"0x000000"];
        _usernameTextField.leftView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 18, 10)];
        _usernameTextField.leftViewMode = UITextFieldViewModeAlways;
        _usernameTextField.layer.cornerRadius = kLoginButtonHeight * 0.5;
        
    }
    return _usernameTextField;
}

- (UITextField *)passwordTextField {
    if (_passwordTextField == nil) {
        _passwordTextField = [[UITextField alloc] init];
        _passwordTextField.backgroundColor = [UIColor colorWithHexString:@"0xF2F2F2"];
        _passwordTextField.delegate = self;
        _passwordTextField.borderStyle = UITextBorderStyleNone;

        _passwordTextField.font = [UIFont fontWithName:@"PingFang SC" size:14.0];
        _passwordTextField.textColor = [UIColor colorWithHexString:@"0x000000"];
        _passwordTextField.attributedPlaceholder = [self textFieldAttributeString:@"Password"];

        _passwordTextField.returnKeyType = UIReturnKeyDone;
        _passwordTextField.clearsOnBeginEditing = NO;
        _passwordTextField.leftView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 18, 10)];
        _passwordTextField.leftViewMode = UITextFieldViewModeAlways;
        _passwordTextField.layer.cornerRadius = kLoginButtonHeight * 0.5;
    }
    return _passwordTextField;
}


- (UIButton *)loginButton {
    if (_loginButton == nil) {
        _loginButton = [[UIButton alloc] init];
        _loginButton.titleLabel.font = [UIFont fontWithName:@"PingFang SC" size:16.0];
        _loginButton.titleLabel.textColor = [UIColor colorWithHexString:@"0x000000"];
        [_loginButton setTitle:@"Sign in" forState:UIControlStateNormal];
        [_loginButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _loginButton.backgroundColor = [UIColor colorWithHexString:@"0x114EFF"];
        [_loginButton addTarget:self action:@selector(doSignIn) forControlEvents:UIControlEventTouchUpInside];
        _loginButton.layer.cornerRadius = kLoginButtonHeight * 0.5;
        
        [_loginButton addSubview:self.loadingImageView];
        
        [self.loadingImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.center.equalTo(_loginButton);
        }];
        
    }
    return _loginButton;
}


- (UIImageView *)loadingImageView {
    if (_loadingImageView == nil) {
        _loadingImageView = [[UIImageView alloc] init];
        _loadingImageView.contentMode = UIViewContentModeScaleAspectFill;
        _loadingImageView.image = [UIImage imageNamed:@"loading"];
        _loadingImageView.hidden = YES;
    }
    return _loadingImageView;
}

- (UIButton *)accountTypeBtn {
    if (_accountTypeBtn == nil) {
        _accountTypeBtn = [[UIButton alloc] init];
        _accountTypeBtn.tag = 0;
        _accountTypeBtn.titleLabel.font = [UIFont fontWithName:@"PingFang SC" size:16.0];
        _accountTypeBtn.titleLabel.textColor = [UIColor colorWithHexString:@"0x000000"];
        [_accountTypeBtn setTitle:@"Sign up" forState:UIControlStateNormal];
        [_accountTypeBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _accountTypeBtn.backgroundColor = [UIColor colorWithHexString:@"0x114EFF"];
        [_accountTypeBtn addTarget:self action:@selector(switchType) forControlEvents:UIControlEventTouchUpInside];
        _accountTypeBtn.layer.cornerRadius = 15;
    }
    return  _accountTypeBtn;
}


@end
#undef loginButtonHeight
#undef kMaxLimitLength

