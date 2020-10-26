package com.ytqf.signal.database.loaders;

import android.content.Context;
import android.database.Cursor;

import com.ytqf.signal.database.DatabaseFactory;
import com.ytqf.signal.util.AbstractCursorLoader;

public class BlockedContactsLoader extends AbstractCursorLoader {

  public BlockedContactsLoader(Context context) {
    super(context);
  }

  @Override
  public Cursor getCursor() {
    return DatabaseFactory.getRecipientDatabase(getContext()).getBlocked();
  }

}
