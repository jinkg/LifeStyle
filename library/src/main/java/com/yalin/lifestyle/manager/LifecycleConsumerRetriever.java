package com.yalin.lifestyle.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.yalin.lifestyle.LifecycleConsumer;
import com.yalin.lifestyle.util.Util;
import java.util.HashMap;
import java.util.Map;

/**
 * YaLin 2016/12/29.
 */

public class LifecycleConsumerRetriever implements Handler.Callback {

  static final String FRAGMENT_TAG = "com.yalin.lifestyle.manager";

  private static final LifecycleConsumerRetriever INSTANCE = new LifecycleConsumerRetriever();

  private static final int ID_REMOVE_FRAGMENT_MANAGER = 1;
  private static final int ID_REMOVE_SUPPORT_FRAGMENT_MANAGER = 2;

  private volatile LifecycleConsumer applicationConsumer;

//  final Map<android.app.FragmentManager, RequestManagerFragment> pendingLifecycleConsumerFragments =
//      new HashMap<>();

  final Map<FragmentManager, SupportLifecycleConsumerFragment> pendingSupportLifecycleConsumerFragments =
      new HashMap<>();

  private final Handler handler;

  public static LifecycleConsumerRetriever get() {
    return INSTANCE;
  }

  public LifecycleConsumerRetriever() {
    handler = new Handler(Looper.getMainLooper(), this /* Callback */);
  }

  private LifecycleConsumer getApplicationConsumer(Context context) {
    if (applicationConsumer == null) {
      synchronized (this) {
        if (applicationConsumer == null) {
          applicationConsumer =
              new LifecycleConsumer(new ApplicationLifecycle(),
                  new EmptyLifecycleConsumerTreeNode());
        }
      }
    }

    return applicationConsumer;
  }

  public LifecycleConsumer get(Context context) {
    if (context == null) {
      throw new IllegalArgumentException("You cannot start a load on a null Context");
    } else if (Util.isOnMainThread() && !(context instanceof Application)) {
      if (context instanceof FragmentActivity) {
        return get((FragmentActivity) context);
      } else if (context instanceof Activity) {
        return get((Activity) context);
      } else if (context instanceof ContextWrapper) {
        return get(((ContextWrapper) context).getBaseContext());
      }
    }
    return getApplicationConsumer(context);
  }

  public LifecycleConsumer get(FragmentActivity activity) {
    if (Util.isOnBackgroundThread()) {
      return get(activity.getApplicationContext());
    } else {
      assertNotDestroyed(activity);
      FragmentManager fm = activity.getSupportFragmentManager();
      return supportFragmentGet(activity, fm, null);
    }
  }

  public LifecycleConsumer get(Fragment fragment) {
    if (fragment.getActivity() == null) {
      throw new IllegalArgumentException(
          "You cannot start a load on a fragment before it is attached");
    }
    if (Util.isOnBackgroundThread()) {
      return get(fragment.getActivity().getApplicationContext());
    } else {
      FragmentManager fm = fragment.getChildFragmentManager();
      return supportFragmentGet(fragment.getActivity(), fm, fragment);
    }
  }

//  public LifecycleConsumer get(Activity activity) {
//
//  }
//
//  public LifecycleConsumer get(android.app.Fragment fragment) {
//
//  }

  SupportLifecycleConsumerFragment getSupportLifecycleConsumerFragment(
      final FragmentManager fm, Fragment parentHint) {
    SupportLifecycleConsumerFragment current =
        (SupportLifecycleConsumerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
    if (current == null) {
      current = pendingSupportLifecycleConsumerFragments.get(fm);
      if (current == null) {
        current = new SupportLifecycleConsumerFragment();
        current.setParentFragmentHint(parentHint);
        pendingSupportLifecycleConsumerFragments.put(fm, current);
        fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
        handler.obtainMessage(ID_REMOVE_SUPPORT_FRAGMENT_MANAGER, fm).sendToTarget();
      }
    }
    return current;
  }

  LifecycleConsumer supportFragmentGet(Context context, FragmentManager fm, Fragment parentHint) {
    SupportLifecycleConsumerFragment current = getSupportLifecycleConsumerFragment(fm, parentHint);
    LifecycleConsumer lifecycleConsumer = current.getLifecycleConsumer();
    if (lifecycleConsumer == null) {
      lifecycleConsumer =
          new LifecycleConsumer(current.getLifecycle(), current.getLifecycleConsumerTreeNode());
      current.setLifecycleConsumer(lifecycleConsumer);
    }
    return lifecycleConsumer;
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  private static void assertNotDestroyed(Activity activity) {
    if (Build.VERSION.SDK_INT >= 17 && activity.isDestroyed()) {
      throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
    }
  }

  @Override
  public boolean handleMessage(Message msg) {
    return false;
  }
}
