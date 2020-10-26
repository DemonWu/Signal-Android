package com.ytqf.signal.recipients.ui.bottomsheet;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.ytqf.signal.contacts.sync.DirectoryHelper;
import com.ytqf.signal.database.DatabaseFactory;
import com.ytqf.signal.database.GroupDatabase;
import com.ytqf.signal.database.IdentityDatabase;
import com.ytqf.signal.groups.GroupChangeException;
import com.ytqf.signal.groups.GroupId;
import com.ytqf.signal.groups.GroupManager;
import com.ytqf.signal.groups.ui.GroupChangeErrorCallback;
import com.ytqf.signal.groups.ui.GroupChangeFailureReason;
import com.ytqf.signal.logging.Log;
import com.ytqf.signal.recipients.Recipient;
import com.ytqf.signal.recipients.RecipientId;
import com.ytqf.signal.util.concurrent.SignalExecutors;
import com.ytqf.signal.util.concurrent.SimpleTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class RecipientDialogRepository {

  private static final String TAG = Log.tag(RecipientDialogRepository.class);

  @NonNull  private final Context     context;
  @NonNull  private final RecipientId recipientId;
  @Nullable private final GroupId     groupId;

  RecipientDialogRepository(@NonNull Context context,
                            @NonNull RecipientId recipientId,
                            @Nullable GroupId groupId)
  {
    this.context     = context;
    this.recipientId = recipientId;
    this.groupId     = groupId;
  }

  @NonNull RecipientId getRecipientId() {
    return recipientId;
  }

  @Nullable GroupId getGroupId() {
    return groupId;
  }

  void getIdentity(@NonNull Consumer<IdentityDatabase.IdentityRecord> callback) {
    SignalExecutors.BOUNDED.execute(
      () -> callback.accept(DatabaseFactory.getIdentityDatabase(context)
                                           .getIdentity(recipientId)
                                           .orNull()));
  }

  void getRecipient(@NonNull RecipientCallback recipientCallback) {
    SimpleTask.run(SignalExecutors.BOUNDED,
                   () -> Recipient.resolved(recipientId),
                   recipientCallback::onRecipient);
  }

  void refreshRecipient() {
    SignalExecutors.UNBOUNDED.execute(() -> {
      try {
        DirectoryHelper.refreshDirectoryFor(context, Recipient.resolved(recipientId), false);
      } catch (IOException e) {
        Log.w(TAG, "Failed to refresh user after adding to contacts.");
      }
    });
  }

  void removeMember(@NonNull Consumer<Boolean> onComplete, @NonNull GroupChangeErrorCallback error) {
    SimpleTask.run(SignalExecutors.UNBOUNDED,
                   () -> {
                     try {
                       GroupManager.ejectFromGroup(context, Objects.requireNonNull(groupId).requireV2(), Recipient.resolved(recipientId));
                       return true;
                     } catch (GroupChangeException | IOException e) {
                       Log.w(TAG, e);
                       error.onError(GroupChangeFailureReason.fromException(e));
                     }
                     return false;
                   },
                   onComplete::accept);
  }

  void setMemberAdmin(boolean admin, @NonNull Consumer<Boolean> onComplete, @NonNull GroupChangeErrorCallback error) {
    SimpleTask.run(SignalExecutors.UNBOUNDED,
                   () -> {
                     try {
                       GroupManager.setMemberAdmin(context, Objects.requireNonNull(groupId).requireV2(), recipientId, admin);
                       return true;
                     } catch (GroupChangeException | IOException e) {
                       Log.w(TAG, e);
                       error.onError(GroupChangeFailureReason.fromException(e));
                     }
                     return false;
                   },
                   onComplete::accept);
  }

  void getGroupMembership(@NonNull Consumer<List<RecipientId>> onComplete) {
    SimpleTask.run(SignalExecutors.UNBOUNDED,
                   () -> {
                     GroupDatabase                   groupDatabase   = DatabaseFactory.getGroupDatabase(context);
                     List<GroupDatabase.GroupRecord> groupRecords    = groupDatabase.getPushGroupsContainingMember(recipientId);
                     ArrayList<RecipientId>          groupRecipients = new ArrayList<>(groupRecords.size());

                     for (GroupDatabase.GroupRecord groupRecord : groupRecords) {
                       groupRecipients.add(groupRecord.getRecipientId());
                     }

                     return groupRecipients;
                   },
                   onComplete::accept);
  }

  public void getActiveGroupCount(@NonNull Consumer<Integer> onComplete) {
    SignalExecutors.BOUNDED.execute(() -> onComplete.accept(DatabaseFactory.getGroupDatabase(context).getActiveGroupCount()));
  }

  interface RecipientCallback {
    void onRecipient(@NonNull Recipient recipient);
  }
}
