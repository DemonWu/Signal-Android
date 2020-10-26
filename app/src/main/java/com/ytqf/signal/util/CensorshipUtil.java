package com.ytqf.signal.util;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ytqf.signal.push.SignalServiceNetworkAccess;

public final class CensorshipUtil {

  private CensorshipUtil() {}

  public static boolean isCensored(@NonNull Context context) {
    return new SignalServiceNetworkAccess(context).isCensored(context);
  }
}
