package com.ytqf.signal.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ytqf.signal.dependencies.ApplicationDependencies;
import com.ytqf.signal.jobs.PushNotificationReceiveJob;

public class BootReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    ApplicationDependencies.getJobManager().add(new PushNotificationReceiveJob(context));
  }
}
