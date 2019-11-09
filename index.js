import React, { Component } from 'react'
import {
    View, Dimensions,
    StyleSheet, ActivityIndicator,
    requireNativeComponent,
    UIManager, findNodeHandle,
    NativeModules, NativeEventEmitter
} from "react-native"
import PropTypes from 'prop-types'

const defaultIndicatorSize = 16
const { width: D_WIDTH } = Dimensions.get('window')

class IJKPlayerView extends Component {
    static propTypes = {
        showIndicator: PropTypes.bool,
        options: PropTypes.object,
        onComplete: PropTypes.func,
        onPrepared: PropTypes.func,
        onError: PropTypes.func,
        onInfo: PropTypes.func,
        onProgressUpdate: PropTypes.func,
        onLoadProgressUpdate: PropTypes.func,
    }
    constructor(props) {
        super(props)
        this.state = {
            indicatorLeft: null,
            indicatorTop: null,
            showIndicator: false,
        }
    }

    static getDerivedStateFromProps(props, state) {
        const { style,showIndicator } = props
        let width, height
        if (style) {
            width = style.width
            height = style.height
        }
        let videoWrapperWidth = width || D_WIDTH
        let videoWrapperHeight = height || D_WIDTH * 0.7
        const indicatorLeft = videoWrapperWidth / 2 - defaultIndicatorSize / 2
        const indicatorTop = videoWrapperHeight / 2 - defaultIndicatorSize / 2
        if (
            indicatorLeft !== state.indicatorLeft
            || indicatorTop !== state.indicatorTop
            || showIndicator !== state.showIndicator
        ) {
            return {
                indicatorLeft,
                indicatorTop,
                videoWrapperWidth,
                videoWrapperHeight,
                showIndicator,
            }
        }
        return null
    }

    play = () => {
        console.log('play')
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this.ref),
            UIManager.getViewManagerConfig('IJKPlayer').Commands.play,
            null,
        )
    }

    pause = () => {
        console.log('pause')
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this.ref),
            UIManager.getViewManagerConfig('IJKPlayer').Commands.pause,
            null,
        )
    }

    stop = () => {
        console.log('pause')
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this.ref),
            UIManager.getViewManagerConfig('IJKPlayer').Commands.stop,
            null,
        )
    }

    seekTo = (time) => {
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this.ref),
            UIManager.getViewManagerConfig('IJKPlayer').Commands.seekTo,
            [time],
        )
    }

    /**
     *
     * @param callback
     */
    getDuration = (callback) => {
        NativeModules.RNEasyIjkplayer.getDuration(findNodeHandle(this.ref), callback)
    }

    getSize = (callback) => {
        NativeModules.RNEasyIjkplayer.getSize(findNodeHandle(this.ref), callback)
    }

    _onProgressUpdate = ({ nativeEvent: { progress } }) => {
        const { onProgressUpdate } = this.props
        onProgressUpdate && onProgressUpdate(progress)
    }

    _onPrepared = (event) => {
        console.log('on prepared')
        const { onPrepared } = this.props
        onPrepared && onPrepared(event)

        this.setState({ showIndicator: false })
        this.getSize((err, size) => {
            if (!err) {
                const { videoWrapperHeight, videoWrapperWidth } = this.state
                if (size.width <= size.height) { //宽度小于高度, 左右留黑边
                    let videoWidth = size.width / size.height * videoWrapperHeight
                    this.setState({
                        videoWidth,
                        videoHeight: videoWrapperHeight,
                        videoLeft: (videoWrapperWidth - videoWidth) / 2,
                    })
                } else { //宽度大于高度, 上下留黑边
                    let videoHeight = size.height / size.width * videoWrapperWidth
                    this.setState({
                        videoHeight,
                        videoWidth: videoWrapperWidth,
                        videoTop: (videoWrapperHeight - videoHeight) / 2,
                    })
                }
            }
        })
    }

    _onLoadProgressUpdate = ({ nativeEvent: { loadProgress } }) => {
        console.log('on loadProgressUpdate:', loadProgress)
        const { onLoadProgressUpdate } = this.props
        onLoadProgressUpdate && onLoadProgressUpdate(loadProgress)
    }

    _onInfo = ({ nativeEvent: { info } }) => {
        console.log('on Info:', info)
        const { onInfo } = this.props
        onInfo && onInfo(info)
    }

    _onError = ({ nativeEvent: { error } }) => {
        console.log('on error:', error)
        const { onError } = this.props
        onError && onError(error)
    }

    _onComplete = () => {
        const { onComplete } = this.props
        onComplete && onComplete()
    }

    render() {
        const {
            videoWrapperWidth, videoWrapperHeight, videoHeight, videoWidth, videoLeft, videoTop,
            indicatorLeft, indicatorTop, showIndicator
        } = this.state
        return <View style={{ ...styles.container, width: videoWrapperWidth, height: videoWrapperHeight }}>
            <IJKPlayer
                style={{
                    ...styles.video,
                    width: videoWidth,
                    height: videoHeight,
                    left: videoLeft ? videoLeft : 0,
                    top: videoTop ? videoTop : 0
                }}
                {...this.props}

                ref={ref => this.ref = ref}
                onPrepared={this._onPrepared}
                onProgressUpdate={this._onProgressUpdate}
                onLoadProgressUpdate={this._onLoadProgressUpdate}
                onInfo={this._onInfo}
                onError={this._onError}
                onComplete={this._onComplete}
            />
            <ActivityIndicator
                style={{ ...styles.indicator, left: indicatorLeft, top: indicatorTop }}
                size={'small'}
                animating={showIndicator}
            />
        </View>
    }
}

const styles = StyleSheet.create({
    container: {
        position: 'relative',
    },
    video: {
        position: 'absolute',
    },
    indicator: {
        position: 'absolute',
    },
})
var IJKPlayer = requireNativeComponent('RNEasyIjkplayerView', IJKPlayerView)

export default IJKPlayerView
