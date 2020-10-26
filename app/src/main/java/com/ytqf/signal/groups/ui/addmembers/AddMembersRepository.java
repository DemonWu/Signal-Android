package com.ytqf.signal.groups.ui.addmembers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.ytqf.signal.contacts.SelectedContact;
import com.ytqf.signal.dependencies.ApplicationDependencies;
import com.ytqf.signal.recipients.RecipientId;
import com.ytqf.signal.util.concurrent.SignalExecutors;

class AddMembersRepository {

  private final Context context;

  AddMembersRepository() {
    this.context = ApplicationDependencies.getApplication();
  }

  void getOrCreateRecipientId(@NonNull SelectedContact selectedContact, @NonNull Consumer<RecipientId> consumer) {
    SignalExecutors.BOUNDED.execute(() -> consumer.accept(selectedContact.getOrCreateRecipientId(context)));
  }
}
