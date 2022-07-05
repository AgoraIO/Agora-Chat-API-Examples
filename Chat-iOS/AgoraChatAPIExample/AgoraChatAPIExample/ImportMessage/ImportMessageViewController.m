//
//  ImportMessageViewController.m
//  AgoaChatMessageImport
//
//  Created by zhangchong on 2022/4/13.
//

#import "ImportMessageViewController.h"
#import <AgoraChat/AgoraChat.h>
#import <Masonry/Masonry.h>
#import "EMHttpRequest.h"

@interface ImportMessageViewController ()<UINavigationControllerDelegate>
//@property (nonatomic, strong) SBDGroupChannelListQuery *groupChannelQuery;

@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) UIView *bottomLine;
@property (nonatomic, strong) UITextView *resultView;

@property (nonatomic, strong) UITextField *nameField;
@property (nonatomic, strong) UITextField *pswdField;

@property (nonatomic, strong) UIButton *loginBtn;
@property (nonatomic, strong) UIButton *importBtn;

@property (nonatomic, strong) NSDateFormatter *formatter;

@property (nonatomic, strong) NSMutableArray *channelAry;
@end

@implementation ImportMessageViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];

    self.channelAry = [[NSMutableArray alloc]init];
    [self initAgoraChatSdk];
    [self _setupSubviews];
    // Do any additional setup after loading the view.
}

- (void)dealloc {
    [[AgoraChatClient sharedClient] logout:YES];
}

- (void)_setupSubviews
{
    self.scrollView = [[UIScrollView alloc]init];
    self.scrollView.accessibilityActivationPoint = CGPointMake(0, 0);
    self.scrollView.backgroundColor = [UIColor whiteColor];
    self.scrollView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    self.scrollView.scrollsToTop = YES;
    self.scrollView.bounces = NO;
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
        make.top.equalTo(self.scrollView).offset(50);
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
    
    self.importBtn = [[UIButton alloc] init];
    self.importBtn.clipsToBounds = YES;
    self.importBtn.layer.cornerRadius = 5;
    self.importBtn.backgroundColor = [UIColor colorWithRed:((float) 78 / 255.0f) green:0 blue:((float) 234 / 255.0f) alpha:1];
    self.importBtn.titleLabel.font = [UIFont systemFontOfSize:19];
    [self.importBtn setTitle:@"Import message" forState:UIControlStateNormal];
    [self.importBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [self.importBtn addTarget:self action:@selector(importAction) forControlEvents:UIControlEventTouchUpInside];
    [self.scrollView addSubview:self.importBtn];
    [self.importBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.nameField);
        make.top.equalTo(self.loginBtn.mas_bottom).offset(20);
        make.height.mas_equalTo(@50);
        make.width.mas_equalTo(@150);
    }];
}

