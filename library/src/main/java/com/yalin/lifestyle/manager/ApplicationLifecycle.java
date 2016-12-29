package com.yalin.lifestyle.manager;

/**
 * YaLin 2016/12/29.
 */

class ApplicationLifecycle implements Lifecycle {

  @Override
  public void addListener(LifecycleListener listener) {
    listener.onStart();
  }

  @Override
  public void removeListener(LifecycleListener listener) {

  }
}
