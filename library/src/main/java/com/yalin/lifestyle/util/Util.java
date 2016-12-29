package com.yalin.lifestyle.util;

import android.os.Looper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * YaLin 2016/12/29.
 */

public final class Util {

  public static boolean isOnMainThread() {
    return Looper.myLooper() == Looper.getMainLooper();
  }

  public static boolean isOnBackgroundThread() {
    return !isOnMainThread();
  }

  public static <T> List<T> getSnapshot(Collection<T> other) {
    List<T> result = new ArrayList<>(other.size());
    for (T item : other) {
      result.add(item);
    }
    return result;
  }
}
