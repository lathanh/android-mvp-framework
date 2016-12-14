Android Model-View-Presentation Framework
===============================================================================
Practical, High-performance Android using MVVm/MVC/MVP

Android doesn't provide much of a framework or even guidance when it comes to
creating and maintaining the view from data, which makes it easy to do things 
such as:

  * Process text in the UI thread, which can cause poor scrolling performance
  * Hold onto View elements in places where they may be carried across contexts, 
    resulting in context leaks.
  * Not save view state of items in a list adapter, so when those items are
    scrolled back into view they lose state.
  * Put business logic in widgets or Fragments, where they can't be easily
    tested or reused.

The right organization model — a way of separating and organizing code —
for views and the data that goes into them improves:

  * **Robustness and correctness** — Reducing bugs and bad practices, such as 
    context leaks
  * **Responsiveness** — How quickly the UI is ready to be interacted with and
    how quickly it responds to interactions
  * **Performance** — Fast rendering so high frame rates can be consistently
    achieved
  * **Reusability** — So code is more easily usable across multiple use-cases.
  * **Development efficiency** — Less boilerplate code being written for each
    use-case.
  * **Testability** — Code that has better separation is easier to test.

This framework first provides definitions/terminology and concepts — an 
organization model — that is the foundation for building practical, 
high-performance Android UI.
It is based on the Model-View-Presenter (MVP) and Model-View-Controller 
general-purpose patterns, but applies them for practical use in Android.
You can take just these concepts and use them to organize your code, and you 
should get the above improvements.
 
This framework also provides code, of course:

  * Base classes/implementations that guide and simplify putting code into the 
    right places of the organization model.
      * For example, the `AdaptableAdapter` gives a home to Adapting code, and
        helps ensure it is kept separate from Binding code (so they can each be
        executed and tested independently).
  * Supporting classes that do a lot of the legwork for your classes.
      * For example, the `AdaptOnDemandPresenterBindingAdapter` Adapts and Binds
        a list of `AdaptableViewModel`s into a RecyclerView for you.
   

Goals
-------------------------------------------------------------------------------
The design of this framework steers developers into practices that give them:

### Responsiveness and High Performance (Fluid)
  * Responsiveness = Not blocking the UI thread so that the user can always
    interact with the app.
  * High Performance = 60 fps

