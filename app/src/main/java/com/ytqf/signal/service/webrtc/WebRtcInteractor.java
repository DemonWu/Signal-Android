package com.ytqf.signal.service.webrtc;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.ringrtc.CallManager;
import com.ytqf.signal.ringrtc.CameraEventListener;
import com.ytqf.signal.ringrtc.RemotePeer;
import com.ytqf.signal.service.WebRtcCallService;
import com.ytqf.signal.service.webrtc.state.WebRtcServiceState;
import com.ytqf.signal.webrtc.audio.BluetoothStateManager;
import com.ytqf.signal.webrtc.audio.OutgoingRinger;
import com.ytqf.signal.webrtc.audio.SignalAudioManager;
import com.ytqf.signal.webrtc.locks.LockManager;
import org.whispersystems.signalservice.api.messages.calls.SignalServiceCallMessage;

/**
 * Serves as the bridge between the action processing framework as the WebRTC service. Attempts
 * to minimize direct access to various managers by providing a simple proxy to them. Due to the
 * heavy use of {@link CallManager} throughout, it was exempted from the rule.
 */
public class WebRtcInteractor {

  @NonNull private final WebRtcCallService           webRtcCallService;
  @NonNull private final CallManager                 callManager;
  @NonNull private final LockManager                 lockManager;
  @NonNull private final SignalAudioManager          audioManager;
  @NonNull private final BluetoothStateManager       bluetoothStateManager;
  @NonNull private final CameraEventListener         cameraEventListener;

  public WebRtcInteractor(@NonNull WebRtcCallService webRtcCallService,
                          @NonNull CallManager callManager,
                          @NonNull LockManager lockManager,
                          @NonNull SignalAudioManager audioManager,
                          @NonNull BluetoothStateManager bluetoothStateManager,
                          @NonNull CameraEventListener cameraEventListener)
  {
    this.webRtcCallService     = webRtcCallService;
    this.callManager           = callManager;
    this.lockManager           = lockManager;
    this.audioManager          = audioManager;
    this.bluetoothStateManager = bluetoothStateManager;
    this.cameraEventListener   = cameraEventListener;
  }

  @NonNull CameraEventListener getCameraEventListener() {
    return cameraEventListener;
  }

  @NonNull CallManager getCallManager() {
    return callManager;
  }

  @NonNull WebRtcCallService getWebRtcCallService() {
    return webRtcCallService;
  }

  void setWantsBluetoothConnection(boolean enabled) {
    bluetoothStateManager.setWantsConnection(enabled);
  }

  void updatePhoneState(@NonNull LockManager.PhoneState phoneState) {
    lockManager.updatePhoneState(phoneState);
  }

  void sendMessage(@NonNull WebRtcServiceState state) {
    webRtcCallService.sendMessage(state);
  }

  void sendCallMessage(@NonNull RemotePeer remotePeer, @NonNull SignalServiceCallMessage callMessage) {
    webRtcCallService.sendCallMessage(remotePeer, callMessage);
  }

  void setCallInProgressNotification(int type, @NonNull RemotePeer remotePeer) {
    webRtcCallService.setCallInProgressNotification(type, remotePeer);
  }

  void retrieveTurnServers(@NonNull RemotePeer remotePeer) {
    webRtcCallService.retrieveTurnServers(remotePeer);
  }

  void stopForegroundService() {
    webRtcCallService.stopForeground(true);
  }

  void insertMissedCall(@NonNull RemotePeer remotePeer, boolean signal, long timestamp) {
    webRtcCallService.insertMissedCall(remotePeer, signal, timestamp);
  }

  void startWebRtcCallActivityIfPossible() {
    webRtcCallService.startCallCardActivityIfPossible();
  }

  void registerPowerButtonReceiver() {
    webRtcCallService.registerPowerButtonReceiver();
  }

  void unregisterPowerButtonReceiver() {
    webRtcCallService.unregisterPowerButtonReceiver();
  }

  void silenceIncomingRinger() {
    audioManager.silenceIncomingRinger();
  }

  void initializeAudioForCall() {
    audioManager.initializeAudioForCall();
  }

  void startIncomingRinger(@Nullable Uri ringtoneUri, boolean vibrate) {
    audioManager.startIncomingRinger(ringtoneUri, vibrate);
  }

  void startOutgoingRinger(@NonNull OutgoingRinger.Type type) {
    audioManager.startOutgoingRinger(type);
  }

  void stopAudio(boolean playDisconnect) {
    audioManager.stop(playDisconnect);
  }

  void startAudioCommunication(boolean preserveSpeakerphone) {
    audioManager.startCommunication(preserveSpeakerphone);
  }
}
