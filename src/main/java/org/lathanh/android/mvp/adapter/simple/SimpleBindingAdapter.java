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

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The SimpleBindingAdapter takes adaptable objects ("data model" objects that
 * aren't prepared for display) and adapts them to be displayed, and then binds
 * them to the View for rendering.
 * While adapting, which happens in the background, some sort of
 * progress indicator / loading placeholder should be displayed for unadapted
 * items instead.
 *
 * @param <A> the type of the object that will be adapted; "adaptable"
 * @param <VM> the type of the object that {@code <A>} will be adapted into;
 *        "View Model"
 * @param <VH> {@link RecyclerView.Adapter}'s {@code VH} type parameter
 * @author Robert LaThanh 2016-03-25
 */
public abstract
    class SimpleBindingAdapter<A, VM, VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> {

  /**
   * In order to notify the ViewHolder that the View Model is ready, a handle
   * to the ViewHolder is kept. In order to know that the View/ViewHolder
   * hasn't been recycled (bound to another item), a back-reference (tag) is
   * needed in the ViewHolder.
   */
  public interface Taggable {
    void setTag(@Nullable Object tag);
    @Nullable Object getTag();
  }

  /**
   * Each Adaptable item in the list will be put into one of these containers
   * where it will be available for adapting (e.g., on demand, depending on the
   * implementation). This container will then also hold onto the adapted item,
   * the View Model.
   */
  protected static class AdaptableViewModel<A, VM> extends BaseObservable {
    @NonNull A adaptable;
    @Nullable VM viewModel;

    AdaptableViewModel(A adaptable) {
      this.adaptable = adaptable;
    }

    public void setViewModel(@NonNull VM viewModel) {
      this.viewModel = viewModel;
    }

    @Bindable
    @Nullable
    public VM getViewModel() {
      return viewModel;
    }
  }


  //== Operating fields =======================================================

  protected List<AdaptableViewModel<A, VM>> items;


  //== Abstract methods =======================================================

  /**
   * Perform binding of the {@code ViewModel} to the View / (Loading)ViewHolder.
   *
   * @param viewModel The ViewModel for the item at {@code position}.
   *        This may be {@code null}.
   *        If it is, the implementation has already queued up the item for
   *        adapting in the background.
   */
  public abstract void onBindViewHolder(@NonNull final VH viewHolder,
                                        @NonNull VM viewModel,
                                        int position);

  /**
   * When a View Model is given to a View (via
   * {@link #onBindViewHolder(RecyclerView.ViewHolder, Object, int)})
   * it may be {@code null} (adapting has not yet completed).
   * Once the adapting has been completed, the View/ViewHolder may need to be
   * notified (if it hasn't already been recycled) so that it can be updated to
   * show the ViewModel.
   *
   * This is called when the ViewModel is ready and the viewHolder has not been
   * recycled.
   * Implementations using Android Data Binding will simply just have to provide
   * the {@code viewModel} to the {@code viewHolder}'s binder.
   */
  protected abstract void onViewModelReadyForViewHolder(
      @NonNull VH viewHolder, @NonNull VM viewModel);


  //== 'RecyclerView.Adapter' methods =========================================

  @Override
  public int getItemCount() {
    return items.size();
  }


  //== 'SimpleBindingAdapter' methods =========================================

  public void addAll(Collection<A> adaptables) {
    if (items == null) {
      items = new ArrayList<>(adaptables.size());
    }

    for (A adaptable : adaptables) {
      items.add(new AdaptableViewModel<A, VM>(adaptable));
    }
  }

}
