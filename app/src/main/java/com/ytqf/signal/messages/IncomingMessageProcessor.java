package com.ytqf.signal.messages;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ytqf.signal.database.DatabaseFactory;
import com.ytqf.signal.database.MessageDatabase.SyncMessageId;
import com.ytqf.signal.database.MmsSmsDatabase;
import com.ytqf.signal.database.PushDatabase;
import com.ytqf.signal.dependencies.ApplicationDependencies;
import com.ytqf.signal.jobmanager.JobManager;
import com.ytqf.signal.jobs.PushDecryptMessageJob;
import com.ytqf.signal.logging.Log;
import com.ytqf.signal.recipients.Recipient;
import org.whispersystems.signalservice.api.messages.SignalServiceEnvelope;

import java.io.Closeable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The central entry point for all envelopes that have been retrieved. Envelopes must be processed
 * here to guarantee proper ordering.
 */
public class IncomingMessageProcessor {

  private static final String TAG = Log.tag(IncomingMessageProcessor.class);

  private final Context       context;
  private final ReentrantLock lock;

  public IncomingMessageProcessor(@NonNull Context context) {
    this.context = context;
    this.lock    = new ReentrantLock();
  }

  /**
   * @return An instance of a Processor that will allow you to process messages in a thread safe
   * way. Must be closed.
   */
  public Processor acquire() {
    lock.lock();
    return new Processor(context);
  }

  private void release() {
    lock.unlock();
  }

  public class Processor implements Closeable {

    private final Context           context;
    private final PushDatabase      pushDatabase;
    private final MmsSmsDatabase    mmsSmsDatabase;
    private final JobManager        jobManager;

    private Processor(@NonNull Context context) {
      this.context           = context;
      this.pushDatabase      = DatabaseFactory.getPushDatabase(context);
      this.mmsSmsDatabase    = DatabaseFactory.getMmsSmsDatabase(context);
      this.jobManager        = ApplicationDependencies.getJobManager();
    }

    /**
     * @return The id of the {@link PushDecryptMessageJob} that was scheduled to process the message, if
     *         one was created. Otherwise null.
     */
    public @Nullable String processEnvelope(@NonNull SignalServiceEnvelope envelope) {
      if (envelope.hasSource()) {
        Recipient.externalHighTrustPush(context, envelope.getSourceAddress());
      }

      if (envelope.isReceipt()) {
        processReceipt(envelope);
        return null;
      } else if (envelope.isPreKeySignalMessage() || envelope.isSignalMessage() || envelope.isUnidentifiedSender()) {
        return processMessage(envelope);
      } else {
        Log.w(TAG, "Received envelope of unknown type: " + envelope.getType());
        return null;
      }
    }

    private @Nullable String processMessage(@NonNull SignalServiceEnvelope envelope) {
      Log.i(TAG, "Received message " + envelope.getTimestamp() + ". Inserting in PushDatabase.");

      long id  = pushDatabase.insert(envelope);

      if (id > 0) {
        PushDecryptMessageJob job = new PushDecryptMessageJob(context, id);

        jobManager.add(job);

        return job.getId();
      } else {
        Log.w(TAG, "The envelope was already present in the PushDatabase.");
        return null;
      }
    }

    private void processReceipt(@NonNull SignalServiceEnvelope envelope) {
      Log.i(TAG, "Received server receipt for " + envelope.getTimestamp());
      mmsSmsDatabase.incrementDeliveryReceiptCount(new SyncMessageId(Recipient.externalHighTrustPush(context, envelope.getSourceAddress()).getId(), envelope.getTimestamp()),
                                                   System.currentTimeMillis());
    }

    @Override
    public void close() {
      release();
    }
  }
}
