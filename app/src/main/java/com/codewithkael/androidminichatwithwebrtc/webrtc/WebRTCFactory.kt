package com.codewithkael.androidminichatwithwebrtc.webrtc

import android.app.Application
import android.content.Context
import com.codewithkael.androidminichatwithwebrtc.utils.SharedPrefHelper
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.IceServer
import org.webrtc.PeerConnectionFactory
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebRTCFactory @Inject constructor(
    private val application: Application, prefHelper: SharedPrefHelper
) {
    private val peerConnectionFactory by lazy { createPeerConnectionFactory() }
    private val eglBaseContext = EglBase.create().eglBaseContext

    private val iceServer = listOf(
        IceServer.builder("stun:stun.relay.metered.ca:80").createIceServer(),
        IceServer.builder("turn:159.223.175.154:3478").setUsername("user")
            .setPassword("password").createIceServer(),
        IceServer.builder("turn:95.217.13.89:3478").setUsername("user")
            .setPassword("password").createIceServer()
    )
    private var videoCapture: CameraVideoCapturer? = null

    private val localVideoSource by lazy { peerConnectionFactory.createVideoSource(false) }
    private val localAudioSource by lazy { peerConnectionFactory.createAudioSource(MediaConstraints()) }

    private val streamId = "${prefHelper.getUserId()}_stream"
    private var localAudioTrack: AudioTrack? = null
    private var localVideoTrack: VideoTrack? = null
    private var localStream: MediaStream? = null

    init {
        initPeerConnectionFactory(application)
    }

    fun prepareLocalStream(
        view: SurfaceViewRenderer,
    ) {
        initSurfaceView(view)
        startLocalVideo(view)
    }

    fun initSurfaceView(view: SurfaceViewRenderer) {
        view.run {
            setMirror(false)
            setEnableHardwareScaler(true)
            init(eglBaseContext, null)
        }
    }

    private fun initPeerConnectionFactory(application: Context) {
        val options = PeerConnectionFactory.InitializationOptions.builder(application)
            .setEnableInternalTracer(true).setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)
    }

    private fun createPeerConnectionFactory(): PeerConnectionFactory {
        return PeerConnectionFactory.builder().setVideoDecoderFactory(
            DefaultVideoDecoderFactory(eglBaseContext)
        ).setVideoEncoderFactory(
            DefaultVideoEncoderFactory(
                eglBaseContext, true, true
            )
        ).setOptions(PeerConnectionFactory.Options().apply {
            disableEncryption = false
            disableNetworkMonitor = false
        }).createPeerConnectionFactory()
    }

    private fun startLocalVideo(surface: SurfaceViewRenderer) {
        val surfaceTextureHelper =
            SurfaceTextureHelper.create(Thread.currentThread().name, eglBaseContext)
        videoCapture = getVideoCapture()
        videoCapture?.initialize(
            surfaceTextureHelper, surface.context, localVideoSource.capturerObserver
        )
        videoCapture?.startCapture(720, 480, 10)
        localVideoTrack =
            peerConnectionFactory.createVideoTrack(streamId + "_video", localVideoSource)
        localVideoTrack?.addSink(surface)
        localAudioTrack =
            peerConnectionFactory.createAudioTrack(streamId + "_audio", localAudioSource)
        localStream = peerConnectionFactory.createLocalMediaStream(streamId)
        localStream?.addTrack(localAudioTrack)
        localStream?.addTrack(localVideoTrack)
    }

    private fun getVideoCapture(): CameraVideoCapturer {
        return Camera2Enumerator(application).run {
            deviceNames.find {
                isFrontFacing(it)
            }?.let {
                createCapturer(it, null)
            } ?: throw IllegalStateException()
        }
    }


    fun onDestroy() {
        runCatching {
            // Stop capturing video
            videoCapture?.stopCapture()
            videoCapture?.dispose()

            // Mute and dispose of the audio track
            localAudioTrack?.let {
                it.setEnabled(false) // Disable the track to stop mic input
                it.dispose()
            }

            // Dispose of the video track
            localVideoTrack?.dispose()

            // Dispose of the local media stream
            localStream?.dispose()
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun createRTCClient(
        observer: PeerConnection.Observer, listener: RTCClientImpl.TransferDataToServerCallback
    ): RTCClient? {
        val connection = peerConnectionFactory.createPeerConnection(
            PeerConnection.RTCConfiguration(iceServer), observer
        )
        localStream?.let {
            connection?.addStream(localStream)
        }
        return connection?.let { RTCClientImpl(it, listener) }
    }

    fun switchCamera() {
        videoCapture?.switchCamera(null)
    }

    fun toggleMic(enabled: Boolean) {
        localAudioTrack?.setEnabled(enabled)
    }

    fun toggleCamera(enabled: Boolean) {
        localVideoTrack?.setEnabled(enabled)
    }
}