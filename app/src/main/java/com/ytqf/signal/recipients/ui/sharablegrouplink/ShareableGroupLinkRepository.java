package com.ytqf.signal.recipients.ui.sharablegrouplink;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.signal.storageservice.protos.groups.AccessControl;
import com.ytqf.signal.database.DatabaseFactory;
import com.ytqf.signal.groups.GroupChangeBusyException;
import com.ytqf.signal.groups.GroupChangeFailedException;
import com.ytqf.signal.groups.GroupId;
import com.ytqf.signal.groups.GroupInsufficientRightsException;
import com.ytqf.signal.groups.GroupManager;
import com.ytqf.signal.groups.GroupNotAMemberException;
import com.ytqf.signal.groups.ui.GroupChangeFailureReason;
import com.ytqf.signal.util.AsynchronousCallback;
import com.ytqf.signal.util.concurrent.SignalExecutors;

import java.io.IOException;

final class ShareableGroupLinkRepository {

  private final Context    context;
  private final GroupId.V2 groupId;

  ShareableGroupLinkRepository(@NonNull Context context, @NonNull GroupId.V2 groupId) {
    this.context = context;
    this.groupId = groupId;
  }

  void cycleGroupLinkPassword(@NonNull AsynchronousCallback.WorkerThread<Void, GroupChangeFailureReason> callback) {
    SignalExecutors.UNBOUNDED.execute(() -> {
      try {
        GroupManager.cycleGroupLinkPassword(context, groupId);
        callback.onComplete(null);
      } catch (GroupNotAMemberException | GroupChangeFailedException | GroupInsufficientRightsException | IOException | GroupChangeBusyException e) {
        callback.onError(GroupChangeFailureReason.fromException(e));
      }
    });
  }

  void toggleGroupLinkEnabled(@NonNull AsynchronousCallback.WorkerThread<Void, GroupChangeFailureReason> callback) {
    setGroupLinkEnabledState(toggleGroupLinkState(true, false), callback);
  }

  void toggleGroupLinkApprovalRequired(@NonNull AsynchronousCallback.WorkerThread<Void, GroupChangeFailureReason> callback) {
    setGroupLinkEnabledState(toggleGroupLinkState(false, true), callback);
  }

  private void setGroupLinkEnabledState(@NonNull GroupManager.GroupLinkState state,
                                        @NonNull AsynchronousCallback.WorkerThread<Void, GroupChangeFailureReason> callback)
  {
    SignalExecutors.UNBOUNDED.execute(() -> {
      try {
        GroupManager.setGroupLinkEnabledState(context, groupId, state);
        callback.onComplete(null);
      } catch (GroupNotAMemberException | GroupChangeFailedException | GroupInsufficientRightsException | IOException | GroupChangeBusyException e) {
        callback.onError(GroupChangeFailureReason.fromException(e));
      }
    });
  }

  @WorkerThread
  private GroupManager.GroupLinkState toggleGroupLinkState(boolean toggleEnabled, boolean toggleApprovalNeeded) {
    AccessControl.AccessRequired currentState = DatabaseFactory.getGroupDatabase(context)
                                                               .getGroup(groupId)
                                                               .get()
                                                               .requireV2GroupProperties()
                                                               .getDecryptedGroup()
                                                               .getAccessControl()
                                                               .getAddFromInviteLink();

    boolean enabled;
    boolean approvalNeeded;

    switch (currentState) {
      case UNKNOWN:
      case UNSATISFIABLE:
      case UNRECOGNIZED:
      case MEMBER:
        enabled        = false;
        approvalNeeded = false;
        break;
      case ANY:
        enabled        = true;
        approvalNeeded = false;
        break;
      case ADMINISTRATOR:
        enabled        = true;
        approvalNeeded = true;
        break;
      default: throw new AssertionError();
    }

    if (toggleApprovalNeeded) {
      approvalNeeded = !approvalNeeded;
    }

    if (toggleEnabled) {
      enabled = !enabled;
    }

    if (approvalNeeded && enabled) {
      return GroupManager.GroupLinkState.ENABLED_WITH_APPROVAL;
    } else {
      if (enabled) {
        return GroupManager.GroupLinkState.ENABLED;
      }
    }

    return GroupManager.GroupLinkState.DISABLED;
  }
}
