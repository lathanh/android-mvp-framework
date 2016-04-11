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

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import org.lathanh.android.mvp.adapter.adaptable.AdaptableAdapter;
import org.lathanh.android.mvp.adapter.adaptable.AdaptableViewModel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A rudimentary {@link PresenterBindingAdapter} that submits an item for
 * adapting when it comes into view.
 * Some of what makes it rudimentary are that: it will process duplicate
 * requests, it doesn't do predictive queuing (e.g., adapting for items below
 * the fold, likely to be scrolled onto the screen).
 *
 * @author Robert LaThanh 2016-04-07
 */
public abstract class AdaptOnDemandPresenterBindingAdapter
    <VM,
        AVM extends AdaptableViewModel<VM> & PresenterBindingAdapter.ItemViewType,
        AA extends AdaptableAdapter<VM, AVM>,
        VH extends RecyclerView.ViewHolder,
        VHF extends PresenterBindingAdapter.ViewHolderFactory<VH>>
    extends PresenterBindingAdapter<VM, AVM, AA, VH, VHF> {

  //== Operating fields =======================================================

  private final @NonNull ExecutorService executorService;


  //== Constructors ===========================================================

  /**
   * The default executor service for adapting items uses a stack so the items
   * most recently scrolled into view are adapted first. Thus, items scrolled
   * into and then off of the screen before getting picked up for adapting are
   * adapted later. The number of threads it uses is up to double the number of
   * CPUs ({@code numCpus * 2 + 1}).
   */
  public AdaptOnDemandPresenterBindingAdapter(
      @NonNull LayoutInflater layoutInflater) {
    super(layoutInflater);

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

  @Override
  protected void onAdaptNeeded(
      @NonNull final AVM adaptableViewModel, int position,
      @NonNull final Presenter<VM, AVM, AA, VH, VHF> presenter) {
    // item not yet adapted. start task to make this view available
    new AsyncTask<Void, Void, VM>() {
      @Override
      protected VM doInBackground(Void... params) {
        if (adaptableViewModel.getViewModel() != null) {
          // another task must have beat me to adapting. (the item was
          // scrolled onto screen, I was created, the item was scrolled off
          // and back on, another duplicate task was added to the top of the
          // queue, that task finished, then I got executed.)
          return adaptableViewModel.getViewModel();
        }
        // this could still be a duplicate, concurrent job. oh well.
        return presenter.adaptableAdapter.adapt(adaptableViewModel);
      }

      @Override
      protected void onPostExecute(VM viewModel) {
        // save the adapted model to the adaptableViewModel so it's available
        // if it's requested again
        adaptableViewModel.setViewModel(viewModel);
      }
    }.executeOnExecutor(executorService);
  }

}
