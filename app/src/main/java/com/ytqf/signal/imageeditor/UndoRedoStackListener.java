package com.ytqf.signal.imageeditor;

public interface UndoRedoStackListener {

  void onAvailabilityChanged(boolean undoAvailable, boolean redoAvailable);
}