The framework achieves these by steering the developer into doing as little work
and object creation as possible in the UI thread, doing as much processing as
possible in the background.
See the
[lathanh/android-mvp-demos on github](https://github.com/lathanh/android-mvp-demos)
for demos on how typical Adapter implementations compromise scrolling
performance.

### State Preservation
The framework designs for view state to be kept in a well-defined place.
It is then easy to restore view state after being unbound; whether because of
configuration change (e.g., screen rotation) or because the view was recycled
(viz., ListViews and RecyclerViews).

The framework separates view state from the view itself (as well as the data /
date state).
This makes it easy to ensure that items that are detached from the view (whether
list items scrolled off of the screen, or when the app is paused to be put in
the background) retain their state and restore properly when reattached.
This also makes the attaching process fast, resulting in high-performing list
scrolling.

### Reusability
By separating each type of code — viz., view preparation, view binding, and
business logic — into well-defined places, each becomes more reusable.
Components are more easily reused; for example using the widget on both a page
and within a list.

### Testable
More compartmentalized code means code units are more concise and thus tests are
more concise.
It also means dependencies affect fewer components, thus each component has
fewer dependencies and is easier to test.
For example, View Models can be tested without View instrumentation.

Justification
-------------------------------------------------------------------------------
There already exist many architectures/frameworks (e.g., MVC, MVP, or MVVm) and
many technologies/solutions (e.g., Butter Knife and Android Data Binding) that
meet or address some of these goals.
None, however, meet them all (see Existing Frameworks, Technologies, and
Solutions in the Appendix).
This framework meets them all by defining an overarching framework, leveraging
some of those other solutions where appropriate.
It is also more concrete than, say, the MVP pattern by providing practical
interfaces and abstract classes.

### Practical
Further, while architectures like MVC and MVP are useful, they are also largely
theoretical.
Every Android developer and project can implement them differently.
So, even while taking advantage of, say, MVC, Android developers are likely to
implement them in such a way that doesn't result in the highest performance or
reusability, and will also likely run into state preservation challenges like
the rest of the community.

This framework not only applies the best of these frameworks to Android, it
provides mobile-app-specific strategies.

Tenets
-------------------------------------------------------------------------------
### Easy
The framework is easy to understand and use, and by simply following the
framework the app gains the benefits of the goals.

### Imposing
The framework enforces doing things the right way.

### Leverages, Flexible
The framework doesn't try to solve everything by itself.
It takes advantage of technologies that address some of the challenges.
It is flexible, allowing developers to choose which technologies they want to
use (e.g., Android Data Binding or Butterknife for binding).

### Balanced
Provides enough definition to ensure best-practices are followed; but avoid
over-engineering (which makes it less easy to use).

Terms & Definitions
-------------------------------------------------------------------------------
#### View (and View Holder)
View and layout objects (and other subclasses of `android.view.View`) are
responsible for drawing on the screen.
A TextView, for example, takes the text (String) and draws the letters of the
text onto the screen.
As another example, a Button often draws some sort of rectangle with an icon
and/or text in it that looks — and is — clickable.
Views may accept touch and click events, and call listeners upon those events.

<blockquote>"[A View] is a passive interface that displays data (the model) and
routes user commands (events) to the presenter to act upon that data."
—Wikipedia</blockquote>

In other words, Views are the bridge to creating the on-screen, visual
manifestation of data (that's in a View Model, which is mostly sourced from 
Data Models).
Views are transient (especially in lists), so they cannot be responsible for
view state, so that's what View Models are for.

Interactions with the widget rely on a Presenter to perform the action, and the 
Presenter — and only the Presenter — is responsible for manipulating the View, 
via the View Model, if/when appropriate.

View creation/manipulation, and the rendering of the View occur on the Main/UI
thread, so it's important that Views be as efficient as possible (efficient
themselves, and used efficiently).
See 'Understanding the main thread and UI pipeline' in the Appendix for more
details.

