package com.ytqf.signal.net;

import android.os.Build;

import com.ytqf.signal.BuildConfig;

/**
 * The user agent that should be used by default -- includes app name, version, etc.
 */
public class StandardUserAgentInterceptor extends UserAgentInterceptor {

  public StandardUserAgentInterceptor() {
    super("Signal-Android/" + BuildConfig.VERSION_NAME + " Android/" + Build.VERSION.SDK_INT);
  }
}
