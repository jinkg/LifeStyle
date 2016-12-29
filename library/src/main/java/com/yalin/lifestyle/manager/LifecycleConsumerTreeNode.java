package com.yalin.lifestyle.manager;

import com.yalin.lifestyle.LifecycleConsumer;
import java.util.Set;

/**
 * YaLin 2016/12/29.
 */

public interface LifecycleConsumerTreeNode {

  Set<LifecycleConsumer> getDescendants();
}
