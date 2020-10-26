package com.ytqf.signal.groups.ui.pendingmemberinvites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.ytqf.signal.PassphraseRequiredActivity;
import com.ytqf.signal.R;
import com.ytqf.signal.groups.GroupId;
import com.ytqf.signal.groups.ui.invitesandrequests.ManagePendingAndRequestingMembersActivity;
import com.ytqf.signal.groups.ui.invitesandrequests.invited.PendingMemberInvitesFragment;
import com.ytqf.signal.util.DynamicNoActionBarTheme;
import com.ytqf.signal.util.DynamicTheme;
import com.ytqf.signal.util.FeatureFlags;

/**
 * @deprecated With group links FF, this activity is replaced with {@link ManagePendingAndRequestingMembersActivity}.
 */
@Deprecated
public class PendingMemberInvitesActivity extends PassphraseRequiredActivity {

  private static final String GROUP_ID = "GROUP_ID";

  private final DynamicTheme dynamicTheme = new DynamicNoActionBarTheme();

  public static Intent newIntent(@NonNull Context context, @NonNull GroupId.V2 groupId) {
    Intent intent = new Intent(context, PendingMemberInvitesActivity.class);
    intent.putExtra(GROUP_ID, groupId.toString());
    return intent;
  }

  @Override
  protected void onPreCreate() {
    dynamicTheme.onCreate(this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState, boolean ready) {
    super.onCreate(savedInstanceState, ready);

    if (FeatureFlags.groupsV2manageGroupLinks()) {
      throw new AssertionError();
    }

    setContentView(R.layout.group_pending_member_invites_activity);

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
                                 .replace(R.id.container, PendingMemberInvitesFragment.newInstance(GroupId.parseOrThrow(getIntent().getStringExtra(GROUP_ID)).requireV2()))
                                 .commitNow();
    }

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  public void onResume() {
    super.onResume();
    dynamicTheme.onResume(this);
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }
}
