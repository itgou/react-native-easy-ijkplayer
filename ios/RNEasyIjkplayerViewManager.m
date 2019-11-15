//
//  RNEasyIjkplayerViewManager.m
//  RNEasyIjkplayer
//
//  Created by lj on 2019/11/10.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import "RNEasyIjkplayerViewManager.h"
#import <React/RCTUIManager.h>
#import <React/RCTLog.h>
#import <React/RCTEventEmitter.h>

@interface RNEasyIjkplayerViewManager()
@end

@implementation RNEasyIjkplayerViewManager
    RCT_EXPORT_MODULE(RNEasyIjkplayerView)
    // RNTMapManager.m

    RCT_EXPORT_VIEW_PROPERTY(onPrepared, RCTBubblingEventBlock)
    RCT_EXPORT_VIEW_PROPERTY(onProgressUpdate, RCTBubblingEventBlock)
    RCT_EXPORT_VIEW_PROPERTY(onLoadProgressUpdate, RCTBubblingEventBlock)
    RCT_EXPORT_VIEW_PROPERTY(onInfo, RCTBubblingEventBlock)
    RCT_EXPORT_VIEW_PROPERTY(onComplete, RCTBubblingEventBlock)
    RCT_EXPORT_VIEW_PROPERTY(onError, RCTBubblingEventBlock)

    RCT_CUSTOM_VIEW_PROPERTY(options, NSDictionary, RNEasyIjkplayerView){
        
        NSString *urlString = [json objectForKey:@"url"];
        if(urlString){
            view.url = [NSURL URLWithString:urlString];
        }
        view.autoPlay= [json objectForKey:@"autoPlay"] ? [json objectForKey:@"autoPlay"] : @0    ;
        
    }

    RCT_EXPORT_METHOD(play:(nonnull NSNumber*) reactTag){
        [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
            RNEasyIjkplayerView *view =(RNEasyIjkplayerView *) viewRegistry[reactTag];
            if (!view || ![view isKindOfClass:[RNEasyIjkplayerView class]]) {
                RCTLogError(@"Cannot find NativeView with tag #%@", reactTag);
                return;
            }
            [view play];
        }];
    }

    RCT_EXPORT_METHOD(pause:(nonnull NSNumber*) reactTag){
        [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
            RNEasyIjkplayerView *view = (RNEasyIjkplayerView *) viewRegistry[reactTag];
            if (!view || ![view isKindOfClass:[RNEasyIjkplayerView class]]) {
                RCTLogError(@"Cannot find NativeView with tag #%@", reactTag);
                return;
            }
            [view pause];
        }];
    }

    RCT_EXPORT_METHOD(stop:(nonnull NSNumber*) reactTag){
        [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
            RNEasyIjkplayerView *view =(RNEasyIjkplayerView *) viewRegistry[reactTag];
            if (!view || ![view isKindOfClass:[RNEasyIjkplayerView class]]) {
                RCTLogError(@"Cannot find NativeView with tag #%@", reactTag);
                return;
            }
            [view stop];
        }];
    }


    RCT_EXPORT_METHOD(seekTo:(nonnull NSNumber*) reactTag time:(NSInteger) time ){
        [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
            RNEasyIjkplayerView *view = (RNEasyIjkplayerView *) viewRegistry[reactTag];
            if (!view || ![view isKindOfClass:[RNEasyIjkplayerView class]]) {
                RCTLogError(@"Cannot find NativeView with tag #%@", reactTag);
                return;
            }
            if(time){
                [view seekTo:time];
            }
        }];
    }

    RCT_EXPORT_METHOD(getDuration:(nonnull NSNumber*) reactTag callback:(RCTResponseSenderBlock)callback){
        [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
            RNEasyIjkplayerView *view = (RNEasyIjkplayerView *)viewRegistry[reactTag];
            if (!view || ![view isKindOfClass:[RNEasyIjkplayerView class]]) {
                RCTLogError(@"Cannot find NativeView with tag #%@", reactTag);
                return;
            }
            if(view.duration){
                callback(@[@NO,view.duration]);
            }else{
                callback(@[@YES]);
            }
            
        }];
    }

    RCT_EXPORT_METHOD(getSize:(nonnull NSNumber*) reactTag callback:(RCTResponseSenderBlock)callback){
        [self.bridge.uiManager addUIBlock:^(RCTUIManager *uiManager, NSDictionary<NSNumber *,UIView *> *viewRegistry) {
            RNEasyIjkplayerView *view = (RNEasyIjkplayerView *)viewRegistry[reactTag];
            if (!view || ![view isKindOfClass:[RNEasyIjkplayerView class]]) {
                RCTLogError(@"Cannot find NativeView with tag #%@", reactTag);
                return;
            }
            if(view.size){
                callback(@[@NO,view.size]);
            }else{
                callback(@[@YES]);
            }
            
        }];
    }

    -(UIView *)view{
        
        //直播视频
        // http://img.elleshop.com.cn/media/product/14994134515891.mp4
        // http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8
        RNEasyIjkplayerView *view = [[RNEasyIjkplayerView alloc]init];
        
        return view;
    }

@end
