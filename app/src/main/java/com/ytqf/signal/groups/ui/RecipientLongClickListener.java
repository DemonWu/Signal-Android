package com.ytqf.signal.groups.ui;

import androidx.annotation.NonNull;

import com.ytqf.signal.recipients.Recipient;

public interface RecipientLongClickListener {
  boolean onLongClick(@NonNull Recipient recipient);
}
