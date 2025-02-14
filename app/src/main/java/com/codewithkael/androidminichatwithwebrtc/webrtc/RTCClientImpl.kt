package com.codewithkael.androidminichatwithwebrtc.webrtc

import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription

class RTCClientImpl(
    connection: PeerConnection,
    private val transferListener: TransferDataToServerCallback
) : RTCClient {

    private val mediaConstraint = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
    }

    override val peerConnection: PeerConnection = connection

    override fun offer() {
        peerConnection.createOffer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                peerConnection.setLocalDescription(object : MySdpObserver() {}, desc)
                desc?.let {
                    transferListener.onOfferGenerated(desc)
                }
            }
        }, mediaConstraint)
    }

    override fun answer() {
        peerConnection.createAnswer(object : MySdpObserver() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                peerConnection.setLocalDescription(object : MySdpObserver() {
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        desc?.let { transferListener.onAnswerGenerated(it) }
                    }
                }, desc)
            }
        }, mediaConstraint)
    }


    override fun onRemoteSessionReceived(sessionDescription: SessionDescription) {
        peerConnection.setRemoteDescription(MySdpObserver(), sessionDescription)
    }

    override fun onIceCandidateReceived(iceCandidate: IceCandidate) {
        peerConnection.addIceCandidate(iceCandidate)
    }


    override fun onDestroy() {
        runCatching {
            peerConnection.close()
        }
    }


    override fun onLocalIceCandidateGenerated(iceCandidate: IceCandidate) {
        peerConnection.addIceCandidate(iceCandidate)
        transferListener.onIceGenerated(iceCandidate)
    }

    interface TransferDataToServerCallback {
        fun onIceGenerated(iceCandidate: IceCandidate)
        fun onOfferGenerated(sessionDescription: SessionDescription)
        fun onAnswerGenerated(sessionDescription: SessionDescription)
    }
}