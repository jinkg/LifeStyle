package com.yalin.lifestyle.manager;

import com.yalin.lifestyle.LifecycleConsumer;
import java.util.Collections;
import java.util.Set;

/**
 * YaLin 2016/12/29.
 */

public class EmptyLifecycleConsumerTreeNode implements LifecycleConsumerTreeNode {

  @Override
  public Set<LifecycleConsumer> getDescendants() {
    return Collections.emptySet();
  }
}
