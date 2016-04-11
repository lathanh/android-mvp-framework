By performing Adapting (making data ready for display) separately from Binding
(attaching data — ideally that has already been prepared (adapted) — to the 
View), we get [consistently smooth scrolling in 
lists](https://github.com/lathanh/android-mvp-demos), and better separation of
business logic and presentation.

This Model-View-Presenter framework provides two frameworks that encourage and 
make it easy for developers to provide "adapters" separately from binding.
It also makes it easy to take advantage of smart binding adapters that perform 
adapting on-demand in the background.

In order for adapting to happen in the background, a container "View Model" is
needed. That is, a View Model that is empty while the adapter data is not
ready (in which case the View will show some sort of loading/progress 
placeholder), and which will then contain the actual View Model when that's 
ready. This container is called a `AdaptableViewModel` in this framework.

There are actually three frameworks provided here, and each accomplishes the
framework goals in different ways and have their own PROs and CONs:

  1. **Simple**: The "Simple" framework handles all of the containers for you.
     You provide it with the list of items, it puts them into 
     AdaptableViewModels.
     When it comes time to bind, it provides you with the ViewModel to bind, if
     it's available (`null` if it's not yet ready, in which case you show some
     placeholder instead).
     This means that the framework handles the list of items, and all 
     modifications of the list must go through it.
  2. **Adaptable**: With the "Adaptable" framework, you handle all of the items
     and thus the containers yourself. So, each of your items must implement
     `AdaptableViewModel`.
     This gives you the responsibility/ability to manage your own list of items, 
     as is the case with the base Android `RecyclerView.Adapter`.
  3. **Presenter**: Adapters, Binders, and ViewHolder factory are provided 
     together for a given item type. Together, the three compose a Presenter
     (something that can handle all things presentation of an item).
  
These frameworks are designed to be used with RecyclerViews, as opposed to 
ListViews (but can trivially be rewritten for ListViews), and are best paired 
with Android Data Binding.
The [Android MVP Demos](https://github.com/lathanh/android-mvp-demos) has 
examples on using these frameworks.

Terminology
-------------------------------------------------------------------------------
  * **Binding**: Populating Views with values.
    Ideally, these values have already prepared ("adapted") for the view.
  * **Adapting**: Preparing data for display; more specifically, preparing for 
    binding to the View.
  * **RecyclerView.Adapter**: Android's RecyclerView.Adapter's primary job is
    actually for _Binding_ items (of a list) to recycled views.
    Because of its misleading name, along with lots of bad examples, _Adapting_ 
    is also often performed in RecyclerView.Adapters.
    In this framework, the objects responsible for actual Adapting are also 
    called Adapters (`SimpleAdapter` and `AdaptableAdapter`).
  * **View Model**: Since Adapting will be done separately from Binding, an
    object is needed to hold on to adapted data to then/later be bound.
    Such objects are called "View Models".
  * **Adaptable** / **Data Model**: The data that needs to be adapted for view
    (adapted into a View Model) are often Data Models (data as they are 
    received from servers or retrieved from databases).
    A more general term, "Adaptable", is used here.

The Three Frameworks
-------------------------------------------------------------------------------
### Simple
For the "Simple" framework, you provide a SimpleAdapter that takes in a Data 
Model and outputs a View Model.
The Binding Adapter will use the SimpleAdapter to adapt items in the background,
making the View Models available when they're ready.
In order to keep track of View Models, the Simple BindingAdapters keeps track of
items for you.
This makes it harder for you to manipulate the list of items.

### Adaptable
For the "Adaptable" framework, you provide "AdaptableViewModel" objects.
That is, objects that have the ability to return a View Model of themselves once
the AdaptableAdapter has adapted it.
This makes for a more complicated object that implementations have to handle,
but implementations retain full control of the items list.
This is more conducive to integration with MergeAdapters and has a more natural 
flexibility to provide objects that don't need adapting. 

### Presenter
Takes the "Adaptable" framework further by having the ViewHolder factory and
Binder also be provided by the consumer.
This means the consumer is providing the entire Presenter, and makes it easy
for the BindingAdapter to support heterogeneous items; simply provide a
presenter for each item type. 


Framework Comparison
-------------------------------------------------------------------------------
### View Model
#### Simple framework
View Models are plain objects (no necessary interfaces or anything) that are 
output by `SimpleAdapter`s.
They are adapted from other plain objects ("Data Models").
When it comes time to bind, the View Model is `null` if it has not yet been 
adapted (in which case the View should show some sort of placeholder).

#### Adaptable framework and Presenter framework
The Data Model and View Model are provided together within `AdaptableViewModel` 
container objects.
The `AdaptableViewModel` given to the View is never `null`, but the View Model
within it will be if it has not yet been adapted.

### Adapter
#### Simple framework
A `SimpleAdapter` simply takes an object into its `adapt` method and outputs
a View Model (another object).

#### Adaptable framework  and Presenter framework
An `AdaptableAdapter` takes an AdaptableViewModel into its `adapt` method, and
outputs a View Model, which is saved back into the AdaptableViewModel.

### Binding Adapter
#### SimpleBindingAdapter
Takes plain "Data Model" objects in, and uses the provided `SimpleAdapter` to
adapt each into a View Model as necessary.
As an item comes into view, the View Model is provided to the implementation's
`onBindViewHolder`.
The ViewModel is `null` if it was not previously adapted, in which case the View
should show a placeholder instead of the content that would be shown for the
ViewModel.

This maintains the list of items, as it needs to maintain the pairing of View 
Models with each item.

#### AdaptableBindingAdapter
Takes `AdaptableViewModel` container objects, which pairs View Models with the
data from which it is adapted (Adaptable Data Model).
In contrast to the SimpleBindingAdapter (which maintains the container/pairing
internally), this allows the implementations to retain control of the items.

#### PresenterBindAdapter
For each supported type, an `AdaptableAdapter` (adapts `AdaptableViewModels`), 
`ViewHolderFactory`, and `Binder` (binds `AdaptableViewModels` to `ViewHolders`
— together a "Presenter" — are provided.
This makes it easy to support a heterogeneous list by simply providing a
Presenter for each type. 

### Adapt-on-Demand implementations of BindingAdapters
For a quick start, basic, abstract implementations of Binding Adapters are 
provide for each framework:
  * **Simple**: AdaptOnDemandSimpleBindingAdapter
  * **Adaptable**: AdaptOnDemandBindingAdapter

Each submits the item to be adapted if/when the item comes into view. 

Status
-------------------------------------------------------------------------------
### Release 0.2.0
The implementations are proof-of-concept, not yet suitable for practical use.

Future
-------------------------------------------------------------------------------
  * There are many things that can be incorporated into better BindingAdapters
    (future implementations, perhaps implemented by others):
      * Queue additional items from the next page so they're ready before being 
        scrolled onto screen 
      * Work on adapting all items from the start, not just on demand
      * Prevent duplicate adapting jobs
