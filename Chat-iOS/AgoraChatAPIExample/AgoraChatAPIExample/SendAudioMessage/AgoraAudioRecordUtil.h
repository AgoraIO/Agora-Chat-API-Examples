//
//  AgoraAudioRecordUtil.h
//  AgoraChatMessage
//
//  Created by zhangchong on 2022/04/18.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface AgoraAudioRecordUtil : NSObject

+ (instancetype)sharedHelper;

- (void)startRecordWithPath:(NSString *)aPath
                 completion:(void(^)(NSError *error))aCompletion;

-(void)stopRecordWithCompletion:(void(^)(NSString *aPath, NSInteger aTimeLength))aCompletion;

-(void)cancelRecord;

@end

NS_ASSUME_NONNULL_END
