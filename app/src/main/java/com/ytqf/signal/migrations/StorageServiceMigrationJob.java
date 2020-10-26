package com.ytqf.signal.migrations;

import androidx.annotation.NonNull;

import com.ytqf.signal.dependencies.ApplicationDependencies;
import com.ytqf.signal.jobmanager.Data;
import com.ytqf.signal.jobmanager.Job;
import com.ytqf.signal.jobmanager.JobManager;
import com.ytqf.signal.jobs.MultiDeviceKeysUpdateJob;
import com.ytqf.signal.jobs.StorageSyncJob;
import com.ytqf.signal.logging.Log;
import com.ytqf.signal.util.TextSecurePreferences;

public class StorageServiceMigrationJob extends MigrationJob {

  private static final String TAG = Log.tag(StorageServiceMigrationJob.class);

  public static final String KEY = "StorageServiceMigrationJob";

  StorageServiceMigrationJob() {
    this(new Parameters.Builder().build());
  }

  private StorageServiceMigrationJob(@NonNull Parameters parameters) {
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

    if (TextSecurePreferences.isMultiDevice(context)) {
      Log.i(TAG, "Multi-device.");
      jobManager.startChain(new StorageSyncJob())
                .then(new MultiDeviceKeysUpdateJob())
                .enqueue();
    } else {
      Log.i(TAG, "Single-device.");
      jobManager.add(new StorageSyncJob());
    }
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  public static class Factory implements Job.Factory<StorageServiceMigrationJob> {
    @Override
    public @NonNull StorageServiceMigrationJob create(@NonNull Parameters parameters, @NonNull Data data) {
      return new StorageServiceMigrationJob(parameters);
    }
  }
}
