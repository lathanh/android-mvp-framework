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

package org.lathanh.android.mvp.demo;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The common base for the
 * {@link org.lathanh.android.mvp.demo.simple.SimpleDemo_Fragment} and the
 * {@link org.lathanh.android.mvp.demo.adaptable.AdaptableDemo_Fragment}.
 *
 * <p>This base class for the Adapting Demo centralizes most of what each step
 * has in common in order to minimize differences and to simplify identification
 * of those differences.</p>
 */
public abstract class BaseFragment<M> extends Fragment {

  private static final String LOG_TAG = BaseFragment.class.getSimpleName();


  //== Constants ==============================================================

  /**
   * The number of elements in the list to show.
   * Because some approaches will process every item in this list before
   * displaying and of the items, the larger this number the longer it will take
   * for those approaches to be displaying any items.
   */
  protected static final int NUM_LIST_ELEMENTS = 500;

  /**
   * The amount of time it should take to "load" the data.
   * An artificial delay, this allows us to see what the loading UI looks like.
   */
  protected static final int LOAD_DELAY_MS = 500;

  /** Inclusive. See {@link #ITEM_ADAPTING_COST_RANDOM_SEED} */
  protected static final int ITEM_ADAPTING_COST_MIN_MS = 6;

  /** Inclusive. See {@link #ITEM_ADAPTING_COST_RANDOM_SEED} */
  protected static final int ITEM_ADAPTING_COST_MAX_MS = 15;

  protected static final int ITEM_ADAPTING_COST_DIFF_MS =
      ITEM_ADAPTING_COST_MAX_MS - ITEM_ADAPTING_COST_MIN_MS + 1;

  /**
   * Each item will have a "random" adapting cost between
   * {@link #ITEM_ADAPTING_COST_MIN_MS} and {@link #ITEM_ADAPTING_COST_MAX_MS}.
   *
   * By using a fixed seed for the "randomization," each item will be assigned
   * the same cost for every run.
   * This results in all demos getting the same sequence of costs for its items,
   * so comparisons are more fair.
   */
  protected static final long ITEM_ADAPTING_COST_RANDOM_SEED = 0xABCDEF;


  //== Instance fields ========================================================

  protected RecyclerView recyclerView;

  private long debugLoadStartTime;


  //== Instantiation ==========================================================

  /** Default constructor for subclasses, does nothing. */
  protected BaseFragment() { }


  //== Instance methods =======================================================

  //-- 'Fragment' methods ----------------------------------------------------

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Start a task to "load" the list items
    Runnable onLoadedCallback = new Runnable() {
      @Override
      public void run() {
        getActivity().findViewById(R.id.loadingView).setVisibility(View.GONE);
        getActivity().findViewById(R.id.recycler_view).setVisibility(View.VISIBLE);

        long debugElapsedMs = System.currentTimeMillis() - debugLoadStartTime;
        Toast.makeText(getActivity(),
                       "Load completed in " + debugElapsedMs + "ms",
                       Toast.LENGTH_SHORT).show();
      }
    };
    getLoadTask(onLoadedCallback).execute();
    debugLoadStartTime = System.currentTimeMillis();
  }

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

  /**
   * Return an AsyncTask that will call {@link #loadData()} in its
   * {@link AsyncTask#doInBackground(Object[])}.
   *
   * Upon completion — in its {@link AsyncTask#onPostExecute(Object)} — it must
   * call the {@code onLoadedCallback}'s {@link Runnable#run()}, which will let
   * this base fragment know that data loading has completed so it can show the
   * list.
   */
  @NonNull
  protected abstract AsyncTask<Void, Void, List<M>>
  getLoadTask(@NonNull Runnable onLoadedCallback);


  //-- 'BaseFragment' methods ------------------------------------

  /**
   * Emulates loading of strings from a place that takes
   * {@link #LOAD_DELAY_MS} ms, for example from a disk or remote server.
   *
   * It will determine how much processing cost any particular item should take
   * to "adapt" (that is, to create a {@link BaseModels.ViewModel} from it.
   * Since a fixed seed is used for the cost "randomizer", all implementations
   * will end up with items having the same sequence of costs.
   */
  protected List<BaseModels.DataModel> loadData() {
    //-- "Load" the strings
    Random random = new Random(ITEM_ADAPTING_COST_RANDOM_SEED);
    List<BaseModels.DataModel> dataModels = new ArrayList<>(NUM_LIST_ELEMENTS);
    for (int i = 0; i < NUM_LIST_ELEMENTS; i++) {
      int delayForItem =
          ITEM_ADAPTING_COST_MIN_MS +
              random.nextInt(ITEM_ADAPTING_COST_DIFF_MS);
      dataModels.add(new BaseModels.DataModel(i, delayForItem));
    }

    // artificial delay to overall "loading" so it feels more real
    try {
      Thread.sleep(LOAD_DELAY_MS);
    } catch (InterruptedException e) {
      Log.e(LOG_TAG, "Load sleep interrupted!");
    }

    return dataModels;
  }

  /** Create - "adapt" - a ViewModel from a DataModel. */
  public static BaseModels.ViewModel adaptDataModelToViewModel(BaseModels.DataModel dataModel) {
    //-- A handful of string operations (e.g., concat, parse) until delay
    //   elapsed
    String string = dataModel.getName();
    String delayDescription = BaseModels.adaptForDelay(dataModel);
    return new BaseModels.ViewModel(string, delayDescription);
  }

}
