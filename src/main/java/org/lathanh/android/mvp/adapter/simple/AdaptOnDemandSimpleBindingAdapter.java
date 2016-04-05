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

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.concurrent.*;

/**
 * A SimpleBindingAdapter that queues up unadapted items as they come into view.
 * So, when an item first comes into view, its View Model is {@code null} (and
 * a placeholder should be shown for the item).
 * Once adapting has completed, the ViewModel is given to the ViewHolder (if it
 * hasn't been recycled) to update the view.
 * The ViewModel is also saved for the next time the item is scrolled into view
 * again (so it can be shown immediately).
 *
 * @param <A> {@inheritDoc}
 * @param <VM> {@inheritDoc}
 * @param <VH> {@inheritDoc}
 * @author Robert LaThanh 2016-01-15
 */
public abstract
    class AdaptOnDemandSimpleBindingAdapter<A, VM,
                                            VH extends RecyclerView.ViewHolder &
                                                SimpleBindingAdapter.Taggable>
    extends SimpleBindingAdapter<A, VM, VH> {

  //== Operating fields =======================================================

  private final @NonNull SimpleAdapter<VM, A> actualAdapter;
  private final @NonNull ExecutorService executorService;


  //== Constructors ===========================================================

  /**
   * Constructor for providing a custom executor service. For example, one that
   * uses fewer threads than the default, which means items would probably take
   * longer to adapt but may have less impact on the UI.
   */
  @SuppressWarnings("unused")
  public AdaptOnDemandSimpleBindingAdapter(
      SimpleAdapter<VM, A> actualAdapter,
      @NonNull ExecutorService executorService) {
    this.actualAdapter = actualAdapter;
    this.executorService = executorService;
  }

  /**
   * The default executor service for adapting items uses a stack so the items
   * most recently scrolled into view are adapted first. Thus, items scrolled
   * into and then off of the screen before getting picked up for adapting are
   * adapted later. The number of threads it uses is up to double the number of
   * CPUs ({@code numCpus * 2 + 1}).
   */
  public AdaptOnDemandSimpleBindingAdapter(
      @NonNull SimpleAdapter<VM, A> actualAdapter) {
    this.actualAdapter = actualAdapter;

    // queue size must be at least as large as the number of items possibly on
    // the screen
    BlockingQueue<Runnable> queue = new LinkedBlockingDeque<Runnable>(128) {
      @Override
      public Runnable poll() {
        return super.pollLast();
      }
    };

    int cpus = Runtime.getRuntime().availableProcessors();
    this.executorService = new ThreadPoolExecutor(cpus + 1, cpus * 2 + 1,
                                                  1, TimeUnit.SECONDS, queue);
  }


  //== 'RecyclerView.Adapter' methods =========================================

  @Override
  public void onBindViewHolder(final VH loadingViewHolder,
                               int position) {
    final AdaptableViewModel<A, VM> adaptableViewModel = items.get(position);
    VM viewModel = adaptableViewModel.viewModel;
    loadingViewHolder.setTag(adaptableViewModel);

    if (viewModel == null) {
      // item not yet adapted. start task to make this view available
      new AsyncTask<Void, Void, VM>() {
        @Override
        protected VM doInBackground(Void... params) {
          if (adaptableViewModel.viewModel != null) {
            // another task must have beat me to adapting. (the item was
            // scrolled onto screen, I was created, the item was scrolled off
            // and back on, another duplicate task was added to the top of the
            // queue, that task finished, then I got executed.)
            return adaptableViewModel.viewModel;
          }
          // this could still be a duplicate, concurrent job. oh well.
          return actualAdapter.adapt(adaptableViewModel.adaptable);
        }

        @Override
        protected void onPostExecute(VM viewModel) {
          // save the adapted model to the AdaptableViewModel so it's available
          // if it's requested again
          adaptableViewModel.setViewModel(viewModel);
          if (loadingViewHolder.getTag() == adaptableViewModel) {
            onViewModelReadyForViewHolder(loadingViewHolder, viewModel);
          }
        }
      }.executeOnExecutor(executorService);
    }

    // let implementation now do actual binding.
    onBindViewHolder(loadingViewHolder, viewModel, position);
  } // onBindViewHolder()

}
