package com.easy.ijkplayer;

import android.util.Log;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import javax.annotation.Nonnull;

public class RNEasyIjkplayerModule extends ReactContextBaseJavaModule {

    public RNEasyIjkplayerModule(ReactApplicationContext reactContext){
        super(reactContext);
    }

    @Nonnull
    @Override
    public String getName() {
        return "RNEasyIjkplayer";
    }

    /**
     * 获取视频时长
     * @param reactTag
     * @param callback
     */
    @ReactMethod
    public void getDuration(int reactTag, Callback callback){
        Log.i("IJKModule",""+RNEasyIjkplayerView.mDuration);
        int  duration = (int) RNEasyIjkplayerView.mDuration;
        Log.i("IJKModule",""+duration);
        if(duration != 0 ){
            callback.invoke(false,duration);
        }else{
            callback.invoke(true,duration);
        }
    }

    /**
     * 获取视频的像素尺寸
     * @param reactTag
     * @param callback
     */
    @ReactMethod
    public void getSize(int reactTag, Callback callback){
        WritableMap size = Arguments.createMap();
        size.putInt("width",0);
        size.putInt("height",0);
        if(RNEasyIjkplayerView.size.getInt("width")>0 && RNEasyIjkplayerView.size.getInt("height")>0){
            size.putInt("width",RNEasyIjkplayerView.size.getInt("width"));
            size.putInt("height",RNEasyIjkplayerView.size.getInt("height"));
            callback.invoke(false,size);
        }else{
            callback.invoke(true,size);
        }
    }
}