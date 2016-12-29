package com.yalin.lifestyle;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.yalin.lifestyle.manager.Lifecycle;
import com.yalin.lifestyle.manager.LifecycleConsumerTreeNode;
import com.yalin.lifestyle.manager.LifecycleListener;
import com.yalin.lifestyle.util.Util;

/**
 * YaLin 2016/12/29.
 */

public class LifecycleConsumer implements LifecycleListener {

  private static final String TAG = "LifecycleConsumer";

  private final Lifecycle lifecycle;
  private final LifecycleConsumerTreeNode treeNode;
  private final Runnable addSelfToLifecycle = new Runnable() {
    @Override
    public void run() {
      lifecycle.addListener(LifecycleConsumer.this);
    }
  };
  private final Handler mainHandler = new Handler(Looper.getMainLooper());

  public LifecycleConsumer(Lifecycle lifecycle, LifecycleConsumerTreeNode treeNode) {
    this.lifecycle = lifecycle;
    this.treeNode = treeNode;

    if (Util.isOnBackgroundThread()) {
      mainHandler.post(addSelfToLifecycle);
    } else {
      lifecycle.addListener(this);
    }
  }

  @Override
  public void onStart() {
    Log.d(TAG, "onStart: ");
  }

  @Override
  public void onStop() {
    Log.d(TAG, "onStop: ");
  }

  @Override
  public void onDestroy() {
    Log.d(TAG, "onDestroy: ");
  }
}