- (void)initAgoraChatSdk
{
    AgoraChatOptions *options = [AgoraChatOptions optionsWithAppkey:@"41117440#383391"];
    options.enableConsoleLog = YES;
    [[AgoraChatClient sharedClient] initializeSDKWithOptions:options];
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

- (void)importAction
{
    if (![AgoraChatClient sharedClient].isLoggedIn) {
        [self printLog:@"please login agora chat server !"];
        return;
    }
    [self _loadLocalMessageFile];
}

/*
- (void)loadSendBirdGroupChannels {
    // Create a query using setters in the SBDGroupChannelCollection.
    self.groupChannelQuery = [SBDGroupChannel createMyGroupChannelListQuery];
    self.groupChannelQuery.includeEmptyChannel = YES;
    self.groupChannelQuery.order = SBDGroupChannelListOrderChronological;
    [self.groupChannelQuery setUserIdsIncludeFilter:@[@"chong"] queryType:SBDGroupChannelListQueryTypeOr];

    [self _loadLocalMessageFile];
    //[self _loadChannels];
}
*/

// 读取本地 JSON 消息数据文件
- (void)_loadLocalMessageFile
{
    NSString *path = [[NSBundle mainBundle] pathForResource:@"SBDChannelMessage" ofType:@"txt"];
    NSString *channelMessageJsonStr = [NSString stringWithContentsOfFile:path encoding:NSUTF8StringEncoding error:nil];
    NSLog(@"%@",channelMessageJsonStr);
    [self _jsonChannelLoaclStorage:channelMessageJsonStr];
}

// 解析 JSON 数据并创建本地存储 AgoraChatConversation
- (void)_jsonChannelLoaclStorage:(NSString *)jsonChannelMessage
{
    NSData *data = [jsonChannelMessage dataUsingEncoding:NSUTF8StringEncoding];
    NSMutableArray *ary = (NSMutableArray *)[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
    for (NSMutableDictionary *dict in ary) {
        [self printLog:[NSString stringWithFormat:@"load channel ! name : %@, channelUrl : %@",[dict objectForKey:@"channelName"], [dict objectForKey:@"channelUrl"]]];
        
        AgoraChatConversation *agoraChatconversation = [[AgoraChatClient sharedClient].chatManager getConversation:[dict objectForKey:@"channelUrl"] type:AgoraChatConversationTypeGroupChat createIfNotExist:YES];
        [self printLog:[NSString stringWithFormat:@"create conversation ! conversationId : %@, conversationType : %u",agoraChatconversation.conversationId, agoraChatconversation.type]];
        
        [self _jsonMessageLoaclStorage:[dict objectForKey:@"messages"] conversation:agoraChatconversation];
    }
}

// 解析 JSON 数据并创建 AgoraChatMessage，并插入本地数据库
- (void)_jsonMessageLoaclStorage:(NSArray *)messageAry conversation:(AgoraChatConversation *)agoraChatConversation
{
    for (NSDictionary *messageDict in messageAry) {
        AgoraChatMessage *agoraChatMessage = [self translatJsonMessage:messageDict conversationId:agoraChatConversation.conversationId];
        [agoraChatConversation insertMessage:agoraChatMessage error:nil];
        
        [self printLog:[NSString stringWithFormat:@"insert message , messageid : %@, messageType : %u, conversationID : %@", agoraChatMessage.messageId, agoraChatMessage.body.type, agoraChatMessage.conversationId]];
    }
}



/*

- (void)_loadChannels
{
    if (!self.groupChannelQuery) {
        [self printLog:@"create groupChannelQuery fail !"];
        return;
    }
    
    __weak typeof(self) weakself = self;
    [self.groupChannelQuery loadNextPageWithCompletionHandler:^(NSArray<SBDGroupChannel *> * _Nullable channels, SBDError * _Nullable error) {
        if (error != nil) {
            // Handle error.
            [weakself printLog:@"load channels fail !"];
            return;
        }
        
        [weakself printLog:[NSString stringWithFormat:@"load channel ! channel count : %lu",(unsigned long)[channels count]]];
        
        for (SBDGroupChannel *groupChannel in channels) {
            [weakself printLog:[NSString stringWithFormat:@"load channel ! name : %@, channelUrl : %@",groupChannel.name, groupChannel.channelUrl]];
            
            AgoraChatConversation *agoraChatconversation = [weakself groupChannelConvertToAgoraChatConversation:groupChannel];
            [[AgoraChatClient sharedClient].chatManager importConversations:@[agoraChatconversation] completion:^(AgoraChatError *aError) {
                if (!aError) {
                    [weakself printLog:[NSString stringWithFormat:@"import conversation success! conversationId : %@, conversationType : %u",agoraChatconversation.conversationId, agoraChatconversation.type]];
                    
                    [weakself loadGroupChannelMessages:groupChannel agoraChatConversation:agoraChatconversation];
                } else {
                    [weakself printLog:[NSString stringWithFormat:@"import conversation fail ! conversationId : %@, conversationType : %u",agoraChatconversation.conversationId, agoraChatconversation.type]];
                }
            }];
        }
    }];
}

- (AgoraChatConversation *)groupChannelConvertToAgoraChatConversation:(SBDGroupChannel *)groupChannel
{
    return [[AgoraChatClient sharedClient].chatManager getConversation:groupChannel.channelUrl type:AgoraChatConversationTypeGroupChat createIfNotExist:YES];
}

- (void)loadGroupChannelMessages:(SBDGroupChannel *)groupChannel agoraChatConversation:(AgoraChatConversation *)conversation
{
    // Create a MessageCollection instance.
    long long startingPoint = 0;
    // You can use a SBDMessageListParams instance for the SBDMessageCollection.
    SBDMessageListParams *params = [[SBDMessageListParams alloc] init];
    params.reverse = NO;
    SBDMessageCollection *messageCollection = [[SBDMessageCollection alloc] initWithChannel:groupChannel startingPoint:startingPoint params:params];
    
    __weak typeof(self) weakself = self;
    [messageCollection startCollectionWithInitPolicy:SBDMessageCollectionInitPolicyCacheAndReplaceByApi cacheResultHandler:^(NSArray<SBDBaseMessage *> * _Nullable messages, SBDError * _Nullable error) {
        if (error) {
            [weakself printLog:@"load channel message fail !"];
            return;
        }
        
        [weakself printLog:[NSString stringWithFormat:@"load channel message complete , channel name : %@, message count : %lu, channelUrl : %@",groupChannel.name, (unsigned long)[messages count], groupChannel.channelUrl]];
        
        if (!messages || [messages count] == 0) {
            return;
        }
        
        NSMutableArray *msgAry = [[NSMutableArray alloc]init];
        NSMutableDictionary *msgDict = nil;
        
        // Messages will be retrieved from the local cache.
        for (SBDBaseMessage *sbdBaseMessage in messages) {
            AgoraChatMessage *agoraChatMessage = [weakself translatemessage:sbdBaseMessage conversationId:conversation.conversationId];
            [conversation insertMessage:agoraChatMessage error:nil];
            
            [weakself printLog:[NSString stringWithFormat:@"insert message , messageid : %@, messageType : %u, conversationID : %@", agoraChatMessage.messageId, agoraChatMessage.body.type, agoraChatMessage.conversationId]];
            
            msgDict = [self convertSbdMessageToDict:sbdBaseMessage];
            [msgAry addObject:msgDict];
        }
        
        NSMutableDictionary * channelDict = [self converChannelToDict:groupChannel];
        [channelDict setObject:msgAry forKey:@"messages"];
        
        [self.channelAry addObject:channelDict];
        [self convertToJson:self.channelAry];
        
    } apiResultHandler:^(NSArray<SBDBaseMessage *> * _Nullable messages, SBDError * _Nullable error) {
       
    }];
}

- (NSString *)convertToJson:(NSArray *)ary
{
    NSError *error = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:ary options:NSJSONWritingPrettyPrinted error:&error];
    if ([jsonData length] == 0 || error != nil) {
        return nil;
    }
    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    NSLog(@"%@", jsonString);
    return jsonString;
}

- (NSMutableDictionary *)converChannelToDict:(SBDGroupChannel *)groupChannel
{
    NSMutableDictionary * channelDict = [[NSMutableDictionary alloc]init];
    [channelDict setObject:groupChannel.name forKey:@"channelName"];
    [channelDict setObject:groupChannel.channelUrl forKey:@"channelUrl"];
    return channelDict;
}

- (NSMutableDictionary *)convertSbdMessageToDict:(SBDBaseMessage *)sbdBaseMessage
{
    NSMutableDictionary *msgDict = [[NSMutableDictionary alloc]init];
    [msgDict setObject:[self convertMsgType:sbdBaseMessage] forKey:@"type"];
    if ([sbdBaseMessage isKindOfClass:[SBDUserMessage class]]){
        [msgDict setObject:sbdBaseMessage.message forKey:@"message"];
    }
        
    if ([sbdBaseMessage isKindOfClass:[SBDAdminMessage class]]){
        [msgDict setObject:sbdBaseMessage.message forKey:@"message"];
    }
        
    if ([sbdBaseMessage isKindOfClass:[SBDFileMessage class]]) {
        SBDFileMessage *fileMessage = (SBDFileMessage *)sbdBaseMessage;
        if ([fileMessage.thumbnails count] > 0) {
            [msgDict setObject:[NSString stringWithFormat:@"%@",[fileMessage getFileMessageParams].file] forKey:@"file"];
            [msgDict setObject:fileMessage.name forKey:@"name"];
            [msgDict setObject:[NSNumber numberWithUnsignedLong:[fileMessage getFileMessageParams].fileSize] forKey:@"fileSize"];
            [msgDict setObject:fileMessage.thumbnails[0].url forKey:@"thumbnailRemoteUrl"];
            [msgDict setObject:[fileMessage.thumbnails[0] url] forKey:@"thumbnailLocalUrl"];
            [msgDict setObject:[fileMessage getFileMessageParams].fileName forKey:@"thumbnailFileName"];
            [msgDict setObject:[fileMessage url] forKey:@"localUrl"];
            [msgDict setObject:fileMessage.url forKey:@"remoteUrl"];
            [msgDict setObject:fileMessage.name forKey:@"fileName"];
        } else {
            [msgDict setObject:[fileMessage url] forKey:@"localUrl"];
            [msgDict setObject:fileMessage.name forKey:@"fileName"];
            [msgDict setObject:fileMessage.url forKey:@"remoteUrl"];
            [msgDict setObject:[NSNumber numberWithUnsignedLong:fileMessage.size] forKey:@"size"];
        }
    }
    
    [msgDict setObject:sbdBaseMessage.sender.userId forKey:@"sender"];
    [msgDict setObject:[NSString stringWithFormat:@"%lld", sbdBaseMessage.messageId] forKey:@"messageId"];
    [msgDict setObject:[NSNumber numberWithInt:[self getMessageStatus:sbdBaseMessage.sendingStatus]] forKey:@"sendingStatus"];
    [msgDict setObject:[NSNumber numberWithLongLong:sbdBaseMessage.createdAt] forKey:@"createdAt"];

    return msgDict;
}

- (NSString *)convertMsgType:(SBDBaseMessage *)sbdBaseMessage
{
    if ([sbdBaseMessage isKindOfClass:[SBDUserMessage class]])
        return @"MessageTypeText";
    if ([sbdBaseMessage isKindOfClass:[SBDAdminMessage class]])
        return @"MessageTypeText";
    if ([sbdBaseMessage isKindOfClass:[SBDFileMessage class]]) {
        SBDFileMessage *fileMessage = (SBDFileMessage *)sbdBaseMessage;
        if ([fileMessage.thumbnails count] > 0) {
            return @"MessageTypeImage";
        }
        return @"MessageTypeFile";
    }
    return @"MessageTypeText";;
}

- (AgoraChatMessage *)translatemessage:(SBDBaseMessage *)sbdBaseMessage conversationId:(NSString *)aConversationId
{
    // example
    AgoraChatMessageBody *body = nil;
    if ([sbdBaseMessage isKindOfClass:[SBDUserMessage class]]) {
        body = [[AgoraChatTextMessageBody alloc]initWithText:sbdBaseMessage.message];
    } else if ([sbdBaseMessage isKindOfClass:[SBDAdminMessage class]]) {
        body = [[AgoraChatTextMessageBody alloc]initWithText:sbdBaseMessage.message];
    } else if ([sbdBaseMessage isKindOfClass:[SBDFileMessage class]]) {
        SBDFileMessage *fileMessage = (SBDFileMessage *)sbdBaseMessage;
        if ([fileMessage.thumbnails count] > 0) {
            AgoraChatImageMessageBody *imageBody = [[AgoraChatImageMessageBody alloc]initWithData:[fileMessage getFileMessageParams].file displayName:fileMessage.name];
            imageBody.fileLength = [fileMessage getFileMessageParams].fileSize;
            imageBody.thumbnailRemotePath = fileMessage.thumbnails[0].url;
            imageBody.thumbnailLocalPath = [fileMessage.thumbnails[0] url];
            imageBody.thumbnailDisplayName = [fileMessage getFileMessageParams].fileName;
            imageBody.localPath = [fileMessage url];
            imageBody.remotePath = fileMessage.url;
            imageBody.displayName = fileMessage.name;
            body = imageBody;
        } else {
            AgoraChatFileMessageBody *fileBody = [[AgoraChatFileMessageBody alloc]initWithLocalPath:[fileMessage url] displayName:fileMessage.name];
            fileBody.localPath = [fileMessage url];
            fileBody.remotePath = fileMessage.url;
            fileBody.displayName = fileMessage.name;
            fileBody.fileLength = fileMessage.size;
            body = fileBody;
        }
    } else {
        body = [[AgoraChatTextMessageBody alloc]initWithText:sbdBaseMessage.message];
    }
    
    // example
    if ([sbdBaseMessage.customType isEqualToString:@"userCustomMessageType"]) {
        // create custom match type AgoraChatMessageBody
    }
    // example
    if ([sbdBaseMessage isKindOfClass:[SBDFileMessage class]]) {
        SBDFileMessage *fileMessage = (SBDFileMessage *)sbdBaseMessage;
        if ([fileMessage.type isEqualToString:@"userCustomFileMessageType"]) {
            // create custom match AgoraChatMessageBody.  AgoraChatVideoMessageBody,AgoraChatVoiceMessageBody,AgoraChatImageMessageBody,AgoraChatFileMessageBody
        }
    }
    
    AgoraChatMessage *agoraChatMessage = [[AgoraChatMessage alloc]initWithConversationID:aConversationId from:sbdBaseMessage.sender.userId to:aConversationId body:body ext:nil];
    agoraChatMessage.messageId = [NSString stringWithFormat:@"%lld", sbdBaseMessage.messageId];
    agoraChatMessage.status = [self getMessageStatus:sbdBaseMessage.sendingStatus];
    agoraChatMessage.localTime = sbdBaseMessage.createdAt;
    
    return agoraChatMessage;
}
 */

- (AgoraChatMessage *)translatJsonMessage:(NSDictionary *)messageDict conversationId:(NSString *)aConversationId
{
    AgoraChatMessageBody *body = nil;
    NSString *messageType = [messageDict objectForKey:@"type"];
    if ([messageType isEqualToString:@"MessageTypeText"]) {
        body = [[AgoraChatTextMessageBody alloc]initWithText:[messageDict objectForKey:@"message"]];
    } else if ([messageType isEqualToString:@"MessageTypeImage"]) {
        AgoraChatImageMessageBody *imageBody = [[AgoraChatImageMessageBody alloc]initWithData:[[messageDict objectForKey:@"file"] dataUsingEncoding:NSUTF8StringEncoding] displayName:[messageDict objectForKey:@"name"]];
        NSNumber *fileManageSizeNumber = (NSNumber *)[messageDict objectForKey:@"fileSize"];
        imageBody.fileLength = fileManageSizeNumber.unsignedLongValue;
        imageBody.thumbnailRemotePath = [messageDict objectForKey:@"thumbnailRemoteUrl"];
        imageBody.thumbnailLocalPath = [messageDict objectForKey:@"thumbnailLocalUrl"];
        imageBody.thumbnailDisplayName = [messageDict objectForKey:@"thumbnailFileName"];
        imageBody.localPath = [messageDict objectForKey:@"localUrl"];
        imageBody.remotePath = [messageDict objectForKey:@"remoteUrl"];
        imageBody.displayName = [messageDict objectForKey:@"fileName"];
        body = imageBody;
    } else if ([messageType isEqualToString:@"MessageTypeFile"]) {
        AgoraChatFileMessageBody *fileBody = [[AgoraChatFileMessageBody alloc]initWithLocalPath:(NSString *)[messageDict objectForKey:@"localUrl"] displayName:(NSString *)[messageDict objectForKey:@"fileName"]];
        fileBody.localPath = [messageDict objectForKey:@"localUrl"];
        fileBody.remotePath = [messageDict objectForKey:@"remoteUrl"];
        fileBody.displayName = [messageDict objectForKey:@"fileName"];
        NSNumber *fileSize = (NSNumber *)[messageDict objectForKey:@"size"];
        fileBody.fileLength = [fileSize unsignedLongValue];
        body = fileBody;
    }

    AgoraChatMessage *agoraChatMessage = [[AgoraChatMessage alloc]initWithConversationID:aConversationId from:[messageDict objectForKey:@"sender"] to:aConversationId body:body ext:nil];
    agoraChatMessage.messageId = [messageDict objectForKey:@"messageId"];
    NSNumber *ststusNumber = (NSNumber *)[messageDict objectForKey:@"sendingStatus"];
    agoraChatMessage.status = [ststusNumber intValue];
    NSNumber *localTimeNumber = (NSNumber *)[messageDict objectForKey:@"createdAt"];
    agoraChatMessage.localTime = [localTimeNumber longLongValue];

    return agoraChatMessage;
}

/*
- (AgoraChatMessageStatus)getMessageStatus:(SBDMessageSendingStatus)status
{
    switch (status) {
        case SBDMessageSendingStatusNone:
            return AgoraChatMessageStatusPending;
        case SBDMessageSendingStatusPending:
            return AgoraChatMessageStatusPending;
        case SBDMessageSendingStatusFailed:
        case SBDMessageSendingStatusCanceled:
            return AgoraChatMessageStatusFailed;
        case SBDMessageSendingStatusSucceeded:
            return AgoraChatMessageStatusSucceed;
    }
    return AgoraChatMessageStatusPending;
}*/



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
