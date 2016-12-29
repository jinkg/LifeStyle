package com.yalin.lifestyle.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.yalin.lifestyle.LifecycleConsumer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * YaLin 2016/12/29.
 */

public class SupportLifecycleConsumerFragment extends Fragment {

  private static final String TAG = "SupportFragment";

  private final ActivityFragmentLifecycle lifecycle;
  private final LifecycleConsumerTreeNode lifecycleConsumerTreeNode =
      new SupportFragmentLifecycleConsumerTreeNode();
  private final HashSet<SupportLifecycleConsumerFragment> childLifecycleConsumerFragments =
      new HashSet<>();

  private SupportLifecycleConsumerFragment rootLifeCycleConsumerFragment;
  private LifecycleConsumer lifecycleConsumer;
  private Fragment parentFragmentHint;

  public SupportLifecycleConsumerFragment() {
    this(new ActivityFragmentLifecycle());
  }

  @SuppressLint("ValidFragment")
  public SupportLifecycleConsumerFragment(ActivityFragmentLifecycle lifecycle) {
    this.lifecycle = lifecycle;
  }

  public LifecycleConsumer getLifecycleConsumer() {
    return lifecycleConsumer;
  }

  public void setLifecycleConsumer(LifecycleConsumer lifecycleConsumer) {
    this.lifecycleConsumer = lifecycleConsumer;
  }

  ActivityFragmentLifecycle getLifecycle() {
    return lifecycle;
  }

  public LifecycleConsumerTreeNode getLifecycleConsumerTreeNode() {
    return lifecycleConsumerTreeNode;
  }

  private void addChildRequestManagerFragment(SupportLifecycleConsumerFragment child) {
    childLifecycleConsumerFragments.add(child);
  }

  private void removeChildRequestManagerFragment(SupportLifecycleConsumerFragment child) {
    childLifecycleConsumerFragments.remove(child);
  }


  void setParentFragmentHint(Fragment parentFragmentHint) {
    this.parentFragmentHint = parentFragmentHint;
    if (parentFragmentHint != null && parentFragmentHint.getActivity() != null) {
      registerFragmentWithRoot(parentFragmentHint.getActivity());
    }
  }

  private void registerFragmentWithRoot(FragmentActivity activity) {
    unregisterFragmentWithRoot();
    rootLifeCycleConsumerFragment = LifecycleConsumerRetriever.get()
        .getSupportLifecycleConsumerFragment(activity.getSupportFragmentManager(), null);
    if (rootLifeCycleConsumerFragment != this) {
      rootLifeCycleConsumerFragment.addChildRequestManagerFragment(this);
    }
  }

  private void unregisterFragmentWithRoot() {
    if (rootLifeCycleConsumerFragment != null) {
      rootLifeCycleConsumerFragment.removeChildRequestManagerFragment(this);
      rootLifeCycleConsumerFragment = null;
    }
  }

  private Fragment getParentFragmentUsingHint() {
    Fragment fragment = getParentFragment();
    return fragment != null ? fragment : parentFragmentHint;
  }

  public Set<SupportLifecycleConsumerFragment> getDescendantLifecycleConsumerFragments() {
    if (rootLifeCycleConsumerFragment == null) {
      return Collections.emptySet();
    } else if (rootLifeCycleConsumerFragment == this) {
      return Collections.unmodifiableSet(childLifecycleConsumerFragments);
    } else {
      HashSet<SupportLifecycleConsumerFragment> descendants = new HashSet<>();
      for (SupportLifecycleConsumerFragment fragment : rootLifeCycleConsumerFragment
          .getDescendantLifecycleConsumerFragments()) {
        if (isDescendant(fragment.getParentFragmentUsingHint())) {
          descendants.add(fragment);
        }
      }
      return Collections.unmodifiableSet(descendants);
    }
  }

  private boolean isDescendant(Fragment fragment) {
    Fragment root = this.getParentFragmentUsingHint();
    while (fragment.getParentFragment() != null) {
      if (fragment.getParentFragment() == root) {
        return true;
      }
      fragment = fragment.getParentFragment();
    }
    return false;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      registerFragmentWithRoot(getActivity());
    } catch (IllegalStateException e) {
      // OnAttach can be called after the activity is destroyed, see #497.
      if (Log.isLoggable(TAG, Log.WARN)) {
        Log.w(TAG, "Unable to register fragment with root", e);
      }
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    parentFragmentHint = null;
    unregisterFragmentWithRoot();
  }

  @Override
  public void onStart() {
    super.onStart();
    lifecycle.onStart();
  }

  @Override
  public void onStop() {
    super.onStop();
    lifecycle.onStop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    lifecycle.onDestroy();
    unregisterFragmentWithRoot();
  }

  private class SupportFragmentLifecycleConsumerTreeNode implements LifecycleConsumerTreeNode {

    @Override
    public Set<LifecycleConsumer> getDescendants() {
      Set<SupportLifecycleConsumerFragment> descendantFragments =
          getDescendantLifecycleConsumerFragments();
      HashSet<LifecycleConsumer> descendants = new HashSet<>(descendantFragments.size());
      for (SupportLifecycleConsumerFragment fragment : descendantFragments) {
        if (fragment.getLifecycleConsumer() != null) {
          descendants.add(fragment.getLifecycleConsumer());
        }
      }
      return descendants;
    }
  }
}
