package com.ytqf.signal.groups.ui;

import androidx.annotation.NonNull;

public interface GroupChangeErrorCallback {
  void onError(@NonNull GroupChangeFailureReason failureReason);
}
