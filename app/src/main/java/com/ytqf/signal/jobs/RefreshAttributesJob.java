package com.ytqf.signal.jobs;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.ytqf.signal.AppCapabilities;
import com.ytqf.signal.crypto.ProfileKeyUtil;
import com.ytqf.signal.dependencies.ApplicationDependencies;
import com.ytqf.signal.jobmanager.Data;
import com.ytqf.signal.jobmanager.Job;
import com.ytqf.signal.jobmanager.impl.NetworkConstraint;
import com.ytqf.signal.keyvalue.KbsValues;
import com.ytqf.signal.keyvalue.SignalStore;
import com.ytqf.signal.logging.Log;
import com.ytqf.signal.util.TextSecurePreferences;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.crypto.UnidentifiedAccess;
import org.whispersystems.signalservice.api.push.exceptions.NetworkFailureException;
import org.whispersystems.signalservice.api.account.AccountAttributes;

import java.io.IOException;

public class RefreshAttributesJob extends BaseJob {

  public static final String KEY = "RefreshAttributesJob";

  private static final String TAG = RefreshAttributesJob.class.getSimpleName();

  public RefreshAttributesJob() {
    this(new Job.Parameters.Builder()
                           .addConstraint(NetworkConstraint.KEY)
                           .setQueue("RefreshAttributesJob")
                           .setMaxInstances(2)
                           .build());
  }

  private RefreshAttributesJob(@NonNull Job.Parameters parameters) {
    super(parameters);
  }

  @Override
  public @NonNull Data serialize() {
    return Data.EMPTY;
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  public void onRun() throws IOException {
    if (!TextSecurePreferences.isPushRegistered(context) || TextSecurePreferences.getLocalNumber(context) == null) {
      Log.w(TAG, "Not yet registered. Skipping.");
      return;
    }

    int       registrationId              = TextSecurePreferences.getLocalRegistrationId(context);
    boolean   fetchesMessages             = TextSecurePreferences.isFcmDisabled(context);
    byte[]    unidentifiedAccessKey       = UnidentifiedAccess.deriveAccessKeyFrom(ProfileKeyUtil.getSelfProfileKey());
    boolean   universalUnidentifiedAccess = TextSecurePreferences.isUniversalUnidentifiedAccess(context);
    String    registrationLockV1          = null;
    String    registrationLockV2          = null;
    KbsValues kbsValues                   = SignalStore.kbsValues();

    if (kbsValues.isV2RegistrationLockEnabled()) {
      registrationLockV2 = kbsValues.getRegistrationLockToken();
    } else if (TextSecurePreferences.isV1RegistrationLockEnabled(context)) {
      //noinspection deprecation Ok to read here as they have not migrated
      registrationLockV1 = TextSecurePreferences.getDeprecatedV1RegistrationLockPin(context);
    }

    boolean phoneNumberDiscoverable = SignalStore.phoneNumberPrivacy().getPhoneNumberListingMode().isDiscoverable();

    AccountAttributes.Capabilities capabilities = AppCapabilities.getCapabilities(kbsValues.hasPin() && !kbsValues.hasOptedOut());
    Log.i(TAG, "Calling setAccountAttributes() reglockV1? " + !TextUtils.isEmpty(registrationLockV1) + ", reglockV2? " + !TextUtils.isEmpty(registrationLockV2) + ", pin? " + kbsValues.hasPin() +
               "\n    Phone number discoverable : " + phoneNumberDiscoverable +
               "\n  Capabilities:" +
               "\n    Storage? " + capabilities.isStorage() +
               "\n    GV2? " + capabilities.isGv2() +
               "\n    GV1 Migration? " + capabilities.isGv1Migration() +
               "\n    UUID? " + capabilities.isUuid());

    SignalServiceAccountManager signalAccountManager = ApplicationDependencies.getSignalServiceAccountManager();
    signalAccountManager.setAccountAttributes(null, registrationId, fetchesMessages,
                                              registrationLockV1, registrationLockV2,
                                              unidentifiedAccessKey, universalUnidentifiedAccess,
                                              capabilities,
                                              phoneNumberDiscoverable);
  }

  @Override
  public boolean onShouldRetry(@NonNull Exception e) {
    return e instanceof NetworkFailureException;
  }

  @Override
  public void onFailure() {
    Log.w(TAG, "Failed to update account attributes!");
  }

  public static class Factory implements Job.Factory<RefreshAttributesJob> {
    @Override
    public @NonNull RefreshAttributesJob create(@NonNull Parameters parameters, @NonNull com.ytqf.signal.jobmanager.Data data) {
      return new RefreshAttributesJob(parameters);
    }
  }
}
