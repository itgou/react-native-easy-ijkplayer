
# react-native-easy-ijkplayer

## Getting started

`$ npm install react-native-easy-ijkplayer --save`

### Mostly automatic installation

`$ react-native link react-native-easy-ijkplayer`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-easy-ijkplayer` and add `RNEasyIjkplayer.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNEasyIjkplayer.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.easy.ijkplayer.RNEasyIjkplayerPackage;` to the imports at the top of the file
  - Add `new RNEasyIjkplayerPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-easy-ijkplayer'
  	project(':react-native-easy-ijkplayer').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-easy-ijkplayer/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-easy-ijkplayer')
  	```

### Demo Reposity: https://github.com/itgou/react-native-easy-ijkplayer-demo

### Extra Setting in Android
1. For resolving Error: minSdkVersion 16 cannot be smaller than version 21 declared in library [:react-native-easy-ijkplayer]

   Open up `android/build.gradle`  
    ```
    buildscript {
        ext {
            buildToolsVersion = "28.0.3"
    -        minSdkVersion = 16
    +        minSdkVersion = 21 
            compileSdkVersion = 28
            targetSdkVersion = 28
            supportLibVersion = "28.0.0"

    ```
2. For resolving Bug about  android:allowBackup

    Open up `android/app/src/main/AndroidManifest.xml`
    ```
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
    +        xmlns:tools="http://schemas.android.com/tools"
         package="com.reactnativeeasyiijkplayerdemo">
 
     <uses-permission android:name="android.permission.INTERNET" />
       android:icon="@mipmap/ic_launcher"
       android:roundIcon="@mipmap/ic_launcher_round"
       android:allowBackup="false"
    +  tools:replace="android:allowBackup"
       android:theme="@style/AppTheme">
       <activity
         android:name=".MainActivity"

    ```
3. For resolving Error: java.lang.UnsatisfiedLinkError:......couldn't find libffmpeg.so

    Open up `/android/app/build.gradle`
    ```
    android {
         targetSdkVersion rootProject.ext.targetSdkVersion
         versionCode 1
         versionName "1.0"
    +    ndk{
    +       abiFilters "armeabi-v7a", "x86"
    +    }
     }
     splits {
         abi {

    ```

## Usage
```javascript
import RNEasyIjkplayer from 'react-native-easy-ijkplayer';
import React, {Component} from 'react'
import {Platform, StyleSheet, Text, View, Dimensions, Button} from 'react-native'
import IJKPlayerView from "react-native-easy-ijkplayer"

const {width, height} = Dimensions.get('window')

export default class App extends Component {
    state={
        showIndicator:true
    }
    _play = () => {
        this.RNTIJKPlayerRef.play()
    }

    _pause = () => {
        this.RNTIJKPlayerRef.pause()
    }

    _stop = () => {
        this.RNTIJKPlayerRef.stop()
    }

    _seekTo =  () => {
        this.RNTIJKPlayerRef.seekTo(60)
    }
    _getDuration =  () => {
        this.RNTIJKPlayerRef.getDuration((err, duration) => {
            console.log(err)
            console.log(duration)
        })
    }

    _getSize = () => {
        this.RNTIJKPlayerRef.getSize((err, size) => {
            console.log(err)
            console.log(size)
        })
    }
    _onPrepared = (event) => {
        this.setState({showIndicator:false})
    }
    _onLoadProgressUpdate = ({nativeEvent: {loadProgress}}) => {
    }

    _onProgressUpdate = ( progress) => {
        console.log('progress',progress)
    }

    _onInfo = (info) => {
    }

    _onError = (error) => {
    }

    _onComplete = () => {
    }

    render() {
        const {showIndicator}= this.state
        return (
            <>
                <IJKPlayerView
                    ref={(ref) => this.RNTIJKPlayerRef = ref}
                    options={{
                        url:"http://img.elleshop.com.cn/media/product/14994134515891.mp4",
                        autoPlay: 1,
                    }}
                    showIndicator={showIndicator}
                    onComplete={this._onComplete}
                    onPrepared={this._onPrepared}
                    onError={this._onError}
                    onInfo={this._onInfo}
                    onProgressUpdate={this._onProgressUpdate}
                />
                <Button
                    onPress={this._play}
                    title={"start"}
                />
                <Button
                    onPress={this._pause}
                    title={"pause"}
                />
                <Button
                    onPress={this._stop}
                    title={"stop"}
                />
                <Button
                    onPress={this._seekTo}
                    title={"seek"}
                />
                <Button
                    onPress={this._getDuration}
                    title={"get Duration"}
                />
                <Button
                    onPress={this._getSize}
                    title={"get Size"}
                />
            </>
        )
    }
}

```
  