package com.yalin.lifestyle;

import android.content.Context;

/**
 * YaLin 2016/12/29.
 */

public class LifeStyle {

  private static volatile LifeStyle lifeStyle;

  public static LifeStyle get(Context context) {
    if (lifeStyle == null) {
      synchronized (LifeStyle.class) {
        if (lifeStyle == null) {
          Context applicationContext = context.getApplicationContext();
          lifeStyle = new LifeStyle(applicationContext);
        }
      }
    }

    return lifeStyle;
  }

  LifeStyle(Context context) {
  }
}
