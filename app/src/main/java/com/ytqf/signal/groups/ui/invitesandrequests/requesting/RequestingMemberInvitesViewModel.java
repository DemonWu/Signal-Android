package com.ytqf.signal.groups.ui.invitesandrequests.requesting;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ytqf.signal.R;
import com.ytqf.signal.groups.GroupId;
import com.ytqf.signal.groups.LiveGroup;
import com.ytqf.signal.groups.ui.GroupChangeFailureReason;
import com.ytqf.signal.groups.ui.GroupErrors;
import com.ytqf.signal.groups.ui.GroupMemberEntry;
import com.ytqf.signal.recipients.RecipientId;
import com.ytqf.signal.util.AsynchronousCallback;
import com.ytqf.signal.util.SingleLiveEvent;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RequestingMemberInvitesViewModel extends ViewModel {

  private final Context                                           context;
  private final RequestingMemberRepository                        requestingMemberRepository;
  private final MutableLiveData<String>                           toasts;
  private final LiveData<List<GroupMemberEntry.RequestingMember>> requesting;

  private RequestingMemberInvitesViewModel(@NonNull Context context,
                                           @NonNull GroupId.V2 groupId,
                                           @NonNull RequestingMemberRepository requestingMemberRepository)
  {
    this.context                    = context;
    this.requestingMemberRepository = requestingMemberRepository;
    this.requesting                 = new LiveGroup(groupId).getRequestingMembers();
    this.toasts                     = new SingleLiveEvent<>();
  }

  LiveData<List<GroupMemberEntry.RequestingMember>> getRequesting() {
    return requesting;
  }

  LiveData<String> getToasts() {
    return toasts;
  }

  void approveRequestFor(@NonNull GroupMemberEntry.RequestingMember requestingMember) {
    approveOrDeny(requestingMember, true);
  }

  void denyRequestFor(@NonNull GroupMemberEntry.RequestingMember requestingMember) {
    approveOrDeny(requestingMember, false);
  }

  private void approveOrDeny(@NonNull GroupMemberEntry.RequestingMember requestingMember, boolean approve) {
    RequestConfirmationDialog.show(context, requestingMember.getRequester(), approve, () -> {
        Set<RecipientId> memberAsSet = Collections.singleton(requestingMember.getRequester().getId());

        if (approve) {
          requestingMember.setBusy(true);
          requestingMemberRepository.approveRequests(memberAsSet, new AsynchronousCallback.WorkerThread<Void, GroupChangeFailureReason>() {
            @Override
            public void onComplete(@Nullable Void result) {
              requestingMember.setBusy(false);
              toasts.postValue(context.getString(R.string.RequestingMembersFragment_added_s, requestingMember.getRequester().getDisplayName(context)));
            }

            @Override
            public void onError(@Nullable GroupChangeFailureReason error) {
              requestingMember.setBusy(false);
              toasts.postValue(context.getString(GroupErrors.getUserDisplayMessage(error)));
            }
          });
        } else {
          requestingMember.setBusy(true);
          requestingMemberRepository.denyRequests(memberAsSet, new AsynchronousCallback.WorkerThread<Void, GroupChangeFailureReason>() {
            @Override
            public void onComplete(@Nullable Void result) {
              requestingMember.setBusy(false);
              toasts.postValue(context.getString(R.string.RequestingMembersFragment_denied_s, requestingMember.getRequester().getDisplayName(context)));
            }

            @Override
            public void onError(@Nullable GroupChangeFailureReason error) {
              requestingMember.setBusy(false);
              toasts.postValue(context.getString(GroupErrors.getUserDisplayMessage(error)));
            }
          });
        }
      });
  }

  public static class Factory implements ViewModelProvider.Factory {

    private final Context    context;
    private final GroupId.V2 groupId;

    public Factory(@NonNull Context context, @NonNull GroupId.V2 groupId) {
      this.context = context;
      this.groupId = groupId;
    }

    @Override
    public @NonNull <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
      //noinspection unchecked
      return (T) new RequestingMemberInvitesViewModel(context, groupId, new RequestingMemberRepository(context.getApplicationContext(), groupId));
    }
  }
}
