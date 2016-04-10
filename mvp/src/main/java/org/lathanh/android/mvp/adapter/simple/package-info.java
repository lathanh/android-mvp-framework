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

/**
 * In the "Simple" adapter framework, each item in the list can be adapted into
 * a View Model by a
 * {@link org.lathanh.android.mvp.adapter.simple.SimpleAdapter}.
 *
 * The View Model, which is {@code null} if it hasn't been adapted yet, is
 * given to the AdaptableBindingAdapter to be bound to the view.
 * If the View Model was {@code null} when it was bound but then becomes
 * available (after being adapted in the background), it is given to the
 * AdaptableBindingAdapter again to be re-bound.
 */
package org.lathanh.android.mvp.adapter.simple;