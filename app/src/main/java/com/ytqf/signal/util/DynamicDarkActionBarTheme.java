package com.ytqf.signal.util;

import androidx.annotation.StyleRes;

import com.ytqf.signal.R;

public class DynamicDarkActionBarTheme extends DynamicTheme {

  protected @StyleRes int getLightThemeStyle() {
    return R.style.TextSecure_LightTheme_Conversation;
  }

  protected @StyleRes int getDarkThemeStyle() {
    return R.style.TextSecure_DarkTheme_Conversation;
  }
}
