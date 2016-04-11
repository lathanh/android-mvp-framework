Android Model-View-Presentation Framework
===============================================================================
Practical, High-performance Android using MVVm/MVC/MVP

Android doesn't provide much of a framework when it comes to bridging data
models with the view, making it easy to do things such as:

  * Process text in the UI thread, which can cause poor scrolling performance
  * Not save view state of items in a list adapter, so when those items are
    scrolled back into view they lose state.
  * Put business logic in widgets or Fragments, where they can't be easily
    reused.

This framework makes it easy to create high-performant, resilient, and portable
mobile UI. It:
  1. Creates a better separation of data models, view state, and views, which
     means easier, better, and DRYer view state handling and consistency when
     views get detached; viz., list item recycling or view destroys (e.g.,
     Fragment or Activity) for memory reclamation.
  2. Create a better separation of views and business logic so views are more
     portable; so business logic as well as view widgets can be more easily
     included in other apps.

Goals
-------------------------------------------------------------------------------
The design of this framework steers developers into practices that give them:

### Responsiveness and High Performance (Fluid)
  * Responsiveness = Not blocking the UI thread so that the user can always
    interact with the app.
  * High Performance = 60 fps

The framework achieves these by steering the developer into doing as little work
and object creation as possible in the UI thread, doing as much processing as
possible in the background. See https://github.com/lathanh/android-mvp-demos for
demos on how typical Adapter implementations compromise scrolling performance.

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
Better separation is conducive to this.

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

In other words, Views are just a rendering (visual manifestation) of data
(that's in a View Model, which is mostly sourced from Data Models).
Views are often transient (viz., in lists), so they cannot be responsible for
view state, so that's what View Models are for.

<blockquote>"[A View] is a passive interface that displays data (the model) and
routes user commands (events) to the presenter to act upon that data."
—Wikipedia</blockquote>

Interactions with the widget should rely on a Presenter to perform the action,
and the Presenter is responsible for manipulating the View, via the View Model,
if/when appropriate.

##### View Holders
ViewHolders are simply containers for handles to View elements.

#### Rendering
Given a View tree, traverses the tree to render each View element onto the
screen.

#### View Model
A container for the data that has been prepared for display and for view state
since state needs to be preserved independent of actually being "in view."
This could be because the View may get recycled (lists) or because the View may
get "temporarily" destroyed (backgrounded Activities/Fragments).
View Models are often built from multiple Data Models.

#### Data Model
Data Models are representations of underlying data, usually directly from a
service/API/database. For example: Users, Books, and Reviews.

#### Binding
Updating the View from the View Model.

#### Adapting
Creating a View Model from Data Models.
For example, parsing HTML into Spannable text.

For a deeper look into why Adapting and Binding should be done separately, and
demos showing their affect on scrolling,
see https://github.com/lathanh/android-mvp-demos.

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
They will often use several presenters, and reuse presenters used by other
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
Find the framework for this in the ["mvp"](mvp) directory of this project (it
has its own README) to walk you through it.

Appendix
===============================================================================

Background
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
  3. Rendering (often with the help of the GPU), for example:
    * Taking a string and drawing the letters of that string onto the screen in
      the right place

Again, all of that has to happen within 16.66ms.
If while providing the View tree you do any work, such as concatenating Strings,
that eats into the 16.66ms.

Copyright 2016 Robert LaThanh
===============================================================================
This work is licensed under a (Creative Commons Attribution-NonCommercial 2.0 Generic License)[http://creativecommons.org/licenses/by-nc/2.0/].
