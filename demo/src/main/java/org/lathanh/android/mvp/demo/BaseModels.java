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

import android.annotation.SuppressLint;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.text.Html;

/**
 * This class simply contains (as inner classes) the various models that will
 * be used in the various implementations of this demo.
 *
 * Normally they would each be their own top-level classes, but putting them
 * all in one place should make them easier to see how they relate to each
 * other.
 *
 * This also contains some adapting code that would normally be in
 * {@link BaseFragment}, but Android Data Binding can't use them if
 * they're there because it doesn't know how to handle the class with a generic
 * type definition.
*/
public class BaseModels {

  /**
   * The model of the data as it comes from the data source.
   * In other words, this is a Java object that closely matches the data
   * source's model, and simply holds that data.
   */
  public static class DataModel {
    private final String name;
    private final int delayMs;

    public DataModel(int position, int delayMs) {
      this.name = Integer.toString(position);
      this.delayMs = delayMs;
    }

    public String getName() {
      return name;
    }

    public int getDelayMs() {
      return delayMs;
    }
  }

  /**
   * The data as it should be displayed.
   * While it often has a strong resemblance to a data model, it may also often:
   * <ul>
   *   <li>transform/format the data for display,</li>
   *   <li>contain data from multiple data models, and</li>
   *   <li>contain data/state not found in and data model.</li>
   * </ul>
   *
   * The {@code @Bindable} annotations are used by the DataBinding demos and
   * have no effect on the other demos.
   */
  public static class ViewModel extends BaseObservable {
    private String string;
    private String delay;

    public ViewModel(String string, String delay) {
      this.string = string;
      this.delay = delay;
    }

    @Bindable
    public String getString() {
      return string;
    }

    @Bindable
    public String getDelay() {
      return delay;
    }
  }

  /**
   * Adapt a dataModel's {@link DataModel#getDelayMs()} field into a string
   * value, and take about that long to do it.
   * This will repeatedly perform string operations until it has reached the
   * time cost specified by getDelayMs.
   */
  @SuppressLint("DefaultLocale")
  public static String adaptForDelay(DataModel dataModel) {
    String format = "(%d) [%d] {%d}";
    long elapsedNanos = 0;
    int i = 0;
    for (long startNanos = System.nanoTime();
         elapsedNanos < dataModel.getDelayMs() * 1000000;
         elapsedNanos = System.nanoTime() - startNanos, i++) {
      //noinspection unused
      CharSequence s = "i=" + i + "; delay=" + dataModel.getDelayMs();
      //noinspection UnusedAssignment
      s = String.format(format, i, i, i);
      //noinspection UnusedAssignment
      s = Html.fromHtml("<b>Bold</b><strong>strong</strong><em>em</em>");
    }

    return
        String.format("adapt: %1$.2f ms (target %2$d ms; %3$,d its)",
                      elapsedNanos / 1000000f, dataModel.getDelayMs(), i);
  }
}
