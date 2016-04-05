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

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A convenient {@link AdaptableViewModel} that stores the ViewModel once
 * it's ready (via {@link #setViewModel(Object)} and makes it available
 * (via {@link #getViewModel()}.
 * Other AdaptableViewModels that are already a subclass of something else
 * will need to do these themselves.
 *
 * @author Robert LaThanh 2016-01-16
 */
public abstract class AbstractAdaptableViewModel<VM>
    extends BaseObservable
    implements AdaptableViewModel<VM> {

  private @Nullable VM viewModel;


  //== 'AdaptableViewModel' methods ===========================================

  @Bindable
  @Override
  public @Nullable VM getViewModel() {
    return viewModel;
  }

  @Override
  public void setViewModel(@NonNull VM viewModel) {
    this.viewModel = viewModel;
  }

}
