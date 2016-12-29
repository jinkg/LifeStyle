package com.yalin.lifestyle;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.yalin.lifestyle.manager.LifecycleConsumerRetriever;

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

  public static LifecycleConsumer with(Context context) {
    LifecycleConsumerRetriever retriever = LifecycleConsumerRetriever.get();
    return retriever.get(context);
  }

  public static LifecycleConsumer with(Activity activity) {
    LifecycleConsumerRetriever retriever = LifecycleConsumerRetriever.get();
    return retriever.get(activity);
  }

  public static LifecycleConsumer with(FragmentActivity activity) {
    LifecycleConsumerRetriever retriever = LifecycleConsumerRetriever.get();
    return retriever.get(activity);
  }

  public static LifecycleConsumer with(Fragment fragment) {
    LifecycleConsumerRetriever retriever = LifecycleConsumerRetriever.get();
    return retriever.get(fragment);
  }

  public static LifecycleConsumer with(android.app.Fragment fragment) {
    LifecycleConsumerRetriever retriever = LifecycleConsumerRetriever.get();
    return retriever.get(fragment);
  }
}
