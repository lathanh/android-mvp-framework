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

package org.lathanh.android.mvp.demo.adaptable;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.lathanh.android.mvp.adapter.adaptable.AdaptOnDemandAdaptableBindingAdapter;
import org.lathanh.android.mvp.demo.BaseFragment;
import org.lathanh.android.mvp.demo.BaseModels;
import org.lathanh.android.mvp.demo.databinding.AdaptableListItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstration of usage of
 * {@link org.lathanh.android.mvp.adapter.adaptable.AdaptableBindingAdapter},
 * the Adaptable Adapter Framework.
 *
 * See {@code README.md} at root of demo project.
 *
 * @author Robert LaThanh 2016-01-27
 */
public class AdaptableDemo_Fragment
    extends BaseFragment<AdaptableDemo_ViewModel> {

  //== Instantiation ==========================================================

  public static AdaptableDemo_Fragment newInstance() {
    return new AdaptableDemo_Fragment();
  }


  //== Instance methods =======================================================

  //-- 'BaseFragment' methods ------------------------------------

  /**
   * A task to "load data", which returns data models within
   * {@link AdaptableDemo_ViewModel}.
   */
  @NonNull
  @Override
  protected AsyncTask<Void, Void, List<AdaptableDemo_ViewModel>>
  getLoadTask(@NonNull final Runnable onLoadedCallback) {
    return new AsyncTask<Void, Void, List<AdaptableDemo_ViewModel>>() {
      @Override
      protected List<AdaptableDemo_ViewModel> doInBackground(Void... params) {
        List<BaseModels.DataModel> dataModels = loadData();

        // create an AdaptableViewModel for each DataModel
        // the actual ViewModel isn't adapted here
        List<AdaptableDemo_ViewModel> adaptableViewModels =
            new ArrayList<>(dataModels.size());
        for (BaseModels.DataModel dataModel : dataModels) {
          adaptableViewModels.add(
              new AdaptableDemo_ViewModel(dataModel));
        }
        return adaptableViewModels;
      }

      @Override
      protected void onPostExecute(List<AdaptableDemo_ViewModel>
                                       adaptableViewModels) {
        AdaptableDemo_Adapter actualAdapter =
            new AdaptableDemo_Adapter();
        BindingAdapter bindingAdapter =
            new BindingAdapter(getContext(), adaptableViewModels, actualAdapter);
        recyclerView.setAdapter(bindingAdapter);
        onLoadedCallback.run();
      }
    };
  }

  //== Inner classes ==========================================================

  public static class BindingAdapter
      extends AdaptOnDemandAdaptableBindingAdapter<BaseModels.ViewModel,
      AdaptableDemo_ViewModel,
      ViewHolder> {

    private final List<AdaptableDemo_ViewModel> items;
    private LayoutInflater inflater;

    public BindingAdapter(Context context,
                          List<AdaptableDemo_ViewModel> items,
                          AdaptableDemo_Adapter actualAdapter) {
      super(actualAdapter);
      this.inflater = LayoutInflater.from(context);
      this.items = items;
    }

    @NonNull
    @Override
    protected AdaptableDemo_ViewModel get(int position) {
      return items.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      AdaptableListItemBinding binding =
          AdaptableListItemBinding.inflate(inflater, parent,
                                                           false);
      return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder,
                                 @NonNull AdaptableDemo_ViewModel
                                     adaptableViewModel, int position) {
      adaptableViewModel.setOnBindTimeNanos(System.nanoTime());
      viewHolder.setAdaptableViewModel(adaptableViewModel);
    }

    @Override
    public int getItemCount() {
      return items.size();
    }
  } // class Adapter

  /**
   * The ViewHolder when using Data Binding is simple; it just uses the layout's
   * ViewDataBinding to do the binding.
   */
  public static class ViewHolder extends RecyclerView.ViewHolder {
    private final AdaptableListItemBinding binding;

    public ViewHolder(AdaptableListItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void setAdaptableViewModel(AdaptableDemo_ViewModel
                                        adaptableViewModel) {
      binding.setAdaptableViewModel(adaptableViewModel);
    }
  } // class ViewHolder

}
