package com.ytqf.signal.groups.ui.addtogroup;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ytqf.signal.dependencies.ApplicationDependencies;
import com.ytqf.signal.groups.GroupChangeException;
import com.ytqf.signal.groups.GroupId;
import com.ytqf.signal.groups.GroupManager;
import com.ytqf.signal.groups.MembershipNotSuitableForV2Exception;
import com.ytqf.signal.groups.ui.GroupChangeErrorCallback;
import com.ytqf.signal.groups.ui.GroupChangeFailureReason;
import com.ytqf.signal.logging.Log;
import com.ytqf.signal.recipients.Recipient;
import com.ytqf.signal.recipients.RecipientId;
import com.ytqf.signal.util.concurrent.SignalExecutors;

import java.io.IOException;
import java.util.Collections;

final class AddToGroupRepository {

  private static final String TAG = Log.tag(AddToGroupRepository.class);

  private final Context context;

  AddToGroupRepository() {
    this.context = ApplicationDependencies.getApplication();
  }

  public void add(@NonNull RecipientId recipientId,
                  @NonNull Recipient groupRecipient,
                  @NonNull GroupChangeErrorCallback error,
                  @NonNull Runnable success)
  {
    SignalExecutors.UNBOUNDED.execute(() -> {
      try {
        GroupId.Push pushGroupId = groupRecipient.requireGroupId().requirePush();

        GroupManager.addMembers(context, pushGroupId, Collections.singletonList(recipientId));

        success.run();
        } catch (GroupChangeException | MembershipNotSuitableForV2Exception | IOException e) {
        Log.w(TAG, e);
        error.onError(GroupChangeFailureReason.fromException(e));
      }
    });
  }
}
