//
//  RNEasyIjkplayerView.m
//  RNEasyIjkplayer
//
//  Created by lj on 2019/11/10.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import "RNEasyIjkplayerView.h"
#import <IJKMediaFramework/IJKMediaFramework.h>

@interface RNEasyIjkplayerView()

    @property (nonatomic, strong) IJKFFMoviePlayerController *player;
    @property (nonatomic, strong) UIView *playerView;

@end

@implementation RNEasyIjkplayerView

    -(instancetype)init{
        self = [super init];
        if(self){
            self.backgroundColor = [UIColor blackColor];
        }
        return self;
    }



    -(void)play{
        if(!_player){
            if(_url== nil){
                NSLog(@"url不能为空");
                return;
            }
            
            _player = [[IJKFFMoviePlayerController alloc]initWithContentURL:_url withOptions:nil];
            
            //获取视频的view层
            _playerView = [_player view];
            _playerView.frame = self.bounds; //设置视频的宽高
            _playerView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
            
            [self insertSubview:_playerView atIndex:1];
            
            [_player setScalingMode:IJKMPMovieScalingModeAspectFill];
            [_player prepareToPlay];
            
            [self installMovieNotificationObservers];
        }else{
            if(_player.isPlaying!=YES){
                [_player play];
                [self startSendProgressUpdate];
            }
        }
        
    }

    -(void)startSendProgressUpdate{
        __weak __typeof(self) weakSelf = self;
        dispatch_async(dispatch_get_global_queue(0, 0), ^{
            __strong __typeof(weakSelf) strongSelf = weakSelf;
            if (strongSelf) {
                strongSelf.progressUpdateThread = [NSThread currentThread];
                [strongSelf.progressUpdateThread setName:@"Update Progress Thread"];
                strongSelf.progressUpdateTimer = [NSTimer scheduledTimerWithTimeInterval:1 target:strongSelf selector:@selector(updateProgressAction) userInfo:nil repeats:YES];
                NSRunLoop *runloop = [NSRunLoop currentRunLoop];
                [runloop addTimer:strongSelf.progressUpdateTimer forMode:NSDefaultRunLoopMode];
                [runloop run];
            }
        });
    }

    -(void)updateProgressAction{
        
        if(self.onProgressUpdate){
            NSTimeInterval playbackTime = _player.currentPlaybackTime;
            NSInteger progress = playbackTime;
            self.onProgressUpdate(@{@"progress":@(progress)});
        }
    }

    - (void)cancelUpdateTimerAction{
        if (self.progressUpdateTimer) {
            [self.progressUpdateTimer invalidate];
            self.progressUpdateTimer = nil;
        }
    }

    - (void)cancelTimer{
        if (self.progressUpdateTimer && self.progressUpdateThread) {
            [self performSelector:@selector(cancelUpdateTimerAction) onThread:self.progressUpdateThread withObject:nil waitUntilDone:YES];
        }
    }

    -(void)pause{
        if(_player.isPlaying == YES){
            [_player pause];
            [self cancelTimer];
        }
    }

    -(void)stop{
        if(_player){
            [_player stop];
            _player = nil;
            [self cancelTimer];
            [_playerView removeFromSuperview];
            _playerView=nil;
        }
    }

    -(void)seekTo:(NSInteger)time{
        _player.currentPlaybackTime = time;
        NSLog(@"");
    }

    -(void)didMoveToWindow{
        if([_autoPlay compare:[NSNumber numberWithInt:1]] == NSOrderedSame){
            NSLog(@"");
            [self play];
        }
    }

    -(void)mediaIsPreparedToPlayDidChange:(NSNotification*)notifacation{
        
        NSTimeInterval time = _player.duration;
        NSInteger ttime = time;
        _duration = [NSNumber numberWithInteger:ttime];
        
        CGSize size= _player.naturalSize;
        if(size.width && size.height){
            _size = @{@"width":@(size.width),@"height":@(size.height)};
        }
        if(self.onPrepared){
            self.onPrepared(@{@"duration":@([_duration intValue]),@"size":_size});
        }
        
        [_player play];
        
        [self startSendProgressUpdate];
        
    }

    -(void)mediaPlayerPlaybackDidComplete:(NSNotification*)notifacation{
        NSLog(@"Playback did complete");
        NSLog(@"%@",notifacation);
        [self cancelTimer];
        NSNumber *reason = [notifacation.userInfo objectForKey:IJKMPMoviePlayerPlaybackDidFinishReasonUserInfoKey];
        
        if([reason compare: [NSNumber numberWithInteger:IJKMPMovieFinishReasonPlaybackEnded]] == NSOrderedSame ){
            NSLog(@"normal complete");
            if(self.onComplete){
                self.onComplete(@{});
            }
        }else if([reason compare: [NSNumber numberWithInteger:IJKMPMovieFinishReasonPlaybackError]] == NSOrderedSame ){
            NSLog(@"Error complete");
            if(self.onError){
                self.onError(@{@"info":@"error complete"});
            }
        }else if([reason compare: [NSNumber numberWithInteger:IJKMPMovieFinishReasonUserExited]] == NSOrderedSame ){
            NSLog(@"user exit complete");
        }
        if(self.onInfo){
            self.onInfo(@{@"info":@"complete"});
        }
        
    }

    -(void)mediaPlayerPlaybackStateDidChange:(NSNotification*)notifacation{
        NSLog(@"Playback state change");
        //  IJKMPMoviePlaybackStateStopped,
        //  IJKMPMoviePlaybackStatePlaying,
        //  IJKMPMoviePlaybackStatePaused,
        //  IJKMPMoviePlaybackStateInterrupted,
        //  IJKMPMoviePlaybackStateSeekingForward,
        //  IJKMPMoviePlaybackStateSeekingBackward
        if(_player.playbackState ==  IJKMPMoviePlaybackStatePlaying){
            NSLog(@"playback state changed to playing");
            if(self.onInfo){
                self.onInfo(@{@"info":@"playing"});
            }
        }else if(_player.playbackState == IJKMPMoviePlaybackStatePaused){
            NSLog(@"playback state changed to pause");
            if(self.onInfo){
                self.onInfo(@{@"info":@"pause"});
            }
        }else if(_player.playbackState == IJKMPMoviePlaybackStateStopped){
            if(self.onInfo){
                self.onInfo(@{@"info":@"stop"});
            }
        }else{
            
        }
    }

    -(void)mediaPlayerLoadStateDidChange:(NSNotification*)notifacation{
        if(self.onLoadProgressUpdate){
            self.onLoadProgressUpdate(@{@"bufferingProgress":@(_player.bufferingProgress)});
        }
    }

    -(void)mediaPlayerSeekDidComplete:(NSNotification*)notifacation{
    }

    - (void)installMovieNotificationObservers {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(mediaIsPreparedToPlayDidChange:) name:IJKMPMediaPlaybackIsPreparedToPlayDidChangeNotification object:_player];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(mediaPlayerPlaybackDidComplete:) name:IJKMPMoviePlayerPlaybackDidFinishNotification object:_player];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(mediaPlayerPlaybackStateDidChange:) name:IJKMPMoviePlayerPlaybackStateDidChangeNotification object:_player];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(mediaPlayerLoadStateDidChange:) name:IJKMPMoviePlayerLoadStateDidChangeNotification object:_player];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(mediaPlayerSeekDidComplete:) name:IJKMPMoviePlayerDidSeekCompleteNotification object:_player];
        
        
    }

    -(void)removeMovieNotificationObservers{
        [[NSNotificationCenter defaultCenter]removeObserver:self name:IJKMPMediaPlaybackIsPreparedToPlayDidChangeNotification object:_player];
        
        [[NSNotificationCenter defaultCenter]removeObserver:self name:IJKMPMoviePlayerPlaybackDidFinishNotification object:_player];
        [[NSNotificationCenter defaultCenter]removeObserver:self name:IJKMPMoviePlayerPlaybackStateDidChangeNotification object:_player];
        [[NSNotificationCenter defaultCenter]removeObserver:self name:IJKMPMoviePlayerLoadStateDidChangeNotification object:_player];
        [[NSNotificationCenter defaultCenter]removeObserver:self name:IJKMPMoviePlayerDidSeekCompleteNotification object:_player];
    }

    -(void)dealloc{
        [self removeMovieNotificationObservers];
        if(_player){
            [_player stop];
            _player = nil;
        }
    }

@end
