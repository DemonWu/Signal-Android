package com.ytqf.signal.groups.ui;

import androidx.annotation.NonNull;

import com.ytqf.signal.recipients.Recipient;

public interface RecipientClickListener {
  void onClick(@NonNull Recipient recipient);
}
