package com.ytqf.signal.jobmanager;

import androidx.annotation.NonNull;

import com.ytqf.signal.jobmanager.persistence.JobSpec;

public interface JobPredicate {
  JobPredicate NONE = jobSpec -> true;

  boolean shouldRun(@NonNull JobSpec jobSpec);
}
