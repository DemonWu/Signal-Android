package com.ytqf.signal.migrations;

import androidx.annotation.NonNull;

import com.ytqf.signal.dependencies.ApplicationDependencies;
import com.ytqf.signal.jobmanager.Data;
import com.ytqf.signal.jobmanager.Job;
import com.ytqf.signal.jobmanager.JobManager;
import com.ytqf.signal.jobs.MultiDeviceKeysUpdateJob;
import com.ytqf.signal.jobs.MultiDeviceStorageSyncRequestJob;
import com.ytqf.signal.jobs.RefreshAttributesJob;
import com.ytqf.signal.jobs.StorageForcePushJob;
import com.ytqf.signal.logging.Log;
import com.ytqf.signal.util.TextSecurePreferences;

/**
 * This does a couple things:
 *   (1) Sets the storage capability for reglockv2 users by refreshing account attributes.
 *   (2) Force-pushes storage, which is now backed by the KBS master key.
 *
 * Note: *All* users need to do this force push, because some people were in the storage service FF
 *       bucket in the past, and if we don't schedule a force push, they could enter a situation
 *       where different storage items are encrypted with different keys.
 */
public class StorageCapabilityMigrationJob extends MigrationJob {

  private static final String TAG = Log.tag(StorageCapabilityMigrationJob.class);

  public static final String KEY = "StorageCapabilityMigrationJob";

  StorageCapabilityMigrationJob() {
    this(new Parameters.Builder().build());
  }

  private StorageCapabilityMigrationJob(@NonNull Parameters parameters) {
    super(parameters);
  }

  @Override
  public boolean isUiBlocking() {
    return false;
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  public void performMigration() {
    JobManager jobManager = ApplicationDependencies.getJobManager();

    jobManager.add(new RefreshAttributesJob());

    if (TextSecurePreferences.isMultiDevice(context)) {
      Log.i(TAG, "Multi-device.");
      jobManager.startChain(new StorageForcePushJob())
                .then(new MultiDeviceKeysUpdateJob())
                .then(new MultiDeviceStorageSyncRequestJob())
                .enqueue();
    } else {
      Log.i(TAG, "Single-device.");
      jobManager.add(new StorageForcePushJob());
    }
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  public static class Factory implements Job.Factory<StorageCapabilityMigrationJob> {
    @Override
    public @NonNull StorageCapabilityMigrationJob create(@NonNull Parameters parameters, @NonNull Data data) {
      return new StorageCapabilityMigrationJob(parameters);
    }
  }
}
