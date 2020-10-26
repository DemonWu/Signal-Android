package com.ytqf.signal.profiles.edit;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.ytqf.signal.database.DatabaseFactory;
import com.ytqf.signal.dependencies.ApplicationDependencies;
import com.ytqf.signal.jobs.MultiDeviceProfileContentUpdateJob;
import com.ytqf.signal.jobs.MultiDeviceProfileKeyUpdateJob;
import com.ytqf.signal.jobs.ProfileUploadJob;
import com.ytqf.signal.logging.Log;
import com.ytqf.signal.profiles.AvatarHelper;
import com.ytqf.signal.profiles.ProfileMediaConstraints;
import com.ytqf.signal.profiles.ProfileName;
import com.ytqf.signal.profiles.SystemProfileUtil;
import com.ytqf.signal.recipients.Recipient;
import com.ytqf.signal.recipients.RecipientId;
import com.ytqf.signal.util.Util;
import com.ytqf.signal.util.concurrent.ListenableFuture;
import com.ytqf.signal.util.concurrent.SimpleTask;
import org.whispersystems.libsignal.util.guava.Optional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

class EditSelfProfileRepository implements EditProfileRepository {

  private static final String TAG = Log.tag(EditSelfProfileRepository.class);

  private final Context context;
  private final boolean excludeSystem;

  EditSelfProfileRepository(@NonNull Context context, boolean excludeSystem) {
    this.context        = context.getApplicationContext();
    this.excludeSystem  = excludeSystem;
  }

  @Override
  public void getCurrentProfileName(@NonNull Consumer<ProfileName> profileNameConsumer) {
    ProfileName storedProfileName = Recipient.self().getProfileName();
    if (!storedProfileName.isEmpty()) {
      profileNameConsumer.accept(storedProfileName);
    } else if (!excludeSystem) {
      SystemProfileUtil.getSystemProfileName(context).addListener(new ListenableFuture.Listener<String>() {
        @Override
        public void onSuccess(String result) {
          if (!TextUtils.isEmpty(result)) {
            profileNameConsumer.accept(ProfileName.fromSerialized(result));
          } else {
            profileNameConsumer.accept(storedProfileName);
          }
        }

        @Override
        public void onFailure(ExecutionException e) {
          Log.w(TAG, e);
          profileNameConsumer.accept(storedProfileName);
        }
      });
    } else {
      profileNameConsumer.accept(storedProfileName);
    }
  }

  @Override
  public void getCurrentAvatar(@NonNull Consumer<byte[]> avatarConsumer) {
    RecipientId selfId = Recipient.self().getId();

    if (AvatarHelper.hasAvatar(context, selfId)) {
      SimpleTask.run(() -> {
        try {
          return Util.readFully(AvatarHelper.getAvatar(context, selfId));
        } catch (IOException e) {
          Log.w(TAG, e);
          return null;
        }
      }, avatarConsumer::accept);
    } else if (!excludeSystem) {
      SystemProfileUtil.getSystemProfileAvatar(context, new ProfileMediaConstraints()).addListener(new ListenableFuture.Listener<byte[]>() {
        @Override
        public void onSuccess(byte[] result) {
          avatarConsumer.accept(result);
        }

        @Override
        public void onFailure(ExecutionException e) {
          Log.w(TAG, e);
          avatarConsumer.accept(null);
        }
      });
    }
  }

  @Override
  public void getCurrentDisplayName(@NonNull Consumer<String> displayNameConsumer) {
    displayNameConsumer.accept("");
  }

  @Override
  public void getCurrentName(@NonNull Consumer<String> nameConsumer) {
    nameConsumer.accept("");
  }

  @Override
  public void uploadProfile(@NonNull ProfileName profileName,
                            @NonNull String displayName,
                            boolean displayNameChanged,
                            @Nullable byte[] avatar,
                            boolean avatarChanged,
                            @NonNull Consumer<UploadResult> uploadResultConsumer)
  {
    SimpleTask.run(() -> {
      DatabaseFactory.getRecipientDatabase(context).setProfileName(Recipient.self().getId(), profileName);

      if (avatarChanged) {
        try {
          AvatarHelper.setAvatar(context, Recipient.self().getId(), avatar != null ? new ByteArrayInputStream(avatar) : null);
        } catch (IOException e) {
          return UploadResult.ERROR_IO;
        }
      }

      ApplicationDependencies.getJobManager()
                             .startChain(new ProfileUploadJob())
                             .then(Arrays.asList(new MultiDeviceProfileKeyUpdateJob(), new MultiDeviceProfileContentUpdateJob()))
                             .enqueue();

      return UploadResult.SUCCESS;
    }, uploadResultConsumer::accept);
  }

  @Override
  public void getCurrentUsername(@NonNull Consumer<Optional<String>> callback) {
    callback.accept(Recipient.self().getUsername());
  }
}
