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

package org.lathanh.android.mvp.adapter.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import org.lathanh.android.mvp.adapter.adaptable.AdaptableAdapter;
import org.lathanh.android.mvp.adapter.adaptable.AdaptableViewModel;

/**
 * A <em>Presenter</em> Binding Adapter is an Android RecyclerView.Adapter where
 * {@link AdaptableAdapter Adapters} and {@link Binder Binders} are separate
 * operations.
 * Together, along with the {@link ViewHolderFactory}, the three compose a
 * Presenter (something that can handle all things presentation of an item).
 *
 * This makes it easy to handle heterogeneous lists by just providing the
 * presenter components for any given item type.
 *
 * @author Robert LaThanh 2016-04-05
 */
public abstract class PresenterBindingAdapter
    <VM,
        AVM extends AdaptableViewModel<VM>
            & PresenterBindingAdapter.ItemViewType,
        AA extends AdaptableAdapter<VM, AVM>,
        VH extends RecyclerView.ViewHolder,
        VHF extends PresenterBindingAdapter.ViewHolderFactory<VH>>
    extends RecyclerView.Adapter<VH> {


  //== Public interfaces classes ==============================================

  /**
   * In the PresenterBindingAdapter, all adapters are independent and unaware of
   * each other. So, in order for the PresenterBindingAdapter to be able to know
   * which adapter to use, the item must be able to identify its own type.
   *
   * It is recommended that this be the layout id of the layout for this item
   * type, as those are good unique identifiers that likely correlate well with
   * the appropriate adapter and binder.
   */
  public interface ItemViewType {
    int getItemViewType();
  }

  /** A class that creates a ViewHolder for item of type viewType. */
  public interface ViewHolderFactory<VH extends RecyclerView.ViewHolder> {
    VH onCreateViewHolder(@NonNull LayoutInflater inflater,
                          @Nullable ViewGroup parent, int viewType);
  }

  /** A class that can bind a View Model to a ViewHolder. */
  public interface Binder<VM, VH extends RecyclerView.ViewHolder,
                          AVM extends AdaptableViewModel<VM>> {

    /**
     * If the {@link AVM#getViewModel() viewModel} within the
     * {@code adaptableViewModel} is {@code null}, it has already been given to
     * {@link #onAdaptNeeded(AdaptableViewModel, int, Presenter)} to be queued
     * for adapting.
     */
    void onBindViewHolder(@NonNull AVM adaptableViewModel,
                          @NonNull VH viewHolder,
                          int position);
  }

  //== Inner classes ==========================================================

  /**
   * Contains everything needed to handle a view type:
   * <ul>
   *   <li>The {@link VHF ViewHolderFactory} that can create ViewHolders for the
   *       type</li>
   *   <li>The {@link AA AdaptableAdapter} that can adapt items for Binding
   *       (to the ViewHolder)</li>
   *   <li>The {@link Binder} that can bind the ViewModel to the ViewHolder</li>
   * </ul>
   */
  protected static class Presenter<VM,
                                   AVM extends AdaptableViewModel<VM>,
                                   AA extends AdaptableAdapter<VM, AVM>,
                                   VH extends RecyclerView.ViewHolder,
                                   VHF extends ViewHolderFactory<VH>> {
    public final @NonNull VHF viewHolderFactory;
    public final @NonNull AA adaptableAdapter;
    public final @NonNull Binder<VM, VH, AVM> binder;

    public Presenter(@NonNull VHF viewHolderFactory,
                     @NonNull AA adaptableAdapter,
                     @NonNull Binder<VM, VH, AVM> binder) {
      this.viewHolderFactory = viewHolderFactory;
      this.adaptableAdapter = adaptableAdapter;
      this.binder = binder;
    }
  } // class Presenter


  //== Instance fields ========================================================

  //-- Dependencies -----------------------------------------------------------
  private LayoutInflater inflater;

  //-- Operating fields -------------------------------------------------------

  /** For each {@link ItemViewType}, the {@link Presenter}. */
  protected final SparseArray<Presenter<VM, AVM, AA, VH, VHF>> adapters;


  //== Instantiation ==========================================================

  public PresenterBindingAdapter(@NonNull LayoutInflater layoutInflater) {
    this.inflater = layoutInflater;
    this.adapters = new SparseArray<>();
  }


  //== Abstract methods =======================================================

  /**
   * Get the item at the specified {@code position}.
   *
   * This may so that the item can be
   * {@link AdaptableAdapter#adapt(AdaptableViewModel) adapted} or
   * {@link Binder#onBindViewHolder(AdaptableViewModel, RecyclerView.ViewHolder, int) bound}.
   *
   * @param position of item in the adapter.
   * @return the item at the specified position in the adapter
   */
  protected abstract @NonNull AVM get(int position);

  /**
   * The {@link AVM AdpatableViewModel} at {@code position} is not yet adapted.
   * It should be queued for adapting.
   *
   * @param adaptableViewModel the {@link AVM AdaptableViewModel} that needs
   *        to be adapted.
   * @param position the position of the {@code adaptableViewModel}
   * @param presenter the presenter that can adapt this item
   */
  protected abstract void onAdaptNeeded(
      @NonNull AVM adaptableViewModel, int position,
      @NonNull Presenter<VM, AVM, AA, VH, VHF> presenter);


  //== 'PresenterBindingAdapter' methods ===========================================

  /**
   * Provide the Presenter components for a given type ({@code typeId}).
   *
   * @return {@code this}, so that calls can be easily chained
   */
  public @NonNull PresenterBindingAdapter<VM, AVM, AA, VH, VHF> addPresenter(
      int typeId,
      @NonNull VHF viewHolderFactory,
      @NonNull AA adaptableAdapter,
      @NonNull Binder<VM, VH, AVM> binder) {
    Presenter<VM, AVM, AA, VH, VHF>
        presenter = new Presenter<>(viewHolderFactory,
                                    adaptableAdapter,
                                    binder);
    adapters.put(typeId, presenter);
    return this;
  }

  /**
   * Returns the
   * {@link PresenterBindingAdapter.ItemViewType#getItemViewType(int)} of the
   * item at {@code position}.
   */
  @Override
  public int getItemViewType(int position) {
    return get(position).getItemViewType();
  }

  @Override
  public VH onCreateViewHolder(ViewGroup parent, int viewType) {
    Presenter<VM, AVM, AA, VH, VHF> presenter = adapters.get(viewType);
    return presenter.viewHolderFactory.onCreateViewHolder(inflater, parent,
                                                          viewType);
  }

  /**
   * Fetches the presenter for the item and gives it to the right
   * {@link Binder#onBindViewHolder(AdaptableViewModel, RecyclerView.ViewHolder, int)}
   * (based on its {@link ItemViewType#getItemViewType(int)}) for binding.
   *
   * If the {@link AVM#getViewModel() viewModel} within the item is
   * {@code null}, it will be given to
   * {@link #onAdaptNeeded(AdaptableViewModel, int, Presenter)} to be queued for
   * adapting.
   */
  @Override
  public void onBindViewHolder(VH holder, int position) {
    AVM adaptableViewModel = get(position);
    int viewType = adaptableViewModel.getItemViewType();
    Presenter<VM, AVM, AA, VH, VHF> presenter = adapters.get(viewType);

    if (adaptableViewModel.getViewModel() == null) {
      onAdaptNeeded(adaptableViewModel, position, presenter);
    }

    presenter.binder.onBindViewHolder(adaptableViewModel, holder, position);
  }
}
