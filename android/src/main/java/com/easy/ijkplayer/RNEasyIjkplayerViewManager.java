package com.easy.ijkplayer;

import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class RNEasyIjkplayerViewManager extends SimpleViewManager<RNEasyIjkplayerView> {
    private static final String TAG = "RNEasyIjkplayerViewManager";
    private final String REACT_CLASS = "RNEasyIjkplayerView";
    private static final int COMMAND_PAUSE_ID = 1;
    private static final String COMMAND_PAUSE_NAME = "pause";
    private static final int COMMAND_PLAY_ID = 2;
    private static final String COMMAND_PLAY_NAME = "play";
    private static final int COMMAND_STOP_ID = 3;
    private static final String COMMAND_STOP_NAME = "stop";
    private static final int COMMAND_SEEK_TO_ID = 4;
    private static final String COMMAND_SEEK_TO_NAME = "seekTo";

    @Nonnull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Nonnull
    @Override
    protected RNEasyIjkplayerView createViewInstance(@Nonnull ThemedReactContext reactContext) {
        RNEasyIjkplayerView ijkPlayer = new RNEasyIjkplayerView(reactContext);
        return ijkPlayer;
    }

//    @ReactProp(name = "url")
//    public void setUrl(RNEasyIjkplayerView ijkPlayer, String url) {
//        Log.i(TAG, "url:" + url);
//        if (ijkPlayer.isPlaying()) {
//            ijkPlayer.restart(url);
//        } else {
//            if (!url.equals("")) {
//                ijkPlayer.setDataSource(url);
//            }
//        }
//    }

    @ReactProp(name = "options")
    public void setOptions(RNEasyIjkplayerView ijkPlayer, ReadableMap options) {
        /* auto start */
        int autoPlay = 0;
        if(options.hasKey("autoPlay")){
            autoPlay = options.getInt("autoPlay");
            Log.i(TAG,"autoPlay::"+autoPlay);
            if(autoPlay == 1){
                ijkPlayer.setMAutoPlay(1);
            }
        }
        /* url */
        if(options.hasKey("url")){
            String url = options.getString("url");
            Log.i(TAG,url);
            if (ijkPlayer.isPlaying()) {
                Log.i(TAG,"isPlaying");
                ijkPlayer.restart(url);
            } else {
                if (!url.equals("")) {
                    ijkPlayer.setDataSource(url);
                    if(autoPlay == 1){
                        ijkPlayer.start();
                    }
                }
            }
        }
    }


    @javax.annotation.Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                COMMAND_PAUSE_NAME, COMMAND_PAUSE_ID,
                COMMAND_PLAY_NAME, COMMAND_PLAY_ID,
                COMMAND_STOP_NAME, COMMAND_STOP_ID,
                COMMAND_SEEK_TO_NAME, COMMAND_SEEK_TO_ID
        );
    }

    @Override
    public void receiveCommand(@Nonnull RNEasyIjkplayerView root, int commandId, @javax.annotation.Nullable ReadableArray args) {
        switch (commandId) {
            case COMMAND_PAUSE_ID:
                root.pause();
                break;
            case COMMAND_PLAY_ID:
                root.start();
                break;
            case COMMAND_STOP_ID:
                root.stop();
                break;
            case COMMAND_SEEK_TO_ID:
                int progress = args.getInt(0);
                Log.i(TAG, "seek Progress:" + progress);
                root.seekTo(progress * 1000);
                break;
            default:
                break;
        }

    }

    @Nullable
    @Override
    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put(
                        "onComplete",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onComplete")))
                .put(
                        "onInfo",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onInfo")))
                .put(
                        "onError",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onError")))
                .put(
                        "onPrepared",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onPrepared")))
                .put(
                        "onProgressUpdate",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onProgressUpdate")))
                .build();
    }

}
