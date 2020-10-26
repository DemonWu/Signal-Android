package com.ytqf.signal.database;

import androidx.annotation.NonNull;

import com.ytqf.signal.recipients.RecipientId;
import com.ytqf.signal.util.LRUCache;

import java.util.HashMap;
import java.util.Map;

public class EarlyReceiptCache {

  private static final String TAG = EarlyReceiptCache.class.getSimpleName();

  private final LRUCache<Long, Map<RecipientId, Long>> cache = new LRUCache<>(100);
  private final String name;

  public EarlyReceiptCache(@NonNull String name) {
    this.name = name;
  }

  public synchronized void increment(long timestamp, @NonNull RecipientId origin) {
    Map<RecipientId, Long> receipts = cache.get(timestamp);

    if (receipts == null) {
      receipts = new HashMap<>();
    }

    Long count = receipts.get(origin);

    if (count != null) {
      receipts.put(origin, ++count);
    } else {
      receipts.put(origin, 1L);
    }

    cache.put(timestamp, receipts);
  }

  public synchronized Map<RecipientId, Long> remove(long timestamp) {
    Map<RecipientId, Long> receipts = cache.remove(timestamp);
    return receipts != null ? receipts : new HashMap<>();
  }
}
