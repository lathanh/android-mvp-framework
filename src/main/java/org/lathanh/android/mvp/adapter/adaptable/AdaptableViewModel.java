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

package org.lathanh.android.mvp.adapter.adaptable;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * An adaptable object contains data that has not yet been Adapted, but can be
 * Adapted so it can then be bound to the View (often by a
 * {@link android.support.v7.widget.RecyclerView.Adapter}.
 * The adapted object, the "View Model" is held within this one.
 *
 * @param <VM> the type of the object that Adaptable will be adapted into;
 *        "View Model"
 * @author Robert LaThanh 2016-01-16
 */
public interface AdaptableViewModel<VM> {

  /**
   * Get the
   * already-{@link AdaptableAdapter#adapt(AdaptableViewModel) adapted}
   * version of this object, which had been saved by a previous call to
   * {@link #setViewModel(Object)}.
   *
   * @return the previously adapted object; {@code null} if it has not yet been
   *         adapted
   */
  @Nullable VM getViewModel();

  /**
   * Save the {@link AdaptableAdapter#adapt(AdaptableViewModel) adapted}
   * version of this object so that it's available when / next time it's needed.
   * If the item is still in view, this is the time to update the view; e.g.,
   * by notifying it that the view model has changed. This is the
   * responsibility of {@link AdaptableBindingAdapter} implementations.
   *
   * <p>Currently, there is no guarantee that the item will not be re-adapted
   * (for example, because of race conditions), so it is not safe to dereference
   * the adaptable data.</p>
   *
   * @param adapted the ViewModel for this item
   */
  void setViewModel(@NonNull VM adapted);

}
