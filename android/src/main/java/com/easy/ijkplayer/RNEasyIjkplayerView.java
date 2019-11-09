package com.easy.ijkplayer;

import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class RNEasyIjkplayerView extends SurfaceView implements LifecycleEventListener {

    private static final String TAG = "IJKPlayer";
    private static final String NAME_ERROR_EVENT = "onError";
    private static final String NAME_INFO_EVENT = "onInfo";
    private static final String NAME_COMPLETE_EVENT = "onComplete";
    private static final String NAME_PROGRESS_UPDATE_EVENT = "onProgressUpdate";
    private static final String NAME_PREPARE_EVENT = "onPrepared";
    public static final int PROGRESS_UPDATE_INTERVAL_MILLS = 500;
    private IjkMediaPlayer mIjkPlayer;
    public static int mDuration;
    public static int mAutoPlay = 0;
    public static WritableMap size = Arguments.createMap();
    private String mCurrUrl;
    private boolean mManualPause;
    private boolean mManualStop;
    private Handler mHandler = new Handler();
    private Runnable progressUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIjkPlayer == null || mDuration == 0) {
                return;
            }
            long currProgress = mIjkPlayer.getCurrentPosition();
            int mCurrProgress = (int) Math.ceil((currProgress * 1.0f)/1000);
            sendEvent(NAME_PROGRESS_UPDATE_EVENT, "progress", "" + mCurrProgress);
            mHandler.postDelayed(progressUpdateRunnable, PROGRESS_UPDATE_INTERVAL_MILLS);
        }
    };

    public RNEasyIjkplayerView(ReactContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(this);
        initIjkMediaPlayer();
        initSurfaceView();
        initIjkMediaPlayerListener();
    }


    private void initSurfaceView() {
        this.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.i(TAG, "surface created");
                mIjkPlayer.setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.i(TAG, "surface changed");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG, "surface destroyed");
            }
        });
    }

    private void initIjkMediaPlayer() {
        mIjkPlayer = new IjkMediaPlayer();
    }


    private void initIjkMediaPlayerListener() {
        mIjkPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                mDuration = (int)Math.ceil(mIjkPlayer.getDuration()/1000);
                mHandler.post(progressUpdateRunnable);
                sendEvent(NAME_PREPARE_EVENT, "isPrepare", "1");
            }
        });

        mIjkPlayer.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int width, int height, int i2, int i3) {
                Log.i(TAG, "width:" + width + " height:" + height);
                size.putInt("width", width);
                size.putInt("height", height);
                float ratioHW = height * 1.0f / width;
            }
        });

        mIjkPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int infoCode, int i1) {
                sendEvent(NAME_INFO_EVENT, "code", "" + infoCode);
                return false;
            }
        });

        mIjkPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int errorCode, int i1) {
                sendEvent(NAME_ERROR_EVENT, "code", "" + errorCode);
                return false;
            }
        });

        mIjkPlayer.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {

            }
        });

        mIjkPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
//                mHandler.removeCallbacks(progressUpdateRunnable);
                sendEvent(NAME_COMPLETE_EVENT, "complete", "1");
                stop();
            }
        });
    }

    private void sendEvent(String eventName, String paramName, String paramValue) {
        WritableMap event = Arguments.createMap();
        event.putString(paramName, "" + paramValue);
        ReactContext reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                eventName,
                event);
    }


    public void setMAutoPlay(int autoPlay) {
        mAutoPlay = autoPlay;
    }

    public int getMAutoPlay() {
        return mAutoPlay;
    }

    public void seekTo(long progress) {
        if (mIjkPlayer != null) {
            mIjkPlayer.seekTo(progress);
        }
    }

    public void restart(String url) {
        stop();
        setDataSource(url);
        resetSurfaceView();
    }

    public void resetSurfaceView() {
        this.setVisibility(SurfaceView.GONE);
        this.setVisibility(SurfaceView.VISIBLE);
    }

    public void start() {
        if (mIjkPlayer != null) { //已经初始化
            if (mIjkPlayer.isPlaying()) return;
            if (mManualPause) { //手动点击暂停
                mIjkPlayer.start();
            } else { //第一次播放
                mIjkPlayer.prepareAsync();
            }
            resetSurfaceView();
            mManualPause = false;
        } else {
            setDataSource(mCurrUrl);
            initIjkMediaPlayerListener();
            initSurfaceView();
            resetSurfaceView();
            mIjkPlayer.prepareAsync();
            mManualStop = false;
        }
    }

    public void pause() {
        if (mIjkPlayer != null) {
            mIjkPlayer.pause();
            mManualPause = true;
            mHandler.removeCallbacks(progressUpdateRunnable);
        }
    }

    public void stop() {
        if (mIjkPlayer != null) {
            mIjkPlayer.stop();
            mIjkPlayer.reset();
            mIjkPlayer = null;
            mManualStop = true;
            mHandler.removeCallbacks(progressUpdateRunnable);
        }
    }

    public boolean isPlaying() {
        if (mIjkPlayer != null) {
            return mIjkPlayer.isPlaying();
        }
        return false;
    }

    public void setDataSource(String url) {
        try {
            if (mIjkPlayer == null) initIjkMediaPlayer();
            mIjkPlayer.setDataSource(url);
            mCurrUrl = url;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHostResume() {
        Log.i(TAG, "onHostResume");
        if (!mManualPause) {
            Log.i(TAG, "exec start");
            mIjkPlayer.start();
            mHandler.post(progressUpdateRunnable);
        }
    }

    @Override
    public void onHostPause() {
        Log.i(TAG, "onHostPause");
        mIjkPlayer.pause();
        mHandler.removeCallbacks(progressUpdateRunnable);
    }

    @Override
    public void onHostDestroy() {
        Log.i(TAG, "onHostDestroy");
        mIjkPlayer.stop();
        mIjkPlayer.release();
        mHandler.removeCallbacks(progressUpdateRunnable);
    }
}
