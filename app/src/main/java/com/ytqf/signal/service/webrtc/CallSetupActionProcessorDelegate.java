package com.ytqf.signal.service.webrtc;

import androidx.annotation.NonNull;

import org.signal.ringrtc.CallException;
import org.signal.ringrtc.CallManager;
import com.ytqf.signal.events.WebRtcViewModel;
import com.ytqf.signal.logging.Log;
import com.ytqf.signal.ringrtc.CallState;
import com.ytqf.signal.ringrtc.Camera;
import com.ytqf.signal.ringrtc.RemotePeer;
import com.ytqf.signal.service.webrtc.state.WebRtcServiceState;
import com.ytqf.signal.webrtc.locks.LockManager;

import static com.ytqf.signal.webrtc.CallNotificationBuilder.TYPE_ESTABLISHED;

/**
 * Encapsulates the shared logic to setup a 1:1 call. Setup primarily includes retrieving turn servers and
 * transitioning to the connected state. Other action processors delegate the appropriate action to it but it is
 * not intended to be the main processor for the system.
 */
public class CallSetupActionProcessorDelegate extends WebRtcActionProcessor {

  public CallSetupActionProcessorDelegate(@NonNull WebRtcInteractor webRtcInteractor, @NonNull String tag) {
    super(webRtcInteractor, tag);
  }

  @Override
  public @NonNull WebRtcServiceState handleCallConnected(@NonNull WebRtcServiceState currentState, @NonNull RemotePeer remotePeer) {
    if (!remotePeer.callIdEquals(currentState.getCallInfoState().getActivePeer())) {
      Log.w(tag, "handleCallConnected(): Ignoring for inactive call.");
      return currentState;
    }

    Log.i(tag, "handleCallConnected(): call_id: " + remotePeer.getCallId());

    RemotePeer activePeer = currentState.getCallInfoState().requireActivePeer();

    webRtcInteractor.startAudioCommunication(activePeer.getState() == CallState.REMOTE_RINGING);
    webRtcInteractor.setWantsBluetoothConnection(true);

    activePeer.connected();

    if (currentState.getLocalDeviceState().getCameraState().isEnabled()) {
      webRtcInteractor.updatePhoneState(LockManager.PhoneState.IN_VIDEO);
    } else {
      webRtcInteractor.updatePhoneState(WebRtcUtil.getInCallPhoneState(context));
    }

    currentState = currentState.builder()
                               .actionProcessor(new ConnectedCallActionProcessor(webRtcInteractor))
                               .changeCallInfoState()
                               .callState(WebRtcViewModel.State.CALL_CONNECTED)
                               .callConnectedTime(System.currentTimeMillis())
                               .build();

    webRtcInteractor.unregisterPowerButtonReceiver();
    webRtcInteractor.setCallInProgressNotification(TYPE_ESTABLISHED, activePeer);

    try {
      CallManager callManager = webRtcInteractor.getCallManager();
      callManager.setCommunicationMode();
      callManager.setAudioEnable(currentState.getLocalDeviceState().isMicrophoneEnabled());
      callManager.setVideoEnable(currentState.getLocalDeviceState().getCameraState().isEnabled());
    } catch (CallException e) {
      return callFailure(currentState, "Enabling audio/video failed: ", e);
    }

    if (currentState.getCallSetupState().isAcceptWithVideo()) {
      currentState = currentState.getActionProcessor().handleSetEnableVideo(currentState, true);
    }

    return currentState;
  }

  @Override
  protected @NonNull WebRtcServiceState handleSetEnableVideo(@NonNull WebRtcServiceState currentState, boolean enable) {
    Camera camera = currentState.getVideoState().requireCamera();

    if (camera.isInitialized()) {
      camera.setEnabled(enable);
    }

    currentState = currentState.builder()
                               .changeCallSetupState()
                               .enableVideoOnCreate(enable)
                               .commit()
                               .changeLocalDeviceState()
                               .cameraState(camera.getCameraState())
                               .build();

    WebRtcUtil.enableSpeakerPhoneIfNeeded(context, currentState.getCallSetupState().isEnableVideoOnCreate());

    return currentState;
  }
}
