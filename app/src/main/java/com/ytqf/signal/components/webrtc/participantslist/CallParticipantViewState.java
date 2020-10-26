package com.ytqf.signal.components.webrtc.participantslist;

import androidx.annotation.NonNull;

import com.ytqf.signal.events.CallParticipant;
import com.ytqf.signal.recipients.Recipient;
import com.ytqf.signal.util.viewholders.RecipientMappingModel;

public final class CallParticipantViewState extends RecipientMappingModel<CallParticipantViewState> {

  private final CallParticipant callParticipant;

  CallParticipantViewState(@NonNull CallParticipant callParticipant) {
    this.callParticipant = callParticipant;
  }

  @Override
  public @NonNull Recipient getRecipient() {
    return callParticipant.getRecipient();
  }
}
