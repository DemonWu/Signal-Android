package com.ytqf.signal.pin;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.ytqf.signal.R;
import com.ytqf.signal.logging.Log;
import com.ytqf.signal.util.ThemeUtil;
import com.ytqf.signal.util.concurrent.SimpleTask;
import com.ytqf.signal.util.views.SimpleProgressDialog;

public final class PinOptOutDialog {

  private static final String TAG = Log.tag(PinOptOutDialog.class);

  public static void show(@NonNull Context context, @NonNull Runnable onSuccess) {
    Log.i(TAG, "show()");
    AlertDialog dialog = new AlertDialog.Builder(context)
                                        .setTitle(R.string.PinOptOutDialog_warning)
                                        .setMessage(R.string.PinOptOutDialog_if_you_disable_the_pin_you_will_lose_all_data)
                                        .setCancelable(true)
                                        .setPositiveButton(R.string.PinOptOutDialog_disable_pin, (d, which) -> {
                                          Log.i(TAG, "Disable clicked.");
                                          d.dismiss();
                                          AlertDialog progress = SimpleProgressDialog.show(context);

                                          SimpleTask.run(() -> {
                                            PinState.onPinOptOut();
                                            return null;
                                          }, success -> {
                                            Log.i(TAG, "Disable operation finished.");
                                            onSuccess.run();
                                            progress.dismiss();
                                          });
                                         })
                                        .setNegativeButton(android.R.string.cancel, (d, which) -> {
                                          Log.i(TAG, "Cancel clicked.");
                                          d.dismiss();
                                        })
                                        .create();

    dialog.setOnShowListener(dialogInterface -> {
      dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ThemeUtil.getThemedColor(context, R.attr.dangerous_button_color));
    });

    dialog.show();
  }
}
