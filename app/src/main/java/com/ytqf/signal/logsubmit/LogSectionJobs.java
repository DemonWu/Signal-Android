package com.ytqf.signal.logsubmit;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ytqf.signal.dependencies.ApplicationDependencies;

public class LogSectionJobs implements LogSection {

  @Override
  public @NonNull String getTitle() {
    return "JOBS";
  }

  @Override
  public @NonNull CharSequence getContent(@NonNull Context context) {
    return ApplicationDependencies.getJobManager().getDebugInfo();
  }
}
