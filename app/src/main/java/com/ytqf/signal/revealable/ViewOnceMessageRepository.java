package com.ytqf.signal.revealable;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ytqf.signal.database.DatabaseFactory;
import com.ytqf.signal.database.MessageDatabase;
import com.ytqf.signal.database.MmsDatabase;
import com.ytqf.signal.database.model.MmsMessageRecord;
import com.ytqf.signal.logging.Log;
import com.ytqf.signal.util.concurrent.SignalExecutors;
import org.whispersystems.libsignal.util.guava.Optional;

class ViewOnceMessageRepository {

  private static final String TAG = Log.tag(ViewOnceMessageRepository.class);

  private final MessageDatabase mmsDatabase;

  ViewOnceMessageRepository(@NonNull Context context) {
    this.mmsDatabase = DatabaseFactory.getMmsDatabase(context);
  }

  void getMessage(long messageId, @NonNull Callback<Optional<MmsMessageRecord>> callback) {
    SignalExecutors.BOUNDED.execute(() -> {
      try (MmsDatabase.Reader reader = MmsDatabase.readerFor(mmsDatabase.getMessageCursor(messageId))) {
        MmsMessageRecord record = (MmsMessageRecord) reader.getNext();
        callback.onComplete(Optional.fromNullable(record));
      }
    });
  }

  interface Callback<T> {
    void onComplete(T result);
  }
}
