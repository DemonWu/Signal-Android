package com.ytqf.signal.pin;

import androidx.annotation.NonNull;

import com.ytqf.signal.BuildConfig;
import com.ytqf.signal.KbsEnclave;
import com.ytqf.signal.util.Util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class KbsEnclaves {

  public static @NonNull KbsEnclave current() {
    return BuildConfig.KBS_ENCLAVE;
  }

  public static @NonNull List<KbsEnclave> all() {
    return Util.join(Collections.singletonList(BuildConfig.KBS_ENCLAVE), fallbacks());
  }

  public static @NonNull List<KbsEnclave> fallbacks() {
    return Arrays.asList(BuildConfig.KBS_FALLBACKS);
  }
}