##### View Holders
ViewHolders are simply containers for handles to View elements and handlers
(allowing the Presenter to know when the View is interacted with), making
working with the View more efficient (so you don't have to `find` Views in the 
hierarchy every time they need to be manipulated. 

#### Binding
Populating Views with values.
Ideally, these values have already prepared ("adapted") for the view.
These values are stored in View Models.

#### Rendering
Given a View tree, the rendering engine (which could be software-only, or 
hardware accelerated) traverses the tree to render each View element onto the
screen.

This occurs in the main/UI thread, along with View tree initialization (see 
'Understanding the main thread and UI pipeline' in the Appendix for more
details).

#### View Model
Since Adapting is be done separately from Binding, an object/container is 
needed to hold on to data that has been prepared (adapted) data to then/later 
be bound (displayed).
Such objects are called "View Models", and can also hold view state (state not
backed by any underlying data).

  1. *A container for data prepared for display*: 
     We want to prepare data for display (Adapt) off of the main/UI thread
     (outside of the UI/rendering pipeline), because doing such preparation can
     be time consuming and affect responsiveness and high-performance scrolling 
     (see 'Understanding the main thread and UI pipeline' in the Appendix for 
     more details).
     So, we need a container where the Adapter (which prepares the data in a
     background thread) can put these prepared data into,
     which can then be given to the Binder, which applies it to the View (in the 
     UI thread).
  2. *View State*:
     State needs to be preserved independent of actually being "in view" because
     the View may get recycled (lists) or because the View may get "temporarily"
     destroyed (backgrounded Activities/Fragments).

View Models are often built from multiple Data Models.

#### Data Model
Data Models are the data as they are retrieved from a service, API, or database.
For example: Contacts, Books, and Messages.

The data in these models may need to be processed (for example, parsing HTML
into a Spannable, or combining a first and last name into one string) before it 
is ready to be displayed.

#### Adapting
Doing the processing of preparing data in Data Models for the View.
This should be done in a background thread because it may be time-consuming and
affect responsiveness and high-performance scrolling.
Creating a View Model from Data Models.

For a deeper look into why Adapting and Binding should be done separately, and
demos showing their affect on scrolling,
see https://github.com/lathanh/android-mvp-demos.

This definition varies from what is implied by Android's RecyclerView.Adapter,
whose primary job is actually for _Binding_ items (of a list) to recycled views.
Because of its misleading name, along with lots of bad examples, _Adapting_ is 
also often performed in RecyclerView.Adapters.

#### Presenter
Responsible for the UI, including preparing View Models (adapting them from Data
Models), binding View Models to the view, and determining what to do when the
user takes actions (viz., interacting with the View), firing off tasks
(e.g., remote request).

View onClickListeners should often be very thin, relying on Presenters.

Presenters should usually be singleton; i.e., one instance should be able to
power many View/Widget—Controller/Model pairings.
Android RecyclerView.Adapters should be used like list Presenters.

#### Controller
The controller determines _what_ should be displayed.
They will often use several presenters, and may reuse presenters used by other
controllers.

Android Activities and Fragments are Controllers, and may reuse a Presenter in
both single (viz., Fragment) and list (viz., Adapter) contexts.

### Model, View, View Model, Presenter, Controller (MVVM/MVC/MVP) together
The Controller determines what to show, choosing the Presenters to do the
showing.
An Android RecyclerView.Adapter (along with one or more Presenters) may be used
to Present a list of many items.
The Presenter will use an Adapter (not to be confused with Android
RecyclerView.Adapter), to prepare View Models (from Data Models).
The Presenter will also Bind View Models to the View (with the help of a View
Holder).

Framework
===============================================================================
Find the framework for this in the '[mvp](mvp)' directory of this project (it
has its own README to walk you through it).

Demo
-------------------------------------------------------------------------------
The '[demo](demo)' directory contains a small app that demonstrates the usage
of this framework (which also has its own README).

Appendix
===============================================================================

Helpful Background
-------------------------------------------------------------------------------
### Understanding the main thread and UI pipeline
To understand why it's so important that the View Model be generated in the
background and why binding in the UI thread should be as minimal as possible,
it's helpful to understand the rendering pipeline.

In order to achieve 60 frames per second, each frame must be rendered within
16.66ms.
The UI thread is responsible for many of the steps that lead to a frame that can
be displayed for the user to see:

  1. Your code: provide a View tree.
    * Typically start by inflating a layout file, perhaps add or modify
      additional views
    * Set some values (e.g., setText).
  2. Android code: process the View tree to create a rendering version of the
     tree. This includes:
    * Resolving values (e.g., text, colors, styles),
    * Pruning GONE views,
  3. Rendering (often with the help of the GPU, and sometimes not in the UI 
     thread; see 'Rendering Thread' FAQ question below), for example:
    * Taking a string and drawing the letters of that string onto the screen in
      the right place

Again, all of that has to happen within 16.66ms.
If, while providing the View tree you do any work, such as concatenating
Strings, that eats into the 16.66ms.

Frequently Asked Questions
-------------------------------------------------------------------------------
### Does Data Binding accomplish much of this?
Data binding simplifies the Binding step (that is, populating Views with
values), and is a good tool to do binding with MVP.
However, Data Binding can be used without ViewModels, meaning developers may
still do Adapting during the binding step.
In other words, Data Binding only makes binding easier, it doesn't introduce
a pattern for separating binding and adapting.
 
### Does Android's RenderThread or Prefetch make this moot?
The RenderThread 
([introduced in Lollipop](https://developer.android.com/about/versions/lollipop.html))
and
[RecyclerView Prefetch](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.LayoutManager.html#setItemPrefetchEnabled(boolean))
both make more of the 16.6ms time frame (see 'Understanding the main thread and 
UI pipeline' section above) available for other work.
These optimizations could, for example, give the developer 12ms instead of 7ms
of UI thread time.
While this might reduce the cases in which doing Adapting in the UI thread would
be problematic, doing Adapting separately and off of the UI thread would still
reduce dropped frames (increase consistently high frame rate), and result in 
cleaner and more testable code.


Copyright 2016 Robert LaThanh
===============================================================================
This work is licensed under a 
[Creative Commons Attribution-NonCommercial 2.0 Generic License](http://creativecommons.org/licenses/by-nc/2.0/).
