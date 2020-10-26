package com.ytqf.signal.service;


import android.content.Context;
import android.content.Intent;

import com.ytqf.signal.dependencies.ApplicationDependencies;
import com.ytqf.signal.logging.Log;

import com.ytqf.signal.BuildConfig;
import com.ytqf.signal.jobs.UpdateApkJob;
import com.ytqf.signal.util.TextSecurePreferences;

import java.util.concurrent.TimeUnit;

public class UpdateApkRefreshListener extends PersistentAlarmManagerListener {

  private static final String TAG = UpdateApkRefreshListener.class.getSimpleName();

  private static final long INTERVAL = TimeUnit.HOURS.toMillis(6);

  @Override
  protected long getNextScheduledExecutionTime(Context context) {
    return TextSecurePreferences.getUpdateApkRefreshTime(context);
  }

  @Override
  protected long onAlarm(Context context, long scheduledTime) {
    Log.i(TAG, "onAlarm...");

    if (scheduledTime != 0 && BuildConfig.PLAY_STORE_DISABLED) {
      Log.i(TAG, "Queueing APK update job...");
      ApplicationDependencies.getJobManager().add(new UpdateApkJob());
    }

    long newTime = System.currentTimeMillis() + INTERVAL;
    TextSecurePreferences.setUpdateApkRefreshTime(context, newTime);

    return newTime;
  }

  public static void schedule(Context context) {
    new UpdateApkRefreshListener().onReceive(context, new Intent());
  }

}
