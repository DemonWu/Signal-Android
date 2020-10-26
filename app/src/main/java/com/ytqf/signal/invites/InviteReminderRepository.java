package com.ytqf.signal.invites;

import android.content.Context;

import com.ytqf.signal.database.DatabaseFactory;
import com.ytqf.signal.database.MmsSmsDatabase;
import com.ytqf.signal.database.RecipientDatabase;
import com.ytqf.signal.recipients.Recipient;

public final class InviteReminderRepository implements InviteReminderModel.Repository {

  private final Context context;

  public InviteReminderRepository(Context context) {
    this.context = context;
  }

  @Override
  public void setHasSeenFirstInviteReminder(Recipient recipient) {
    RecipientDatabase recipientDatabase = DatabaseFactory.getRecipientDatabase(context);
    recipientDatabase.setSeenFirstInviteReminder(recipient.getId());
  }

  @Override
  public void setHasSeenSecondInviteReminder(Recipient recipient) {
    RecipientDatabase recipientDatabase = DatabaseFactory.getRecipientDatabase(context);
    recipientDatabase.setSeenSecondInviteReminder(recipient.getId());
  }

  @Override
  public int getPercentOfInsecureMessages(int insecureCount) {
    MmsSmsDatabase mmsSmsDatabase = DatabaseFactory.getMmsSmsDatabase(context);
    int            insecure       = mmsSmsDatabase.getInsecureMessageCountForInsights();
    int            secure         = mmsSmsDatabase.getSecureMessageCountForInsights();

    if (insecure + secure == 0) return 0;
    return Math.round(100f * (insecureCount / (float) (insecure + secure)));
  }
}
