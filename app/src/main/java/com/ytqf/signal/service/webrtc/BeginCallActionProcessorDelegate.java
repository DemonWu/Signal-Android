package com.ytqf.signal.service.webrtc;

import android.media.AudioManager;

import androidx.annotation.NonNull;

import org.signal.ringrtc.CallException;
import org.signal.ringrtc.CallManager;
import com.ytqf.signal.components.webrtc.BroadcastVideoSink;
import com.ytqf.signal.events.CallParticipant;
import com.ytqf.signal.events.WebRtcViewModel;
import com.ytqf.signal.logging.Log;
import com.ytqf.signal.ringrtc.RemotePeer;
import com.ytqf.signal.service.webrtc.state.WebRtcServiceState;
import com.ytqf.signal.util.ServiceUtil;
import org.whispersystems.signalservice.api.messages.calls.OfferMessage;

import static com.ytqf.signal.webrtc.CallNotificationBuilder.TYPE_INCOMING_CONNECTING;

/**
 * Encapsulates the logic to begin a 1:1 call from scratch. Other action processors
 * delegate the appropriate action to it but it is not intended to be the main processor for the system.
 */
public class BeginCallActionProcessorDelegate extends WebRtcActionProcessor {

  public BeginCallActionProcessorDelegate(@NonNull WebRtcInteractor webRtcInteractor, @NonNull String tag) {
    super(webRtcInteractor, tag);
  }

  @Override
  protected @NonNull WebRtcServiceState handleOutgoingCall(@NonNull WebRtcServiceState currentState,
                                                           @NonNull RemotePeer remotePeer,
                                                           @NonNull OfferMessage.Type offerType)
  {
    remotePeer.setCallStartTimestamp(System.currentTimeMillis());
    currentState = currentState.builder()
                               .actionProcessor(new OutgoingCallActionProcessor(webRtcInteractor))
                               .changeCallInfoState()
                               .callRecipient(remotePeer.getRecipient())
                               .callState(WebRtcViewModel.State.CALL_OUTGOING)
                               .putRemotePeer(remotePeer)
                               .putParticipant(remotePeer.getRecipient(),
                                               CallParticipant.createRemote(
                                                       remotePeer.getRecipient(),
                                                       null,
                                                       new BroadcastVideoSink(currentState.getVideoState().getEglBase()),
                                                       false
                                               ))
                               .build();

    CallManager.CallMediaType callMediaType = WebRtcUtil.getCallMediaTypeFromOfferType(offerType);

    try {
      webRtcInteractor.getCallManager().call(remotePeer, callMediaType, 1);
    } catch (CallException e) {
      return callFailure(currentState, "Unable to create outgoing call: ", e);
    }

    return currentState;
  }

  @Override
  protected @NonNull WebRtcServiceState handleStartIncomingCall(@NonNull WebRtcServiceState currentState, @NonNull RemotePeer remotePeer) {
    remotePeer.answering();

    Log.i(tag, "assign activePeer callId: " + remotePeer.getCallId() + " key: " + remotePeer.hashCode());

    AudioManager androidAudioManager = ServiceUtil.getAudioManager(context);
    androidAudioManager.setSpeakerphoneOn(false);

    webRtcInteractor.setCallInProgressNotification(TYPE_INCOMING_CONNECTING, remotePeer);
    webRtcInteractor.retrieveTurnServers(remotePeer);

    return currentState.builder()
                       .actionProcessor(new IncomingCallActionProcessor(webRtcInteractor))
                       .changeCallInfoState()
                       .callRecipient(remotePeer.getRecipient())
                       .activePeer(remotePeer)
                       .callState(WebRtcViewModel.State.CALL_INCOMING)
                       .putParticipant(remotePeer.getRecipient(),
                                       CallParticipant.createRemote(
                                               remotePeer.getRecipient(),
                                               null,
                                               new BroadcastVideoSink(currentState.getVideoState().getEglBase()),
                                               false
                                       ))
                       .build();
  }
}
