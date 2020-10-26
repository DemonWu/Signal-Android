package com.ytqf.signal.groups.ui.chooseadmin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.ytqf.signal.groups.GroupChangeException;
import com.ytqf.signal.groups.GroupId;
import com.ytqf.signal.groups.GroupManager;
import com.ytqf.signal.groups.ui.GroupChangeFailureReason;
import com.ytqf.signal.groups.ui.GroupChangeResult;
import com.ytqf.signal.recipients.RecipientId;

import java.io.IOException;
import java.util.List;

public final class ChooseNewAdminRepository {
  private final Application context;

  ChooseNewAdminRepository(@NonNull Application context) {
    this.context = context;
  }

  @WorkerThread
  @NonNull GroupChangeResult updateAdminsAndLeave(@NonNull GroupId.V2 groupId, @NonNull List<RecipientId> newAdminIds) {
    try {
      GroupManager.addMemberAdminsAndLeaveGroup(context, groupId, newAdminIds);
      return GroupChangeResult.SUCCESS;
    } catch (GroupChangeException | IOException e) {
      return GroupChangeResult.failure(GroupChangeFailureReason.fromException(e));
    }
  }
}
