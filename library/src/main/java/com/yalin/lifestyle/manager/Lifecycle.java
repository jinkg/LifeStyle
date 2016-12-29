package com.yalin.lifestyle.manager;

/**
 * YaLin 2016/12/29.
 */

public interface Lifecycle {

  void addListener(LifecycleListener listener);

  void removeListener(LifecycleListener listener);
}
