package com.ytqf.signal.jobmanager.workmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ytqf.signal.jobs.AttachmentDownloadJob;
import com.ytqf.signal.jobs.AttachmentUploadJob;
import com.ytqf.signal.jobs.AvatarGroupsV1DownloadJob;
import com.ytqf.signal.jobs.CleanPreKeysJob;
import com.ytqf.signal.jobs.CreateSignedPreKeyJob;
import com.ytqf.signal.jobs.DirectoryRefreshJob;
import com.ytqf.signal.jobs.FailingJob;
import com.ytqf.signal.jobs.FcmRefreshJob;
import com.ytqf.signal.jobs.LocalBackupJob;
import com.ytqf.signal.jobs.LocalBackupJobApi29;
import com.ytqf.signal.jobs.MmsDownloadJob;
import com.ytqf.signal.jobs.MmsReceiveJob;
import com.ytqf.signal.jobs.MmsSendJob;
import com.ytqf.signal.jobs.MultiDeviceBlockedUpdateJob;
import com.ytqf.signal.jobs.MultiDeviceConfigurationUpdateJob;
import com.ytqf.signal.jobs.MultiDeviceContactUpdateJob;
import com.ytqf.signal.jobs.MultiDeviceGroupUpdateJob;
import com.ytqf.signal.jobs.MultiDeviceProfileKeyUpdateJob;
import com.ytqf.signal.jobs.MultiDeviceReadUpdateJob;
import com.ytqf.signal.jobs.MultiDeviceVerifiedUpdateJob;
import com.ytqf.signal.jobs.PushDecryptMessageJob;
import com.ytqf.signal.jobs.PushGroupSendJob;
import com.ytqf.signal.jobs.PushGroupUpdateJob;
import com.ytqf.signal.jobs.PushMediaSendJob;
import com.ytqf.signal.jobs.PushNotificationReceiveJob;
import com.ytqf.signal.jobs.PushTextSendJob;
import com.ytqf.signal.jobs.RefreshAttributesJob;
import com.ytqf.signal.jobs.RefreshPreKeysJob;
import com.ytqf.signal.jobs.RequestGroupInfoJob;
import com.ytqf.signal.jobs.RetrieveProfileAvatarJob;
import com.ytqf.signal.jobs.RetrieveProfileJob;
import com.ytqf.signal.jobs.RotateCertificateJob;
import com.ytqf.signal.jobs.RotateProfileKeyJob;
import com.ytqf.signal.jobs.RotateSignedPreKeyJob;
import com.ytqf.signal.jobs.SendDeliveryReceiptJob;
import com.ytqf.signal.jobs.SendReadReceiptJob;
import com.ytqf.signal.jobs.ServiceOutageDetectionJob;
import com.ytqf.signal.jobs.SmsReceiveJob;
import com.ytqf.signal.jobs.SmsSendJob;
import com.ytqf.signal.jobs.SmsSentJob;
import com.ytqf.signal.jobs.TrimThreadJob;
import com.ytqf.signal.jobs.TypingSendJob;
import com.ytqf.signal.jobs.UpdateApkJob;

import java.util.HashMap;
import java.util.Map;

public class WorkManagerFactoryMappings {

  private static final Map<String, String> FACTORY_MAP = new HashMap<String, String>() {{
    put("AttachmentDownloadJob", AttachmentDownloadJob.KEY);
    put("AttachmentUploadJob", AttachmentUploadJob.KEY);
    put("AvatarDownloadJob", AvatarGroupsV1DownloadJob.KEY);
    put("CleanPreKeysJob", CleanPreKeysJob.KEY);
    put("CreateSignedPreKeyJob", CreateSignedPreKeyJob.KEY);
    put("DirectoryRefreshJob", DirectoryRefreshJob.KEY);
    put("FcmRefreshJob", FcmRefreshJob.KEY);
    put("LocalBackupJob", LocalBackupJob.KEY);
    put("LocalBackupJobApi29", LocalBackupJobApi29.KEY);
    put("MmsDownloadJob", MmsDownloadJob.KEY);
    put("MmsReceiveJob", MmsReceiveJob.KEY);
    put("MmsSendJob", MmsSendJob.KEY);
    put("MultiDeviceBlockedUpdateJob", MultiDeviceBlockedUpdateJob.KEY);
    put("MultiDeviceConfigurationUpdateJob", MultiDeviceConfigurationUpdateJob.KEY);
    put("MultiDeviceContactUpdateJob", MultiDeviceContactUpdateJob.KEY);
    put("MultiDeviceGroupUpdateJob", MultiDeviceGroupUpdateJob.KEY);
    put("MultiDeviceProfileKeyUpdateJob", MultiDeviceProfileKeyUpdateJob.KEY);
    put("MultiDeviceReadUpdateJob", MultiDeviceReadUpdateJob.KEY);
    put("MultiDeviceVerifiedUpdateJob", MultiDeviceVerifiedUpdateJob.KEY);
    put("PushContentReceiveJob", FailingJob.KEY);
    put("PushDecryptJob", PushDecryptMessageJob.KEY);
    put("PushGroupSendJob", PushGroupSendJob.KEY);
    put("PushGroupUpdateJob", PushGroupUpdateJob.KEY);
    put("PushMediaSendJob", PushMediaSendJob.KEY);
    put("PushNotificationReceiveJob", PushNotificationReceiveJob.KEY);
    put("PushTextSendJob", PushTextSendJob.KEY);
    put("RefreshAttributesJob", RefreshAttributesJob.KEY);
    put("RefreshPreKeysJob", RefreshPreKeysJob.KEY);
    put("RefreshUnidentifiedDeliveryAbilityJob", FailingJob.KEY);
    put("RequestGroupInfoJob", RequestGroupInfoJob.KEY);
    put("RetrieveProfileAvatarJob", RetrieveProfileAvatarJob.KEY);
    put("RetrieveProfileJob", RetrieveProfileJob.KEY);
    put("RotateCertificateJob", RotateCertificateJob.KEY);
    put("RotateProfileKeyJob", RotateProfileKeyJob.KEY);
    put("RotateSignedPreKeyJob", RotateSignedPreKeyJob.KEY);
    put("SendDeliveryReceiptJob", SendDeliveryReceiptJob.KEY);
    put("SendReadReceiptJob", SendReadReceiptJob.KEY);
    put("ServiceOutageDetectionJob", ServiceOutageDetectionJob.KEY);
    put("SmsReceiveJob", SmsReceiveJob.KEY);
    put("SmsSendJob", SmsSendJob.KEY);
    put("SmsSentJob", SmsSentJob.KEY);
    put("TrimThreadJob", TrimThreadJob.KEY);
    put("TypingSendJob", TypingSendJob.KEY);
    put("UpdateApkJob", UpdateApkJob.KEY);
  }};

  public static @Nullable String getFactoryKey(@NonNull String workManagerClass) {
    return FACTORY_MAP.get(workManagerClass);
  }
}
