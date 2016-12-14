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

import android.databinding.Bindable;
import android.support.annotation.NonNull;

import org.lathanh.android.mvp.adapter.adaptable.AbstractAdaptableViewModel;
import org.lathanh.android.mvp.demo.BR;
import org.lathanh.android.mvp.demo.BaseModels;

/**
 * An {@link org.lathanh.android.mvp.adapter.adaptable.AdaptableViewModel} that has
 * a {@link BaseModels.DataModel} that can be adapted into a
 * {@link BaseModels.ViewModel}.
 *
 * @author Robert LaThanh 2016-01-27
 */
public class AdaptableDemo_ViewModel
    extends AbstractAdaptableViewModel<BaseModels.ViewModel> {

  //-- Operating fields -------------------------------------------------------

  private long onBindTimeNanos;
  private @NonNull BaseModels.DataModel dataModel;


  //== Constructors ===========================================================

  public AdaptableDemo_ViewModel(@NonNull BaseModels.DataModel dataModel) {
    this.dataModel = dataModel;
  }

  //== Instance methods =======================================================

  @Override
  public void setViewModel(@NonNull BaseModels.ViewModel viewModel) {
    super.setViewModel(viewModel);
    notifyPropertyChanged(BR.viewModel);
    notifyPropertyChanged(BR.string);
  }


  //== 'AdaptableViewModel' methods ============================

  public BaseModels.DataModel getDataModel() {
    return dataModel;
  }

  public void setOnBindTimeNanos(long onBindTimeNanos) {
    this.onBindTimeNanos = onBindTimeNanos;
  }

  @NonNull
  @Bindable
  public String getBindTime() {
    long elapsed = System.nanoTime() - onBindTimeNanos;
    return String.format("%.2f ms", elapsed / 1000000f);
  }

}
