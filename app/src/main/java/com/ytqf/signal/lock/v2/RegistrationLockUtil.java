package com.ytqf.signal.lock.v2;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ytqf.signal.keyvalue.SignalStore;
import com.ytqf.signal.util.TextSecurePreferences;

public final class RegistrationLockUtil {

  private RegistrationLockUtil() {}

  public static boolean userHasRegistrationLock(@NonNull Context context) {
    return TextSecurePreferences.isV1RegistrationLockEnabled(context) || SignalStore.kbsValues().isV2RegistrationLockEnabled();
  }
}
