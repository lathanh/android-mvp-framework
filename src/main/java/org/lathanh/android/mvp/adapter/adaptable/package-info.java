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
 * In the "Adaptable" adapter framework, each item in the list is an
 * {@link org.lathanh.android.mvp.adapter.adaptable.AdaptableViewModel}, a
 * container for data that needs to be adapted before it's ready for display.
 *
 * Once adapted &emdash; by an
 * {@link org.lathanh.android.mvp.adapter.adaptable.AdaptableAdapter}, which
 * produces a ViewModel &emdash; the ViewModel is stored within the
 * AdaptableViewModel where it can be bound to the View.
 */
package org.lathanh.android.mvp.adapter.adaptable;
