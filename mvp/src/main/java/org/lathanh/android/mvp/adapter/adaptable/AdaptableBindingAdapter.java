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
import android.support.v7.widget.RecyclerView;

/**
 * The AdaptableBindingAdapter takes {@link AdaptableViewModel} objects (objects
 * that might have not yet been adapted), and adapts them to be displaye, and
 * then binds them to the View for rendering..
 * While adapting, which happens in the background, some sort of
 * progress indicator / loading placeholder should be displayed for unadapted
 * items instead.
 *
 * @param <VM> the type of the object that Adaptable will be adapted into;
 *        "View Model"
 * @param <AVM> the object that holds the data to be adapted, and which will
 *         also be able to hold onto the View Model.
 * @param <VH> {@link RecyclerView.Adapter}'s {@code VH} type parameter
 * @author Robert LaThanh 2016-01-15
 */
public abstract
    class AdaptableBindingAdapter<VM,
                                  AVM extends AdaptableViewModel<VM>,
                                  VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH> {

  /**
   * Provide the item at the position. This allows the binding adapter to queue
   * up the item for adapting if it hasn't already been adapted.
   */
  protected abstract @NonNull AVM get(int position);

  /**
   * This override is for documentation only.
   *
   * <p>Implementations must:
   * <ol>
   *   <li>Fetch the item to be bound,</li>
   *   <li>If the {@link AdaptableViewModel#getViewModel() ViewModel
   *        within the AdaptableViewModel} is {@code null}, the item
   *        must be queued for adapting (if it isn't already), and</li>
   *   <li>Pass the item to {@link #onBindViewHolder(RecyclerView.ViewHolder, AdaptableViewModel, int)}</li>
   * </ol>
   * </p>
   *
   * @param holder {@inheritDoc}
   * @param position {@inheritDoc}
   */
  @Override
  public abstract void onBindViewHolder(@NonNull VH holder, int position);

  /**
   * Perform binding of the {@code adaptableViewModel} to the
   * View / (Loading)ViewHolder.
   *
   * @param adaptableViewModel the item at {@code position}, for
   *        convenience since the implementation already retrieved it (and
   *        since a unique method signature is needed).
   *        The {@link AdaptableViewModel#getViewModel() ViewModel
   *        within the adaptableViewModel} may be {@code null}.
   *        If it is, the implementation has already queued up the item for
   *        adapting in the background.
   */
  public abstract void onBindViewHolder(@NonNull final VH viewHolder,
                                        @NonNull AVM adaptableViewModel,
                                        int position);

}
