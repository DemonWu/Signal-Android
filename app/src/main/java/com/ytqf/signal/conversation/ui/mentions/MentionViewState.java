package com.ytqf.signal.conversation.ui.mentions;

import androidx.annotation.NonNull;

import com.ytqf.signal.recipients.Recipient;
import com.ytqf.signal.util.viewholders.RecipientMappingModel;

public final class MentionViewState extends RecipientMappingModel<MentionViewState> {

  private final Recipient recipient;

  public MentionViewState(@NonNull Recipient recipient) {
    this.recipient = recipient;
  }

  @Override
  public @NonNull Recipient getRecipient() {
    return recipient;
  }
}
