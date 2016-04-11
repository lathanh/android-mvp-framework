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

package org.lathanh.android.mvp.demo.simple;

import android.support.annotation.NonNull;

import org.lathanh.android.mvp.adapter.simple.SimpleAdapter;
import org.lathanh.android.mvp.demo.BaseFragment;
import org.lathanh.android.mvp.demo.BaseModels;

/**
 * Adapts {@link BaseModels.DataModel}s into {@link BaseModels.ViewModel}s.
 *
 * <p>Not to be confused an Android Adapter, which does adapting and binding for
 * lists.</p>
 *
 * @author Robert LaThanh 2016-03-25
 */
public class SimpleDemo_Adapter
    implements SimpleAdapter<BaseModels.ViewModel, BaseModels.DataModel> {

  @NonNull
  @Override
  public BaseModels.ViewModel adapt(
      @NonNull BaseModels.DataModel dataModel) {
    return BaseFragment.adaptDataModelToViewModel(dataModel);
  }
}
