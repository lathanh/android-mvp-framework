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

import android.support.annotation.NonNull;

import org.lathanh.android.mvp.adapter.adaptable.AdaptableAdapter;
import org.lathanh.android.mvp.demo.BaseFragment;
import org.lathanh.android.mvp.demo.BaseModels;

/**
 * Adapts {@link AdaptableDemo_ViewModel#getDataModel() the
 * data} in {@link AdaptableDemo_ViewModel} into a
 * {@link BaseModels.ViewModel}.
 *
 * <p>Not to be confused an Android Adapter, which does adapting and binding for
 * lists.</p>
 *
 * @author Robert LaThanh 2016-01-27
 */
public class AdaptableDemo_Adapter
    implements AdaptableAdapter<BaseModels.ViewModel, AdaptableDemo_ViewModel> {

  @NonNull
  @Override
  public BaseModels.ViewModel adapt(
      @NonNull AdaptableDemo_ViewModel adaptable) {
    BaseModels.DataModel dataModel = adaptable.getDataModel();
    return BaseFragment.adaptDataModelToViewModel(dataModel);
  }
}
