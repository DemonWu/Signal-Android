package com.ytqf.signal.mms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ytqf.signal.attachments.Attachment;
import com.ytqf.signal.contactshare.Contact;
import com.ytqf.signal.database.model.Mention;
import com.ytqf.signal.linkpreview.LinkPreview;
import com.ytqf.signal.recipients.Recipient;

import java.util.Collections;
import java.util.List;

public class OutgoingSecureMediaMessage extends OutgoingMediaMessage {

  public OutgoingSecureMediaMessage(Recipient recipient, String body,
                                    List<Attachment> attachments,
                                    long sentTimeMillis,
                                    int distributionType,
                                    long expiresIn,
                                    boolean viewOnce,
                                    @Nullable QuoteModel quote,
                                    @NonNull List<Contact> contacts,
                                    @NonNull List<LinkPreview> previews,
                                    @NonNull List<Mention> mentions)
  {
    super(recipient, body, attachments, sentTimeMillis, -1, expiresIn, viewOnce, distributionType, quote, contacts, previews, mentions, Collections.emptyList(), Collections.emptyList());
  }

  public OutgoingSecureMediaMessage(OutgoingMediaMessage base) {
    super(base);
  }

  @Override
  public boolean isSecure() {
    return true;
  }
}
