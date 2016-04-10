/*
 *   Copyright 2016 Robert LaThanh
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.lathanh.android.mvp.adapter.simple;

import android.support.annotation.NonNull;

/**
 * A SimpleAdapter simply takes an object (data model) into its
 * {@link #adapt(Object)} method and outputs a View Model (another object)
 * that's ready for display.
 *
 * <p>It is intended to separate adapting from binding, allowing
 * {@link android.support.v7.widget.RecyclerView.Adapter RecyclerView.Adapters}
 * to only do binding (despite its name indicating is should do adapting).</p>
 *
 * @param <A> the type of the object that will be adapted; "adaptable"
 * @param <VM> the type of the object that {@code <A>} will be adapted into;
 *        "View Model"
 * @author Robert LaThanh 2016-03-25
 */
public interface SimpleAdapter<VM, A> {

  /**
   * Adapt the data contained in the {@code adaptable} into a ViewModel
   * ({@link VM}) viewModel that is ready to be bound to the View (displayed).
   *
   * When, precisely, this will happen depends on the
   * {@link SimpleBindingAdapter} used.
   * One may decide to do all of the adapting before showing any items, while
   * another may initiate adapting for each item when it comes into view
   * (showing a loading indicator placeholder while adpating is occurring).
   *
   * @return the adapted version of this object, ready to be bound
   */
  @NonNull VM adapt(@NonNull A adaptable);

}
