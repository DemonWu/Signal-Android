package com.ytqf.signal.groups.ui.invitesandrequests.requesting;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ytqf.signal.groups.GroupChangeException;
import com.ytqf.signal.groups.GroupId;
import com.ytqf.signal.groups.GroupManager;
import com.ytqf.signal.groups.ui.GroupChangeFailureReason;
import com.ytqf.signal.logging.Log;
import com.ytqf.signal.recipients.RecipientId;
import com.ytqf.signal.util.AsynchronousCallback;
import com.ytqf.signal.util.concurrent.SignalExecutors;

import java.io.IOException;
import java.util.Collection;

/**
 * Repository for modifying the requesting members on a single group.
 */
final class RequestingMemberRepository {

  private static final String TAG = Log.tag(RequestingMemberRepository.class);

  private final Context    context;
  private final GroupId.V2 groupId;

  RequestingMemberRepository(@NonNull Context context, @NonNull GroupId.V2 groupId) {
    this.context = context.getApplicationContext();
    this.groupId = groupId;
  }

  void approveRequests(@NonNull Collection<RecipientId> recipientIds,
                       @NonNull AsynchronousCallback.WorkerThread<Void, GroupChangeFailureReason> callback)
  {
    SignalExecutors.UNBOUNDED.execute(() -> {
      try {
        GroupManager.approveRequests(context, groupId, recipientIds);
        callback.onComplete(null);
      } catch (GroupChangeException | IOException e) {
        Log.w(TAG, e);
        callback.onError(GroupChangeFailureReason.fromException(e));
      }
    });
  }

  void denyRequests(@NonNull Collection<RecipientId> recipientIds,
                    @NonNull AsynchronousCallback.WorkerThread<Void, GroupChangeFailureReason> callback)
  {
    SignalExecutors.UNBOUNDED.execute(() -> {
      try {
        GroupManager.denyRequests(context, groupId, recipientIds);
        callback.onComplete(null);
      } catch (GroupChangeException | IOException e) {
        Log.w(TAG, e);
        callback.onError(GroupChangeFailureReason.fromException(e));
      }
    });
  }
}
