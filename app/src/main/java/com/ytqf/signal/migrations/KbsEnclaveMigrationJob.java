package com.ytqf.signal.migrations;

import androidx.annotation.NonNull;

import com.ytqf.signal.dependencies.ApplicationDependencies;
import com.ytqf.signal.jobmanager.Data;
import com.ytqf.signal.jobmanager.Job;
import com.ytqf.signal.jobs.KbsEnclaveMigrationWorkerJob;

/**
 * A job to be run whenever we add a new KBS enclave. In order to prevent this moderately-expensive
 * task from blocking the network for too long, this task simply enqueues another non-migration job,
 * {@link KbsEnclaveMigrationWorkerJob}, to do the heavy lifting.
 */
public class KbsEnclaveMigrationJob extends MigrationJob {

  public static final String KEY = "KbsEnclaveMigrationJob";

  KbsEnclaveMigrationJob() {
    this(new Parameters.Builder().build());
  }

  private KbsEnclaveMigrationJob(@NonNull Parameters parameters) {
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
    ApplicationDependencies.getJobManager().add(new KbsEnclaveMigrationWorkerJob());
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  public static class Factory implements Job.Factory<KbsEnclaveMigrationJob> {
    @Override
    public @NonNull KbsEnclaveMigrationJob create(@NonNull Parameters parameters, @NonNull Data data) {
      return new KbsEnclaveMigrationJob(parameters);
    }
  }
}
