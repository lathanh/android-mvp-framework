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

package org.lathanh.android.mvp.demo.presenter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.lathanh.android.mvp.adapter.adaptable.AdaptableAdapter;
import org.lathanh.android.mvp.adapter.adaptable.AdaptableViewModel;
import org.lathanh.android.mvp.adapter.presenter.AdaptOnDemandPresenterBindingAdapter;
import org.lathanh.android.mvp.adapter.presenter.PresenterBindingAdapter;
import org.lathanh.android.mvp.demo.R;
import org.lathanh.android.mvp.demo.presenter.PresenterDemo_GreenPresenter.GreenAdaptableViewModel;
import org.lathanh.android.mvp.demo.presenter.PresenterDemo_RedPresenter.RedAdaptableViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Demonstration of usage of
 * {@link org.lathanh.android.mvp.adapter.presenter.PresenterBindingAdapter},
 * the Presenter Adapter Framework.
 *
 * See {@code README.md} at root of demo project.
 *
 * @author Robert LaThanh 2016-04-05
 */
public class PresenterDemo_Fragment extends Fragment {

  //== Constants ==============================================================

  private static final int NUM_LIST_ELEMENTS = 500;


  //== Instance fields ========================================================

  protected RecyclerView recyclerView;


  //== Instance methods =======================================================

  //-- 'Fragment' methods -----------------------------------------------------

  /**
   * Starts a task to "load data."
   *
   * The data is a heterogeneous list of {@link PresenterDemo_RedPresenter Red}
   * and {@link PresenterDemo_GreenPresenter Green} items, which have slightly
   * different visual and onLongClick behavior.
   */
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final long debugLoadStartTime = System.currentTimeMillis();
    new AsyncTask<Void, Void, List>() {


      private PresenterDemo_GreenPresenter greenPresenter;
      private PresenterDemo_RedPresenter redPresenter;

      @Override
      protected List doInBackground(Void... params) {
        //-- "Load" data
        Random random = new Random(0xABCDEF); // same "randomness" every run
        List dataModels = new ArrayList(NUM_LIST_ELEMENTS);
        for (int i = 0; i < NUM_LIST_ELEMENTS; i++) {
          int delayForItem = 6 + random.nextInt(11);
          if (random.nextBoolean()) {
            // green
            dataModels.add(
                new GreenAdaptableViewModel(i, delayForItem, new Date()));
          } else {
            // red
            dataModels.add(
                new RedAdaptableViewModel(i, delayForItem,
                                          System.currentTimeMillis()));
          }
        }


        //-- Also prepare the Presenters for this data
        greenPresenter = new PresenterDemo_GreenPresenter();
        redPresenter = new PresenterDemo_RedPresenter();

        return dataModels;
      }

      @Override
      protected void onPostExecute(List list) {
        //-- Create the BindingAdapter and add the Presenters to it
        BindingAdapter bindingAdapter =
            new BindingAdapter(LayoutInflater.from(getContext()), list);
        bindingAdapter.addPresenter(greenPresenter.getLayoutId(),
                                    greenPresenter, greenPresenter,
                                    greenPresenter);
        bindingAdapter.addPresenter(redPresenter.getLayoutId(), redPresenter,
                                    redPresenter, redPresenter);

        RecyclerView recyclerView =
            (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        recyclerView.setAdapter(bindingAdapter);


        //-- Update view to show list
        getActivity().findViewById(R.id.loadingView).setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        long debugElapsedMs = System.currentTimeMillis() - debugLoadStartTime;
        Toast.makeText(getActivity(),
                       "Load completed in " + debugElapsedMs + "ms",
                       Toast.LENGTH_SHORT).show();
      }
    }.execute();
  } // onCreate()

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_all, container, false);

    recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    RecyclerView.LayoutManager layoutManager =
        new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(layoutManager);

    return view;
  }


  //== Private inner classes ==================================================

  private static class BindingAdapter<VM,
      AVM extends AdaptableViewModel<VM> & PresenterBindingAdapter.ItemViewType,
      AA extends AdaptableAdapter<VM, AVM>,
      VH extends RecyclerView.ViewHolder,
      VHF extends PresenterBindingAdapter.ViewHolderFactory<VH>>
      extends AdaptOnDemandPresenterBindingAdapter<VM, AVM, AA, VH, VHF> {

    private final List<AVM> items;

    public BindingAdapter(@NonNull LayoutInflater layoutInflater,
                          @NonNull List<AVM> items) {
      super(layoutInflater);
      this.items = items;
    }

    @Override
    protected @NonNull AVM get(int position) {
      return items.get(position);
    }

    @Override
    public int getItemCount() {
      return items.size();
    }
  }

}
